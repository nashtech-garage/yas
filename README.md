# YAS: Yet Another Shop

YAS is a pet project aim to practice building a typical microservice application in Java

[![storefront-ci](https://github.com/nashtech-garage/yas/actions/workflows/storefront-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/storefront-ci.yaml)
[![product-ci](https://github.com/nashtech-garage/yas/actions/workflows/product-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/product-ci.yaml)
[![media-ci](https://github.com/nashtech-garage/yas/actions/workflows/media-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/media-ci.yaml)

## Tentative technologies and frameworks

- Java 17
- Spring boot 2.7
- Next.js
- Keycloak
- Kafka
- K8s
- Github Actions
- SonarCloud
- OpenTelemetry
- Jaeger
- Quarkus
- GraalVM

## Local development architecture

![Yas - local development architecture](https://raw.githubusercontent.com/nashtech-garage/yas/main/yas_architecture_local.png)

## Getting started

1. Get the latest source code
1. Have your Docker desktop with Kubernetes enabled
1. Install NGINX ingress controller `kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.1.1/deploy/static/provider/cloud/deploy.yaml`
1. In C: driver, create 2 folders `k8s-yas-data/postgres` and `k8s-yas-data/pgadmin`
1. Run `kubectl apply -f yaslocal.yaml`
1. Add `127.0.0.1 identity`, `127.0.0.1 api.yas.local`, `127.0.0.1 storefront.yas.local`, `127.0.0.1 backoffice.yas.local`, `127.0.0.1 pgadmin.yas.local` and `127.0.0.1 jaeger` to your host file
1. Open your favorite browser and go to  http://pgadmin.yas.local. Account login: admin@yas.com / admin. Register a server: localhost, port default, username admin, password admin. Then create a database name `keycloak`, `media`, `product`
1. Open another browser tab and go to http://identity. Admin account is: admin/admin. Add Realm, choose Import, select file `/identity/realm-export.json`. If you already have the realm Yas, the go to that realm then Import
1. The Postgresql server is also published to the host machine: servername: localhost, port: 30007, username: admin, password: admin

## [Getting started with Minikube in Ubuntu](https://github.com/nashtech-garage/yas/wiki/Getting-started-with-Minikube-in-Ubuntu)

## Components roadmap
- [ ] Identity service
- [ ] Product service
- [ ] Pricing service
- [ ] Cart service
- [ ] Order service
- [ ] Review service
