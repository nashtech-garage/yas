- Keep it simple. Simplify things as much as you can. Only make them complex if it’s truly necessary.
  - In controllers, we can call repositories for simple operations like CRUD. For operations that have business logic, the code to handle that business logic should be separated on services.
  - We don't apply soft delete in the system, and not cascade delete (except for many-to-many tables). Whenever there is a database constraint exception, show a message to tell users that they need to delete all the children before delete the parent. 
- Keep the list of third-party libraries as small as possible.
- Dead code or Zombie code should be removed

- Git practices
  - Pull request should be small
  - Delete branch once pull request merged
  - Include #issueId in comment when commit code
  - Local development environment configuration should not be committed

## Code style for Java

- We follow Google Java Style Guide https://google.github.io/styleguide/javaguide.html

#### Install CheckStyle-IDEA Plugin:

1. File -> Setting -> Plugin
2. Find and Install CheckStyle-IDEA Plugin: https://plugins.jetbrains.com/plugin/1065-checkstyle-idea
3. File -> Setting -> Editor -> Code Style -> Java
4. Import Scheme -> Checkstyle configuration
5. Select file /checkstyle/checkstyle.xml to import

- Entity definitions
  - Use primitives for non-nullable fields and wrapper objects for nullable fields
  - Override `equals` and `hashCode`

- Liquibase:
> * Do not update an already run changset. Any new database change should be placed in a new changeset
> * For DDL, put changeset under db\changelog\ddl
> * For data, put changeset under db\changelog\data

## Code style for frontend

- Run `npx prettier -w .` before commit frontend code

## How to run the frontend locally (not in container)

The frontend (storefont and backoffice) needs to run behind the bff, storefront-bff or backoffice-bff respectively  to enable authentication. So to run locally, we must run the bff first either by the IDE or by command line. Then run the frontend by `npm run dev`. We will not access the frontend directly but via the bff http://localhost:8087/

## How to run a backend service locally (not in container)

- Run `docker compose up` to start all the services in docker container
- Run service you want to test in the your IDE
- Routing the fronend to your locally running service
    - In the application.yaml setting of the bff (backoffice-bff and storefront-bff) spring -> cloud --> gateway --> routes. Add the route to your locally service as the first route. For example

```yaml
        - id: api_product_local
          uri: http://localhost:8092 #the url of your product service
          predicates:
            - Path=/api/product/**
          filters:
            - RewritePath=/api/(?<segment>.*), /$\{segment}
            - TokenRelay=
```
    - Run the frontend locally as the guide above

> *_Note:_* If you have only 16GB, then in the /.env file, keep the `COMPOSE_FILE=docker-compose.yml` and Set `OTEL_JAVAAGENT_ENABLED=false`. In the docker-compose.yml, you can also comment out some unused services. 

## ElasticSearch

- Notes for working with `Search` service and `ElasticSearch`
  - In `application.properties` of search module: change spring.kafka.consumer.bootstrap-servers=`kafka:9092` to `http://localhost:29092`
  - In logback-spring.xml of search module: add `<logger name="org.apache.http.wire" level="DEBUG"/>` to show query generated in console
