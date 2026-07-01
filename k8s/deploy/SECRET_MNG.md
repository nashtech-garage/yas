## 1. CURRENT STATE (Problems)

### 1.1 Secrets in plain text in git

| File | Secret type | Value |
|------|-------------|-------|
| `k8s/charts/yas-configuration/values.yaml` | PostgreSQL password | `admin` |
| `k8s/charts/yas-configuration/values.yaml` | ES password | `changeme` |
| `k8s/charts/yas-configuration/values.yaml` | Keycloak client secrets | `TVacLC0cQ8tiiEKiTVerTb2YvwQ1TRJF` |
| `k8s/charts/yas-configuration/values.yaml` | Redis password | `redis` |
| `k8s/charts/yas-configuration/values.yaml` | OpenAI API key | `update-me` |
| `k8s/deploy/cluster-config-dev.yaml` | PostgreSQL password | `admin` |
| `k8s/deploy/cluster-config-dev.yaml` | ES password | `LarUmB3A49NTg9YmgW4=` |
| `k8s/deploy/cluster-config-staging.yaml` | Same as above | Same as above |
| `k8s/deploy/cluster-config-production.yaml` | Same as above | Same as above |
| `k8s/deploy/keycloak/keycloak/values.yaml` | PostgreSQL password | `admin` |
| `k8s/deploy/keycloak/keycloak/values.yaml` | Bootstrap admin | `admin/admin` |
| `k8s/deploy/postgres/postgresql/values.yaml` | PostgreSQL password | `admin` |
| `k8s/deploy/kafka/kafka-cluster/values.yaml` | PostgreSQL password | `admin` |

### 1.2 Gitleaks does NOT scan k8s/

Current CI (`ci.yml`):
- Root scan: only copies files at repo root (`find . -maxdepth 1`)
- Per-service scan: scans `cart/`, `product/`, etc. — never `k8s/`

`gitleaks.toml` allowlist excludes `test-realm.json`, `realm-export`, `keycloak-yas-realm-import.yaml`, `target` — but **does not protect `cluster-config-*.yaml`**.

### 1.3 Risk

- Anyone with repo access sees all credentials.
- Accidental commit of real secrets to `k8s/` goes undetected by CI.
- Production ES password (`LarUmB3A49NTg9YmgW4=`) is in git history forever unless rewritten.

---

## 2. TARGET STATE (Sealed Secrets)

### 2.1 What is Sealed Secrets?

