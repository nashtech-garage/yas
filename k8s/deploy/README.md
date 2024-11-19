# YAS K8S Deployment
## Resource cluster installation reference
- **Postgresql:** https://github.com/zalando/postgres-operator
- **Elasticsearch:** https://github.com/elastic/cloud-on-k8s
- **Kafka:** https://github.com/strimzi/strimzi-kafka-operator
- **Debezium Connect:** https://debezium.io/documentation/reference/stable/operations/kubernetes.html
- **Keycloak:** https://www.keycloak.org/operator/installation
- **Redis:** https://artifacthub.io/packages/helm/bitnami/redis
- **Reloader:** https://github.com/stakater/Reloader
- **Prometheus:** https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack
- **Grafana:** https://github.com/grafana-operator/grafana-operator
- **Loki:** https://github.com/grafana/loki/tree/main/production/helm/loki
- **Tempo:** https://github.com/grafana/helm-charts/tree/main/charts/tempo
- **Promtail:** https://github.com/grafana/helm-charts/tree/main/charts/promtail
- **Opentelemetry:** https://github.com/open-telemetry/opentelemetry-operator
## Local installation steps
- Require a minikube node minimum 16G memory and 40G disk space and run on Ubuntu operator
```shell
minikube start --disk-size='40000mb' --memory='16g'
```
- Enable ingress addon
```shell
minikube addons enable ingress
```
- Install helm
  https://helm.sh/
- Install yq (the tool read, update yaml file)
  https://github.com/mikefarah/yq
- Goto `k8s-deployment` folder
- Execute [setup-keycloak.sh](setup-cluster.sh) to set up keycloak as the Identity and Access Management server.
```shell
./setup-keycloak.sh
```
- Execute [setup-redis.sh](setup-cluster.sh) to set up Redis as the server to store sessions for backends.
```shell
./setup-redis.sh
```
- Execute [setup-cluster.sh](setup-cluster.sh) to set up severs: `postgresql`, `elasticsearch`, `kafka`, `debezium connect`
```shell
./setup-cluster.sh
```
- Verify all servers run successful on namespaces: `postgres`, `elasticsearch`, `kafka`, `keycloak`
- After all above servers are running status, execute  [deploy-yas-applications.sh](deploy-yas-applications.sh) file to deploy all of yas applications to `yas` namespace
```shell
./deploy-yas-applications
```
All of YAS microservice deployed in `yas` namespace
- Setup hosts file
edit host file `/etc/hots`
```shell
192.168.49.2 pgoperator.yas.local.com
192.168.49.2 pgadmin.yas.local.com
192.168.49.2 akhq.yas.local.com
192.168.49.2 kibana.yas.local.com
192.168.49.2 identity.yas.local.com
192.168.49.2 backoffice.yas.local.com
192.168.49.2 storefront.yas.local.com
192.168.49.2 grafana.yas.local.com

```
`192.168.49.2` is ip of minikbe node use this command line to get the ip of minikube
```shell
minikube ip
```
## Keycloak bootstrap admin credentials
The username and password of Keycloak admin user store in the `keycloak-credentials` secret, `keycloak` namespace
use bellow command line to get the admin password
```shell
kubectl get secret keycloak-credentials -n keycloak -o jsonpath="{.data.password}" | base64 --decode
```
bootstrap admin is a temporary admin user. To harden security, create a permanent admin account and delete the temporary one.
## Cluster configuration
All configuration of cluster is setting on [cluster-config.yaml](cluster-config.yaml) in folder k8s-deploy

## Yas configuration 
All configurations of YAS application putted in the yas-configuration helm chart.

Bellow is the values of [values.yaml](../charts/yas-configuration/values.yaml)

## Yas helm charts
All charts of Yas application situated in `charts` folder

To Install the Yas helm charts access to [https://nashtech-garage.github.io/yas/](https://nashtech-garage.github.io/yas/)

## Observability
The Yas observability follow by the standard of Open Telemetry recommendation.
Promtail collect the log from all applications send to Open Telemetry Collector after that, Open Telemetry Collector distribute to Loki server.
The Yas applications also send the metric data to Open Telemetry Collector, Open Telemetry collector send the metric data to Tempo server

View details configuration of Open Telemetry Collector at [opentelemetry](./observability/opentelemetry/values.yaml)

### How to view log on the Grafana
On the left menu select `Expore` -> select `Loki` datasource -> select Label filters:
- namespace
- container (Application)

On the Loki also support track by traceId, on The Tempo you can select the Node graph to view the tracing of request 
