# YAS: Yet Another Shop

YAS is a pet project aim to practice building a typical microservice application in Java

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
1. Add `127.0.0.1 api.yas.local`, `127.0.0.1 storefront.yas.local`, `127.0.0.1 backoffice.yas.local`, `127.0.0.1 identity.yas.local` and `127.0.0.1 pgadmin.yas.local` to your host file
1. Open your favorite browser and go to  http://pgadmin.yas.local. Account login: admin@yas.com / admin. Register a server: localhost, port default, username admin, password admin. Then create a database name `keycloak`, `media`, `product`
1. Open another browser tab and go to http://identity.yas.local. Admin account is: admin/admin
1. The Postgresql server is also published to the host machine: servername: localhost, port: 30007, username: admin, password: admin

## Getting started with Docker in Ubuntu

1. Get the latest source code
2. Install minikube

    ```sh
    curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
    sudo install minikube-linux-amd64 /usr/local/bin/minikube 
    ```
3. Create folders 
    ```sh
    mkdir -p -v $HOME/k8s-yas-data/postgres \
                $HOME/k8s-yas-data/pgadmin \
                $HOME/k8s-yas-data/var/lib/postgres \
                $HOME/k8s-yas-data/var/lib/pgadmin
    ```
4. Start minikube 
    ```sh
    minikube start --driver=docker --mount-string $HOME/k8s-yas-data:/k8s-yas-data/ --mount 
   ```
5. Install kubectl 
    ```sh
    minikube kubectl -- get pods -A
    echo 'alias kubectl="minikube kubectl --"' >> ~/.bashrc
   source ~/.bashrc
   ```
6. Install NGINX ingress controller 
    ```sh
   kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.1.1/deploy/static/provider/cloud/deploy.yaml
   ```
7. Update yaslocal.yaml

    - replace [this](https://github.com/nashtech-garage/yas/blob/main/yaslocal.yaml#L355) with `- mountPath: "/k8s-yas-data/var/lib/postgresql/data"`
    
    - replace [this](https://github.com/nashtech-garage/yas/blob/main/yaslocal.yaml#L367) with `- mountPath: "/k8s-yas-data/var/lib/pgadmin"`
    
    - replace [this](https://github.com/nashtech-garage/yas/blob/main/yaslocal.yaml#L392) with `path: "/k8s-yas-data/postgres"`
    
    - replace [this](https://github.com/nashtech-garage/yas/blob/main/yaslocal.yaml#L417) with `path: "/k8s-yas-data/pgadmin"`
    
    - replace [this](https://github.com/nashtech-garage/yas/blob/main/yaslocal.yaml#L400) and [this](https://github.com/nashtech-garage/yas/blob/main/yaslocal.yaml#L400) with `- minikube`
8. Run 
    ```sh
   kubectl apply -f yaslocal.yaml 
   ```
9. Run to add to your host file
    ```sh
   echo -e "$(minikube ip) api.yas.local\
   \n$(minikube ip) storefront.yas.local\
   \n$(minikube ip) backoffice.yas.local\
   \n$(minikube ip) identity.yas.local\
   \n$(minikube ip) pgadmin.yas.local" | sudo tee -a /etc/hosts
   ```
10. Open your favorite browser and go to  http://pgadmin.yas.local. Account login: admin@yas.com / admin. Register a server: localhost, port default, username admin, password admin. Then create a database name `keycloak`, `media`, `product`



## Components roadmap
- [ ] Identity service
- [ ] Product service
- [ ] Pricing service
- [ ] Cart service
- [ ] Order service
- [ ] Review service
