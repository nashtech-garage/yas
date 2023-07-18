# Update Yas theme into k8s deployment
According to the guideline of theme deployment from Keycloak https://www.keycloak.org/docs/latest/server_development/#deploying-themes

Bellow is steps to create a jar file the theme folder and update to k8s deployment folder

- Go to `yas` folder
```shell
cd yas
```
- Run the jar command line to create a jar file
```shell
jar cvf yas.jar *
```
- Copy `yas.jar` file to `k8s/deploy/keycloak/themes` folder