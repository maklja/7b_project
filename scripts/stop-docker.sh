#!/bin/bash

sh ./mvnw package -DskipTests

docker-compose  -f ./docker/docker-compose-run.yml down