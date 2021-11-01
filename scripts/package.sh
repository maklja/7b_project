#!/bin/bash

sh ./mvnw package

docker build -f ./Dockerfile -t 7b/project .