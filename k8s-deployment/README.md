# YAS K8S Deployment
## Resource cluster installation reference
- **Postgresql:** https://github.com/zalando/postgres-operator
- **Elasticsearch:** https://github.com/elastic/cloud-on-k8s
- **Kafka:** https://github.com/strimzi/strimzi-kafka-operator
- **Debezium Connect:** https://debezium.io/documentation/reference/stable/operations/kubernetes.html
- **Keycloak:** https://www.keycloak.org/operator/installation
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
- Execute [setup-cluster.sh](setup-cluster.sh) to set up severs: `postgresql`, `elasticsearch`, `kafka`, `debezium connect`, `keyloak`
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
192.168.49.2 api.yas.local.com
192.168.49.2 grafana.yas.local.com

```
`192.168.49.2` is ip of minikbe node use this command line to get the ip of minikube
```shell
minikube ip
```
## Cluster configuration
All configuration of cluster is setting on [cluster-config.yaml](cluster-config.yaml) in folder k8s-deploy
```yaml
domain: yas.local.com
postgresql:
  replicas: 1
  username: yasadminuser
  password: admin
kafka:
  replicas: 1
zookeeper:
  replicas: 1
elasticsearch:
  replicas: 1
  username: yas
  password: LarUmB3A49NTg9YmgW4=
keycloakRealm:
  backofficeUrl: http://backoffice.yas.local.com
  storefrontUrl: http://storefront.yas.local.com
grafana:
  username: admin
  password: admin
```
## Yas configuration 
All configurations of YAS application putted in the yas-configuration helm chart bellow is the values of [values.yaml](..%2Fcharts%2Fyas-configuration%2Fvalues.yaml)
```yaml
credentials:
  postgresql:
    username: yasadminuser
    password: admin
  elasticsearch:
    username: yas
    password: LarUmB3A49NTg9YmgW4=
  keycloak:
    customerManagementClientSecret: NKAr3rnjwm9jlakgKpelukZGFaHYqIWE

#Genneral application.yaml for all microservice
applicationConfig:
  server:
    shutdown: graceful
    port: 80

  management:
    otlp:
      tracing:
        endpoint: http://opentelemetry-collector.observability:4318/v1/traces
    server:
      port: 8090
    health:
      readinessstate:
        enabled: true
      livenessstate:
        enabled: true
    tracing:
      sampling:
        probability: 1.0
    metrics:
      tags:
        application: ${spring.application.name}
    endpoints:
      web:
        exposure:
          include: prometheus, health
    endpoint:
      health:
        probes:
          enabled: true
        show-details: always

  logging:
    pattern:
      level: application=${spring.application.name} traceId=%X{traceId:-} spanId=%X{spanId:-} level=%level

  spring:
    lifecycle:
      timeout-per-shutdown-phase: 30s
    security:
      oauth2:
        resourceserver:
          jwt:
            issuer-uri: http://identity.yas.local.com/realms/Yas

    datasource:
      url:
      username: ${POSTGRESQL_USERNAME}
      password: ${POSTGRESQL_PASSWORD}
    kafka:
      consumer:
        bootstrap-servers: kafka-cluster-kafka-brokers.kafka:9092

  springdoc:
    oauthflow:
      authorization-url: http://identity.yas.local.com/realms/Yas/protocol/openid-connect/auth
      token-url: http://identity.yas.local.com/realms/Yas/protocol/openid-connect/token

  yas:
    services:
      cart: http://cart/cart
      customer: http://customer/customer
      inventory: http://inventory/inventory
      location: http://location/location
      media: http://media/media
      order: http://order/order
      payment: http://payment/payment
      payment-paypal: http://payment-paypal/payment-paypal
      product: http://product/product
      promotion: http://promotion/promotion
      rating: http://rating/rating
      search: http://search/search
      tax: http://tax/tax

