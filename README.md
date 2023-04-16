# YAS: Yet Another Shop

YAS is a pet project aim to practice building a typical microservice application in Java

[![storefront-ci](https://github.com/nashtech-garage/yas/actions/workflows/storefront-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/storefront-ci.yaml)
[![storefront-bff-ci](https://github.com/nashtech-garage/yas/actions/workflows/storefront-bff-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/storefront-bff-ci.yaml)
[![backoffice-ci](https://github.com/nashtech-garage/yas/actions/workflows/backoffice-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/backoffice-ci.yaml)
[![backoffice-bff-ci](https://github.com/nashtech-garage/yas/actions/workflows/backoffice-bff-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/backoffice-bff-ci.yaml)
[![product-ci](https://github.com/nashtech-garage/yas/actions/workflows/product-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/product-ci.yaml)
[![media-ci](https://github.com/nashtech-garage/yas/actions/workflows/media-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/media-ci.yaml)
[![cart-ci](https://github.com/nashtech-garage/yas/actions/workflows/cart-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/cart-ci.yaml)
[![customer-ci](https://github.com/nashtech-garage/yas/actions/workflows/customer-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/customer-ci.yaml)
[![rating-ci](https://github.com/nashtech-garage/yas/actions/workflows/rating-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/rating-ci.yaml)
[![location-ci](https://github.com/nashtech-garage/yas/actions/workflows/location-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/location-ci.yaml)
[![order-ci](https://github.com/nashtech-garage/yas/actions/workflows/order-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/order-ci.yaml)
[![inventory-ci](https://github.com/nashtech-garage/yas/actions/workflows/inventory-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/inventory-ci.yaml)
[![tax-ci](https://github.com/nashtech-garage/yas/actions/workflows/tax-ci.yaml/badge.svg)](https://github.com/nashtech-garage/yas/actions/workflows/tax-ci.yaml)

## Tentative technologies and frameworks

- Java 17
- Spring boot 3
- Next.js
- Keycloak
- Kafka
- Elasticsearch
- K8s
- Github Actions
- SonarCloud
- OpenTelemetry
- Grafana, Loki, Prometheus, Tempo

## Local development architecture

![Yas - local development architecture](https://raw.githubusercontent.com/nashtech-garage/yas/main/yas-architecture-local.png)

## Getting started with Docker Compose

1. Get the latest source code
1. Add the following records to your host file: 
```
127.0.0.1 identity
127.0.0.1 api.yas.local
127.0.0.1 pgadmin.yas.local
127.0.0.1 storefront
127.0.0.1 backoffice
127.0.0.1 loki
127.0.0.1 tempo
127.0.0.1 grafana
127.0.0.1 elasticsearch
127.0.0.1 kafka
```
1. Open terminal of your choice, go to `yas` directory and run `docker compose up` and wait for all the containers up and running
2. All the containers up and running then we start source connectors by run script, open any terminal window... go to the YAS root folder and type: ./start-source-connectors.sh
3. Open your browser, now you can access the websites via `http://storefront/`; `http://backoffice/` login with admin/password

You might aslo want to explore:
1. `http://pgadmin.yas.local/`. Account login: `admin@yas.com` / admin. Register a server: postgres, port 5432, username admin, password admin. The Postgresql server is also exposed to the host machine: servername: localhost, port: 5432, username: admin, password: admin
1. `http://api.yas.local/swagger-ui/` for all the REST API document of all the services
1. `http://identity/` for Keycloak console, account admin/admin
1. `http://grafana/` for observability: log, trace, matrix
1. `http://elasticsearch/` for calling Elasticsearch APIs

## Contributing
- Give us a star
- Reporting a bug
- Participate discussions
- Propose new features
- Submit pull requests. If you new to GitHub, consider to [learn how to contribute to a project through forking](https://docs.github.com/en/get-started/quickstart/contributing-to-projects)
- [Developer guidelines](https://github.com/nashtech-garage/yas/wiki/Developer-guidelines)

By contributing, you agree that your contributions will be licensed under MIT License. 
