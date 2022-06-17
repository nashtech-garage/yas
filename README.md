# YAS: Yet Another Shop

YAS is a pet project aim to practice building a typical microservice application in Java

## Intended technologies and frameworks

- Java 17
- Spring boot 2.6
- Next.js
- Keycloak
- Kafka
- K8s
- Github Action
- SonarCloud
- OpenTelemetry

## Getting started

1. Get the latest source code
1. Have your Docker desktop with Kubernetes enabled
1. Install NGINX ingress controller `kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.1.1/deploy/static/provider/cloud/deploy.yaml`
1. In C: driver, create 2 folders `k8s-yas-data/postgres` and `k8s-yas-data/pgadmin`
1. Run `kubectl apply -f yaslocal.yaml`
1. Add `127.0.0.1 yas.local`,   `127.0.0.1 identity.yas.local` and `127.0.0.1 pgadmin.yas.local` to your host file
1. Open your favorite browser and go to  http://pgadmin.yas.local. Account login: admin@yas.com / admin. Register a server: localhost, port default, username admin, password admin. Then create a database name keycloak
1. Open another browser tab and go to http://identity.yas.local. Admin account is: admin/admin
1. The Postgresql server is also published to the host machine: servername: localhost, port: 30007, username: admin, password: admin


## Components roadmap
- [ ] Identity service
- [ ] Product service
- [ ] Pricing service
- [ ] Cart service
- [ ] Order service
- [ ] Review service