# Gateway config for bff microservices
gatewayRoutesConfig:
  spring:
    security:
      oauth2:
        client:
          provider:
            keycloak:
              issuer-uri: http://identity.yas.local.com/realms/Yas
    cloud:
      gateway:
        routes:
          - id: product_api
            uri: http://product
            predicates:
              - Path=/api/product/**
            filters:
              - RewritePath=/api/(?<segment>.*), /$\{segment}
              - TokenRelay=
          - id: location_api
            uri: http://location
            predicates:
              - Path=/api/location/**
            filters:
              - RewritePath=/api/(?<segment>.*), /$\{segment}
              - TokenRelay=
          - id: inventory_api
            uri: http://inventory
            predicates:
              - Path=/api/inventory/**
            filters:
              - RewritePath=/api/(?<segment>.*), /$\{segment}
              - TokenRelay=
          - id: cart_api
            uri: http://cart
            predicates:
              - Path=/api/cart/**
            filters:
              - RewritePath=/api/(?<segment>.*), /$\{segment}
              - TokenRelay=
          - id: customer_api
            uri: http://customer
            predicates:
              - Path=/api/customer/**
            filters:
              - RewritePath=/api/(?<segment>.*), /$\{segment}
              - TokenRelay=
          - id: media_api
            uri: http://media
            predicates:
              - Path=/api/media/**
            filters:
              - RewritePath=/api/(?<segment>.*), /$\{segment}
              - TokenRelay=
          - id: rating_api
            uri: http://rating
            predicates:
              - Path=/api/rating/**
            filters:
              - RewritePath=/api/(?<segment>.*), /$\{segment}
              - TokenRelay=
          - id: tax_api
            uri: http://tax
            predicates:
              - Path=/api/tax/**
            filters:
              - RewritePath=/api/(?<segment>.*), /$\{segment}
              - TokenRelay=
          - id: promotion_api
            uri: http://protion
            predicates:
              - Path=/api/promotion/**
            filters:
              - RewritePath=/api/(?<segment>.*), /$\{segment}
              - TokenRelay=
          - id: search_api
            uri: http://search
            predicates:
              - Path=/api/search/**
            filters:
              - RewritePath=/api/(?<segment>.*), /$\{segment}
              - TokenRelay=
          - id: order_api
            uri: http://order
            predicates:
              - Path=/api/order/**
            filters:
              - RewritePath=/api/(?<segment>.*), /$\{segment}
              - TokenRelay=
          - id: ui
            uri: ${UI_HOST}
            predicates:
              - Path=/**

# Media application config custom
mediaApplicationConfig:
  server:
    servlet:
      context-path: /media
  yas:
    publicUrl: http://api.yas.local.com/media

# Customer application config custom
customerApplicationConfig:
  keycloak:
    auth-server-url: http://identity.yas.local.com
    realm: Yas
    resource: customer-management
    credentials:
      secret: ${KEYCLOAK_CUSTOMER_MANAGEMENT_CLIENT_SECRET}

# Search application config custom
searchApplicationConfig:
  elasticsearch:
    url: elasticsearch-es-http.elasticsearch
    username: ${ELASTICSEARCH_USERNAME}
    password: ${ELASTICSEARCH_PASSWORD}

logbackConfig: |
  <?xml version="1.0" encoding="UTF-8" ?>
  <Configuration>
      <include resource="org/springframework/boot/logging/logback/defaults.xml" />
      <include resource="org/springframework/boot/logging/logback/console-appender.xml" />
      <property name="LOG_LEVEL" value="${LOG_LEVEL:-INFO}"/>
      <root level="INFO">
          <appender-ref ref="CONSOLE" />
      </root>
      <logger name="com.yas" level="${LOG_LEVEL}">
          <appender-ref ref="CONSOLE" />
      </logger>
  </Configuration>

reloader:
  nameOverride: "yas-reloader"
  fullnameOverride: "yas-reloader"
  reloader:
    watchGlobally: false
```
## Yas helm charts
All charts of Yas application situated in `charts` folder

To Install the Yas helm charts access to [https://nashtech-garage.github.io/yas/](https://nashtech-garage.github.io/yas/)

## Observability
The Yas observability follow by the standard of Open Telemetry recommendation.
Promtail collect the log from all applications send to Open Telemetry Collector after that, Open Telemetry Collector distribute to Loki server.
The Yas applications also send the metric data to Open Telemetry Collector, Open Telemetry collector send the metric data to Tempo server
Bellow is the configuration of Open Telemetry Collector
```yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317
      http:
        endpoint: 0.0.0.0:4318
  loki:
    protocols:
      http:
        endpoint: 0.0.0.0:3500
    use_incoming_timestamp: true
processors:
  batch:
  attributes:
    actions:
      - action: insert
        key: loki.attribute.labels
        value: namespace,container,pod,level,traceId
      - action: insert
        key: loki.format
        value: raw

exporters:
  logging:
    verbosity: detailed
  loki:
    endpoint: http://loki-gateway/loki/api/v1/push
  otlphttp:
    endpoint: http://tempo:4318
service:
  pipelines:
    logs:
      receivers: [ loki ]
      processors: [ attributes ]
      exporters: [ loki ]
    traces:
      receivers: [ otlp ]
      processors: [ batch ]
      exporters: [ otlphttp ]
```

### How to view log on the Grafana
On the left menu select `Expore` -> select `Loki` datasource -> select Label filters:
- namespace
- container (Application)

On the Loki also support track by traceId, on The Tempo you can select the Node graph to view the tracing of request 
