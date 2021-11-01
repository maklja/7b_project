# 7B Project - Twitter

Project has been done using Java 11, Spring boot and MongoDB. 
This project has a three modules:
- infrastructure - that contains busyness login and access to the database.
- api - that contains REST API and security.
- data_seeder - Spring boot console application that can generate data for testing.

## Run application

### Local development
In order to start an application for local development it is required to have a MongoDB instance running on port 27117. 
You can start a new MongoDB instance using docker command:

```shell
sh ./scripts/start-dev.sh
```

After the MongoDB container has started you can use your favorite editor and start an application.
<br/>
To clean up docker containers execute:

```shell
sh ./scripts/stop-dev.sh
```

### Test run
In order to start integration it is required to have a MongoDB instance running on port 30017.
You can start a new MongoDB instance using docker command:

```shell
sh ./scripts/start-test.sh
```

After the MongoDB container has started you can use your favorite editor to run tests or you can execute this command:

```shell
./mvnw test
```

To clean up docker containers execute:

```shell
sh ./scripts/stop-test.sh
```

### Docker run
Don’t want to use the editor, then use this option to start an application in the docker.
This option will compile and pack application and then run it in a docker container.
Application will be started on port 8080 and MongoDB will be started on port 27117 on your local machine.

```shell
sh ./scripts/start-docker.sh
```
To clean up docker containers execute:

```shell
sh ./scripts/stop-docker.sh
```

## Create test data
To create test data using data seeder console application execute script below. 
Data seeder requires the MongoDB database to be up and running on port 27117 on your local machine.
**Note that data seeder will remove all previous data from the database.**

```shell
sh ./scripts/seed_data.sh
```

## Open API

Once application is up and running you can find Open API Swagger UI on [this link](http://localhost:8080/swagger-ui.html).
