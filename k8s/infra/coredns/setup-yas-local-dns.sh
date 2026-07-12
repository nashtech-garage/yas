#!/usr/bin/env bash
set -euo pipefail

COREDNS_NS="kube-system"
COREDNS_CM="coredns"
INGRESS_NS="ingress-nginx"
INGRESS_SVC="ingress-nginx-controller"
BACKUP_FILE="$HOME/coredns-backup-$(date +%Y%m%d-%H%M%S).yaml"

echo "=== YAS CoreDNS Setup ==="

INGRESS_IP="$(kubectl get svc -n "$INGRESS_NS" "$INGRESS_SVC" -o jsonpath='{.spec.clusterIP}')"

if [ -z "$INGRESS_IP" ]; then
  echo "ERROR: Cannot get ingress-nginx-controller ClusterIP"
  exit 1
fi

echo "Ingress ClusterIP: $INGRESS_IP"

kubectl get configmap "$COREDNS_CM" -n "$COREDNS_NS" -o yaml > "$BACKUP_FILE"
echo "Backup saved to: $BACKUP_FILE"

TMP_CORE="$(mktemp)"
TMP_NEW="$(mktemp)"

kubectl get configmap "$COREDNS_CM" -n "$COREDNS_NS" -o jsonpath='{.data.Corefile}' > "$TMP_CORE"

python3 - "$TMP_CORE" "$TMP_NEW" "$INGRESS_IP" <<'PY'
import re
import sys

src, dst, ingress_ip = sys.argv[1], sys.argv[2], sys.argv[3]

yas_domains = [
    "identity.yas.local.com",
    "storefront.dev.yas.local.com",
    "api.dev.yas.local.com",
    "storefront.staging.yas.local.com",
    "api.staging.yas.local.com",
]

with open(src, "r", encoding="utf-8") as f:
    lines = f.read().splitlines()

# remove old YAS records
lines = [line for line in lines if "yas.local.com" not in line]

def is_hosts_start(line):
    return re.match(r"^\s*hosts(?:\s+[^{}]+)?\s*\{\s*$", line) is not None

def leading(line):
    return re.match(r"^(\s*)", line).group(1)

def find_block_end(start):
    depth = 0
    for i in range(start, len(lines)):
        depth += lines[i].count("{")
        depth -= lines[i].count("}")
        if depth == 0:
            return i
    raise RuntimeError("hosts block is not closed")

hosts_blocks = []
i = 0
while i < len(lines):
    if is_hosts_start(lines[i]):
        end = find_block_end(i)
        hosts_blocks.append((i, end))
        i = end + 1
    else:
        i += 1

records = [f"{ingress_ip} {d}" for d in yas_domains]

if hosts_blocks:
    start, end = hosts_blocks[0]
    start_line = lines[start]
    end_line = lines[end]
    base_indent = leading(start_line)
    inner_indent = base_indent + "   "

    body = []
    seen = set()

    for s, e in hosts_blocks:
        for line in lines[s+1:e]:
            stripped = line.strip()
            if not stripped or stripped == "fallthrough" or "yas.local.com" in stripped:
                continue
            if stripped not in seen:
                seen.add(stripped)
                body.append(line)

    new_block = [start_line] + body
    for r in records:
        new_block.append(inner_indent + r)
    new_block.append(inner_indent + "fallthrough")
    new_block.append(end_line)

    out = []
    done = False
    block_starts = {s: e for s, e in hosts_blocks}
    i = 0
    while i < len(lines):
        if i in block_starts:
            if not done:
                out.extend(new_block)
                done = True
            i = block_starts[i] + 1
        else:
            out.append(lines[i])
            i += 1
else:
    insert_at = None
    for i, line in enumerate(lines):
        if re.match(r"^\s*forward\s+", line):
            insert_at = i
            break

    if insert_at is None:
        raise RuntimeError("Cannot find forward directive")

    base_indent = leading(lines[insert_at])
    inner_indent = base_indent + "   "

    new_block = [base_indent + "hosts {"]
    for r in records:
        new_block.append(inner_indent + r)
    new_block.append(inner_indent + "fallthrough")
    new_block.append(base_indent + "}")

    out = lines[:insert_at] + new_block + [""] + lines[insert_at:]

count = sum(1 for line in out if is_hosts_start(line))
if count != 1:
    raise RuntimeError(f"Expected exactly 1 hosts block, got {count}")

with open(dst, "w", encoding="utf-8") as f:
    f.write("\n".join(out) + "\n")
PY

kubectl create configmap "$COREDNS_CM" \
  -n "$COREDNS_NS" \
  --from-file=Corefile="$TMP_NEW" \
  -o yaml \
  --dry-run=client | kubectl apply -f -

rm -f "$TMP_CORE" "$TMP_NEW"

kubectl rollout restart deployment coredns -n kube-system
kubectl rollout status deployment coredns -n kube-system --timeout=180s

echo "CoreDNS patched successfully."
echo "Test:"
echo "kubectl run dns-test -n yas-dev --rm -it --restart=Never --image=busybox:1.36 -- nslookup identity.yas.local.com"
