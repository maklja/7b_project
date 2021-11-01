#!/bin/bash

sh ./mvnw package -DskipTests

seedDataJar=$(find ./data_seeder/target -name 'data_seeder-*.jar')

java -jar $seedDataJar