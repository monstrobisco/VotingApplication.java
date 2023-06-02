#!/bin/bash

echo "Executando: mvn clean -DskipTests"
mvn clean -DskipTests


echo "Executando: mvn compile -DskipTests"
mvn compile -DskipTests


echo "Executando: mvn install -DskipTests"
mvn install -DskipTests


echo "Executando: docker build -t thmedeiroslima/voting ."
docker build -t thmedeiroslima/voting .


echo "Executando: docker push thmedeiroslima/voting"
docker push thmedeiroslima/voting


echo "Executando: docker-compose up"
docker-compose up
