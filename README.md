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

1. Have your Docker desktop with Kubernetes enabled
1. Install NGINX ingress controller `kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.1.1/deploy/static/provider/cloud/deploy.yaml`
1. Add `127.0.0.1 yas.local` and  `127.0.0.1 identity.yas.local` to your host file
1. Run `kubectl apply yaslocal.yaml`
1. Open your favorite browser and go to http://identity.yas.local


## Components roadmap
- [ ] Identity service
- [ ] Product service
- [ ] Pricing service
- [ ] Cart service
- [ ] Order service
- [ ] Review service
