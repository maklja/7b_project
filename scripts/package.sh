#!/bin/bash

sh ./mvnw package -DskipTests

docker build -f ./Dockerfile -t 7b/project .