[Bitnami Sealed Secrets](https://github.com/bitnami-labs/sealed-secrets) encrypts a Kubernetes Secret into a `SealedSecret` CRD using the cluster's public key. Only the **Sealed Secrets controller running in the target cluster** can decrypt it.

```
Developer machine                        K8s Cluster
┌─────────────────┐                      ┌──────────────────────┐
│ plain Secret    │  → kubeseal --cert   │ SealedSecret CRD     │
│ (yaml)          │    cluster-public.key│   (safe in git)      │
└─────────────────┘                      └──────────────────────┘
                                               ↓
                                        Sealed Secrets Controller
                                               ↓
                                        Decrypts → creates real Secret
```

### 2.2 Key properties

| Property | Behavior |
|----------|----------|
| **One-way encryption** | `kubeseal` encrypts with cluster public key. Cannot decrypt without controller private key. |
| **Scope: strict** (default) | SealedSecret is bound to `namespace` + `name`. Cannot be applied to different namespace/name. |
| **Scope: namespace-wide** | Bound to namespace only. Same name in different namespace won't work. |
| **Scope: cluster-wide** | Can be decrypted in any namespace (less secure). |
| **Tamper-proof** | Changing any field invalidates the sealed secret. |

### 2.3 Architecture after migration

```
Git Repository (safe to share publicly)
├── k8s/
│   ├── sealed-secrets/                  # SealedSecret manifests
│   │   ├── dev/
│   │   │   ├── yas-credentials.yaml      # SealedSecret (encrypted)
│   │   │   ├── postgresql-credentials.yaml
│   │   │   ├── elasticsearch-credentials.yaml
│   │   │   └── keycloak-credentials.yaml
│   │   ├── staging/
│   │   └── production/
│   └── deploy/
│       ├── cluster-config-dev.yaml       # NO credentials (plain config only)
│       ├── cluster-config-staging.yaml   # NO credentials
│       └── cluster-config-production.yaml # NO credentials
│
K8s Cluster (private key never leaves)
├── Namespace: kube-system
│   └── sealed-secrets-controller (Helm)
│       └── Private RSA key (in Secret sealed-secrets-key)
└── Per-env namespaces
    └── SealedSecret CRDs → auto-decrypt → real Secrets
```

---

## 3. IMPLEMENTATION PLAN

### Phase 1: Install Sealed Secrets Controller

```bash
# One-time per cluster (cluster-scoped, not per env)
helm repo add sealed-secrets https://bitnami-labs.github.io/sealed-secrets
helm repo update

helm upgrade --install sealed-secrets sealed-secrets/sealed-secrets \
  --namespace kube-system \
  --set keyRenewPeriod=0      # Disable auto key rotation (manual control)

# Verify controller running
kubectl get pods -n kube-system -l app.kubernetes.io/name=sealed-secrets
```

**Note:** Controller runs in `kube-system`, manages SealedSecrets for **all namespaces**.

### Phase 2: Export Public Key (for developers)

```bash
# Export public key (safe to share, used for kubeseal encryption)
kubeseal --fetch-cert \
  --controller-namespace=kube-system \
  --controller-name=sealed-secrets \
  > sealed-secrets-public.key

# Commit public key to repo (it is NOT a secret)
git add sealed-secrets-public.key
git commit -m "feat: add sealed-secrets public key for encryption"
```

### Phase 3: Remove Plain-Text Secrets from Values Files

#### 3.1 `k8s/charts/yas-configuration/values.yaml`

**Before:**
```yaml
credentials:
  postgresql:
    username: yasadminuser
    password: admin
  elasticsearch:
    username: elastic
    password: changeme
  keycloak:
    backofficeBffClientSecret: TVacLC0cQ8tiiEKiTVerTb2YvwQ1TRJF
    storefrontBffClientSecret: ZrU9I0q2uXBglBnmvyJdkl1lf0ncr8tn
    customerManagementClientSecret: NKAr3rnjwm9jlakgKpelukZGFaHYqIWE
  redis:
    password: redis
  openai:
    apiKey: update-me
```

**After:**
```yaml
# Credentials are now managed via SealedSecrets in k8s/sealed-secrets/
# This file only contains non-secret configuration.
# DO NOT add passwords here.
```

**Delete the `yas-credentials.secret.yaml` template** from `k8s/charts/yas-configuration/templates/`. The SealedSecret will create the real Secret directly.

#### 3.2 `k8s/deploy/cluster-config-*.yaml`

**Before:**
```yaml
postgresql:
  password: admin
elasticsearch:
  password: LarUmB3A49NTg9YmgW4=
```

**After:**
```yaml
# Credentials removed — managed by SealedSecrets
# If you need to override DB username, do it here (not password)
postgresql:
  username: yasadminuser
  # password: managed by SealedSecret
```

#### 3.3 `k8s/deploy/keycloak/keycloak/values.yaml`

Remove plain-text `password: admin`. The Keycloak Helm chart will reference a SealedSecret instead.

### Phase 4: Create SealedSecrets (per env)

#### Step 4.1: Prepare plain Secret (temporary, local only)

Create a file `temp-secret.yaml` on your local machine (NEVER commit this):

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: yas-credentials-secret
  namespace: dev        # Must match target namespace
type: Opaque
stringData:
  POSTGRESQL_USERNAME: yasadminuser
  POSTGRESQL_PASSWORD: admin
  ELASTICSEARCH_USERNAME: elastic
  ELASTICSEARCH_PASSWORD: LarUmB3A49NTg9YmgW4=
  KEYCLOAK_BACKOFFICE_BFF_CLIENT_SECRET: TVacLC0cQ8tiiEKiTVerTb2YvwQ1TRJF
  KEYCLOAK_STOREFRONT_BFF_CLIENT_SECRET: ZrU9I0q2uXBglBnmvyJdkl1lf0ncr8tn
  KEYCLOAK_CUSTOMER_MANAGEMENT_CLIENT_SECRET: NKAr3rnjwm9jlakgKpelukZGFaHYqIWE
  REDIS_PASSWORD: redis
  OPENAI_API_KEY: sk-xxxxxxxx
```

#### Step 4.2: Encrypt with kubeseal

```bash
# Requires kubeseal CLI installed locally
# https://github.com/bitnami-labs/sealed-secrets/releases

kubeseal --cert=sealed-secrets-public.key \
  --format=yaml \
  --scope=strict \
  -f temp-secret.yaml \
  -w k8s/sealed-secrets/dev/yas-credentials.yaml

# Verify the output is encrypted
cat k8s/sealed-secrets/dev/yas-credentials.yaml
# Should show: apiVersion: bitnami.com/v1alpha1, kind: SealedSecret, encryptedData: {...}
```

#### Step 4.3: Delete temp file

```bash
shred -u temp-secret.yaml  # Secure delete
```

#### Step 4.4: Repeat for staging and production

```bash
# Staging — change namespace in temp-secret.yaml to "staging"
kubeseal --cert=sealed-secrets-public.key --scope=strict \
  -f temp-secret-staging.yaml \
  -w k8s/sealed-secrets/staging/yas-credentials.yaml

# Production — change namespace to "production"
kubeseal --cert=sealed-secrets-public.key --scope=strict \
  -f temp-secret-production.yaml \
  -w k8s/sealed-secrets/production/yas-credentials.yaml
```

**Critical:** Each environment gets a **different encrypted blob** because `scope=strict` binds to namespace + name.

#### Step 4.5: Infrastructure secrets

Also seal secrets for PostgreSQL, Keycloak, Elasticsearch operators:

```bash
# PostgreSQL credentials (used by Zalando operator)
kubeseal --cert=sealed-secrets-public.key --scope=strict \
  -f temp-postgres-secret.yaml \
  -w k8s/sealed-secrets/dev/postgresql-credentials.yaml

# Keycloak realm import credentials
kubeseal --cert=sealed-secrets-public.key --scope=strict \
  -f temp-keycloak-secret.yaml \
  -w k8s/sealed-secrets/dev/keycloak-credentials.yaml

# Elasticsearch credentials
kubeseal --cert=sealed-secrets-public.key --scope=strict \
  -f temp-es-secret.yaml \
  -w k8s/sealed-secrets/dev/elasticsearch-credentials.yaml
```

### Phase 5: GitHub Actions — Scan k8s/ with Gitleaks

#### 5.1 Update `gitleaks.toml`

Remove the implicit trust of `k8s/` by explicitly including it in scan paths:

```toml
# gitleaks.toml
[extend]
useDefault = true

[allowlist]
description = "global allow list"
paths = [
  '''test-realm.json''',
  '''realm-export''',
  '''keycloak-yas-realm-import.yaml''',
  '''target''',
  '''sealed-secrets-public.key''',   # Public key is safe
  '''\.git''',
]

# Add a rule to detect unencrypted secrets in k8s/
[[rules]]
description = "Detect unencrypted secrets in k8s/"
id = "k8s-plain-secret"
path = '''k8s/.*\.yaml'''
regex = '''password\s*:\s*[^\s{].*'''
severity = "error"
```

**Wait — this rule would match SealedSecrets too!** Better approach:

```toml
[extend]
useDefault = true

[allowlist]
description = "global allow list"
paths = [
  '''test-realm.json''',
  '''realm-export''',
  '''keycloak-yas-realm-import.yaml''',
  '''target''',
  '''sealed-secrets-public.key''',
]

# Allow SealedSecret files (they are encrypted, safe in git)
[[allowlist]]
description = "SealedSecrets are encrypted"
paths = [
  '''k8s/sealed-secrets/.*\.yaml''',
]
```

#### 5.2 Update `ci.yml` to scan k8s/

Add a dedicated job:

```yaml
  # Scan k8s/ directory for plain-text secrets
  gitleaks-scan-k8s:
    name: Gitleaks Scan k8s/
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Source Code
        uses: actions/checkout@v4

      - name: Run Gitleaks on k8s/
        uses: ./.github/workflows/actions/gitleaks-scan
        with:
          source_path: k8s/
          config_path: gitleaks.toml

      - name: Upload SARIF to GitHub Security
        if: always()
        uses: github/codeql-action/upload-sarif@v4
        with:
          sarif_file: gitleaks-results.sarif
          category: gitleaks-k8s
```

Update `ci-gate` needs:
```yaml
needs: [filter-changes, gitleaks-scan-root, gitleaks-scan-k8s, services-ci, services-ci-node]
```

### Phase 6: ArgoCD Integration

SealedSecrets are standard Kubernetes CRDs. ArgoCD syncs them like any other resource.

#### Option A: SealedSecrets in existing ApplicationSet

Add `sync-wave: -3` (before `yas-configuration` wave -1):

```yaml
# k8s/argocd/applicationsets/yas-configuration-applicationset.yaml
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
metadata:
  name: yas-configuration
spec:
  generators:
    - list:
        elements:
          - env: "dev"
            # ... existing fields ...
            syncWave: "-1"
  template:
    metadata:
      name: "yas-configuration-{{env}}"
      annotations:
        argocd.argoproj.io/sync-wave: "{{syncWave}}"
    spec:
      source:
        repoURL: https://github.com/23120091/yas
        targetRevision: main
        path: k8s/charts/yas-configuration
        # ...
      # ADD: SealedSecrets as additional resources
      # Option 1: Include SealedSecrets in the same Helm chart
      # Option 2: Separate Application for SealedSecrets
```

#### Option B: Separate SealedSecrets Application (Recommended)

```yaml
# k8s/argocd/applicationsets/sealed-secrets-applicationset.yaml
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
metadata:
  name: sealed-secrets-config
  namespace: argocd
spec:
  generators:
    - list:
        elements:
          - {env: "dev", namespace: "dev", syncWave: "-3"}
          - {env: "staging", namespace: "staging", syncWave: "-3"}
          - {env: "production", namespace: "production", syncWave: "-3"}
  template:
    metadata:
      name: "sealed-secrets-{{env}}"
      labels:
        env: "{{env}}"
      annotations:
        argocd.argoproj.io/sync-wave: "{{syncWave}}"
    spec:
      project: yas
      source:
        repoURL: https://github.com/23120091/yas
        targetRevision: main
        path: "k8s/sealed-secrets/{{env}}"
      destination:
        server: https://kubernetes.default.svc
        namespace: "{{namespace}}"
      syncPolicy:
        automated:
          prune: true
          selfHeal: true
        syncOptions:
          - CreateNamespace=true
```

**Sync wave order:**
- Wave -3: `sealed-secrets-{env}` → creates real Secrets
- Wave -1: `yas-configuration-{env}` → ConfigMaps (reference Secrets)
- Wave 1+: Application services → consume Secrets via `envFrom.secretRef`

### Phase 7: Update Helm Charts to NOT Create Secrets

#### 7.1 Remove `yas-credentials.secret.yaml`

```bash
rm k8s/charts/yas-configuration/templates/yas-credentials.secret.yaml
```

#### 7.2 Update `backend/templates/deployment.yaml`

Ensure `envFrom` references existing Secrets (created by SealedSecrets):

```yaml
envFrom:
  - secretRef:
      name: yas-postgresql-credentials-secret   # Created by SealedSecret
  - secretRef:
      name: yas-elasticsearch-credentials-secret # Created by SealedSecret
```

This is already the case in current code. No change needed.

#### 7.3 Update Keycloak chart

Remove `postgresql-credential.secret.yaml` template. Instead, reference a pre-existing Secret created by SealedSecrets.

---

## 4. DEPLOY WORKFLOW: EMPTY SERVER

Scenario: Brand new cluster, nothing installed.

```bash
# Step 1: Bootstrap cluster (infra)
cd ~/yas/k8s/deploy
./setup-all.sh dev --teardown   # or without --teardown for fresh cluster

# Step 2: Install Sealed Secrets controller (cluster-scoped, one-time)
helm upgrade --install sealed-secrets sealed-secrets/sealed-secrets \
  --namespace kube-system

# Step 3: Verify controller is ready
kubectl wait --for=condition=ready pod \
  -l app.kubernetes.io/name=sealed-secrets \
  -n kube-system --timeout=120s

# Step 4: Export public key (developer local step — done once per cluster)
kubeseal --fetch-cert --controller-namespace=kube-system > sealed-secrets-public.key

# Step 5: ArgoCD auto-syncs everything
#   - Wave -3: SealedSecrets → decrypts to real Secrets
#   - Wave -1: ConfigMaps
#   - Wave 1+: Applications

# Step 6: Verify secrets exist
kubectl get secrets -n dev | grep yas-
```

**No manual `kubectl create secret` needed.**

---

## 5. UPDATE WORKFLOW: CHANGE EXISTING SECRET

Scenario: Rotate PostgreSQL password in `dev`.

### Method A: Re-seal with new value (Recommended)

```bash
# On developer machine with kubectl access to cluster

# 1. Create new plain Secret locally (NEVER commit)
cat > temp-pg-secret.yaml <<EOF
apiVersion: v1
kind: Secret
metadata:
  name: yas-postgresql-credentials-secret
  namespace: dev
type: Opaque
stringData:
  POSTGRESQL_USERNAME: yasadminuser
  POSTGRESQL_PASSWORD: newpassword123
EOF

# 2. Re-encrypt with kubeseal
kubeseal --cert=sealed-secrets-public.key \
  --format=yaml \
  --scope=strict \
  -f temp-pg-secret.yaml \
  -w k8s/sealed-secrets/dev/yas-credentials.yaml

# 3. Secure delete temp file
shred -u temp-pg-secret.yaml

# 4. Commit the updated SealedSecret
git add k8s/sealed-secrets/dev/yas-credentials.yaml
git commit -m "security: rotate PostgreSQL password for dev"
git push origin dev

# 5. ArgoCD auto-syncs → SealedSecret controller recreates real Secret
# 6. Stakater Reloader auto-restarts pods referencing the Secret
```

### Method B: Direct cluster edit (Emergency only, NOT GitOps)

```bash
# Edit Secret directly in cluster (won't be in git — anti-pattern)
kubectl patch secret yas-postgresql-credentials-secret -n dev \
  --type='json' -p='[{"op": "replace", "path": "/data/POSTGRESQL_PASSWORD", "value":"'$(echo -n "newpassword" | base64 -w 0)'"}]'

# Restart affected pods
kubectl rollout restart deployment -n dev --all
```

**Do not use Method B** unless SealedSecrets controller is down.

---

## 6. FILE CHANGES SUMMARY

### Deleted
```
k8s/charts/yas-configuration/templates/yas-credentials.secret.yaml
k8s/deploy/keycloak/keycloak/templates/postgresql-credential.secret.yaml
k8s/deploy/elasticsearch/elasticsearch-cluster/templates/user-credentials.secret.yaml
# ... other *-credential.secret.yaml templates that create Secrets from plain values
```

### Modified (remove secrets)
```
k8s/charts/yas-configuration/values.yaml           # Remove credentials: block
k8s/deploy/cluster-config-dev.yaml               # Remove password fields
k8s/deploy/cluster-config-staging.yaml           # Remove password fields
k8s/deploy/cluster-config-production.yaml        # Remove password fields
k8s/deploy/keycloak/keycloak/values.yaml         # Remove password fields
k8s/deploy/postgres/postgresql/values.yaml        # Remove password fields
k8s/deploy/kafka/kafka-cluster/values.yaml        # Remove password fields
```

### New files
```
sealed-secrets-public.key                        # Cluster public key (safe in git)
k8s/sealed-secrets/
├── dev/
│   ├── yas-credentials.yaml                     # SealedSecret
│   ├── postgresql-credentials.yaml              # SealedSecret
│   ├── elasticsearch-credentials.yaml           # SealedSecret
│   └── keycloak-credentials.yaml                # SealedSecret
├── staging/
│   └── ...
└── production/
    └── ...
k8s/argocd/applicationsets/sealed-secrets-applicationset.yaml
.github/workflows/ci.yml                         # Add gitleaks-scan-k8s job
gitleaks.toml                                    # Add k8s/ allowlist rules
```

---

## 7. DECISION LOG

| Question | Decision | Reason |
|----------|----------|--------|
| Sealed Secrets vs External Secrets Operator? | **Sealed Secrets** | Simpler, no cloud dependency (AWS/GCP/Azure), fits homelab/K3s |
| `scope=strict` vs `namespace-wide`? | **Strict** | Higher security, prevents accidental cross-namespace decryption |
| Separate ApplicationSet for secrets? | **Yes** | Sync-wave control: Secrets must exist before ConfigMaps |
| Key rotation policy? | **Manual** | `keyRenewPeriod=0`. Rotate via `kubeseal --rotate` when needed |
| What about `cluster-config-*.yaml`? | **Remove credentials**, keep plain config | cluster-config drives Helm values, but secrets now come from SealedSecrets |
| Git history rewriting? | **Recommended** | Use `git-filter-repo` to remove old passwords from history after migration |

---

## 8. RISK & MITIGATION

| Risk | Impact | Mitigation |
|------|--------|------------|
| Lose cluster private key | Cannot decrypt any SealedSecret | Backup `sealed-secrets-key` Secret in kube-system to encrypted offsite storage |
| Accidentally commit plain Secret | Secret exposed in git | Gitleaks scan k8s/ + pre-commit hook (`detect-secrets` or `talisman`) |
| Developer doesn't have `kubeseal` CLI | Cannot create/update SealedSecrets | Document install: `brew install kubeseal` / download release binary |
| SealedSecrets controller down | New secrets not decrypted, existing ones still work (stored in etcd) | Controller HA: 2+ replicas |
| Keycloak realm JSON has passwords | Realm import YAML contains PBKDF2 hashes | Keep in allowlist — these are hashes, not reversible |

---

## 9. PRE-COMMIT HOOK (Optional but recommended)

Prevent accidental commit of plain Secrets:

```bash
# .git/hooks/pre-commit (or use pre-commit framework)
#!/bin/bash
if git diff --cached --name-only | grep -qE "k8s/.*secret.*\.yaml"; then
  echo "ERROR: Do not commit plain Secret files. Use kubeseal to encrypt first."
  exit 1
fi
```

Better: Use [detect-secrets](https://github.com/Yelp/detect-secrets) pre-commit hook.

---

## 10. VERIFY POST-MIGRATION

```bash
# 1. Verify no plain secrets in k8s/ (except SealedSecrets and public key)
gitleaks detect --source k8s/ --config gitleaks.toml --verbose --exit-code=2

# 2. Verify SealedSecrets are valid
kubectl get sealedsecrets -n dev
# Should show: yas-credentials, postgresql-credentials, ...

# 3. Verify real Secrets were created
kubectl get secrets -n dev | grep yas-

# 4. Verify pod can read secrets
kubectl exec -n dev deployment/product -- env | grep POSTGRESQL

# 5. Verify changing SealedSecret triggers restart
# Edit SealedSecret in git → ArgoCD syncs → Reloader restarts pod
```

---

## 11. TASKS (TODO)

- [ ] Install Sealed Secrets controller on cluster (`setup-cluster.sh` step)
- [ ] Export and commit `sealed-secrets-public.key`
- [ ] Remove plain-text credentials from `values.yaml` and `cluster-config-*.yaml`
- [ ] Delete Secret templates from Helm charts
- [ ] Create `k8s/sealed-secrets/{dev,staging,production}/` directories
- [ ] Encrypt all secrets with `kubeseal` for each env
- [ ] Create `sealed-secrets-applicationset.yaml` for ArgoCD (sync-wave: -3)
- [ ] Update `yas-configuration-applicationset.yaml` dependencies
- [ ] Update `ci.yml` with `gitleaks-scan-k8s` job
- [ ] Update `gitleaks.toml` allowlist for SealedSecrets
- [ ] Test deploy on empty namespace
- [ ] Test secret rotation workflow
- [ ] Document `kubeseal` install for team
- [ ] (Optional) Rewrite git history to remove old passwords (`git-filter-repo`)

---

**END OF PLAN**
