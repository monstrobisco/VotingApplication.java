#!/bin/bash

echo "Executando: mvn clean "
mvn clean


echo "Executando: mvn compile "
mvn compile


echo "Executando: mvn install "
mvn install


echo "Executando: docker build -t thmedeiroslima/voting ."
docker build -t thmedeiroslima/voting .


echo "Executando: docker push thmedeiroslima/voting"
docker push thmedeiroslima/voting


echo "Executando: docker-compose up"
docker-compose up
