# GitOps Desired State Moved

Canonical GitOps desired state now lives in the CD repository:

```text
git@github.com:emanhthangngot/yas-cd.git
```

Use the CD repo for:

- `base/**`
- `overlays/**`
- `argocd/**`
- Kustomize render validation
- ArgoCD sync source

This app repo owns application source, CI gates, Docker image build/push, and the Jenkins logic
that updates the CD repo. Do not reintroduce active Kubernetes desired state here.
