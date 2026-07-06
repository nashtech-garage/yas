# Istio Service Mesh

This folder contains the Service Mesh configuration for YAS.

## Scope

- Enable automatic sidecar injection for `yas`, `yas-developer`, `dev`, and `staging`.
- Enforce namespace-level strict mTLS with `PeerAuthentication`.
- Keep public entrypoints permissive for NGINX Ingress compatibility while backend services stay strict.
- Inject the NGINX Ingress Controller so public traffic can reach mesh workloads safely.
- Use `ISTIO_MUTUAL` for in-mesh service traffic with `DestinationRule`.
- Add retry policies for `tax` and `order` with `VirtualService`.

## Install Istio

Install Istio with a cluster-admin kubeconfig:

```powershell
C:\tmp\istio-1.30.2\bin\istioctl.exe install --set profile=demo -y --kubeconfig ..\..\teammate-kubeconfig.yaml
```

Or on Linux/macOS:

```bash
istioctl install --set profile=demo -y
```

## Apply Mesh Configuration

```powershell
kubectl apply -f ..\namespaces.yaml --kubeconfig ..\..\teammate-kubeconfig.yaml
kubectl label namespace ingress-nginx istio-injection=enabled --overwrite --kubeconfig ..\..\teammate-kubeconfig.yaml
kubectl apply -f . --kubeconfig ..\..\teammate-kubeconfig.yaml
kubectl rollout restart deployment -n yas --kubeconfig ..\..\teammate-kubeconfig.yaml
kubectl rollout restart deployment ingress-nginx-controller -n ingress-nginx --kubeconfig ..\..\teammate-kubeconfig.yaml
```

## Verify

```powershell
kubectl get pods -n istio-system --kubeconfig ..\..\teammate-kubeconfig.yaml
kubectl get pods -n ingress-nginx --kubeconfig ..\..\teammate-kubeconfig.yaml
kubectl get peerauthentication -A --kubeconfig ..\..\teammate-kubeconfig.yaml
kubectl get destinationrule -A --kubeconfig ..\..\teammate-kubeconfig.yaml
kubectl get virtualservice -A --kubeconfig ..\..\teammate-kubeconfig.yaml
kubectl get pods -n yas -o jsonpath="{range .items[*]}{.metadata.name}{' '}{.spec.containers[*].name}{'\n'}{end}" --kubeconfig ..\..\teammate-kubeconfig.yaml
```

Each YAS application pod should include both the application container and `istio-proxy`.

The public entrypoints `storefront-bff`, `backoffice-bff`, and `swagger-ui` are set to `PERMISSIVE` in `yas` and `yas-developer` because the existing project exposes them through NGINX Ingress. Backend services remain covered by the namespace-level `STRICT` mTLS policy.
