# Imagem base
FROM openjdk:17-alpine

# VARIÁVEL ARG JAR_FILE=target/*.jar -> troque se o seu arquivo JAR estiver em outro local
ARG JAR_FILE=target/*.jar

# Copia o arquivo JAR para o container
COPY ${JAR_FILE} app.jar

# A porta padrão que o Spring Boot utiliza é 8080
EXPOSE 9090

# Comando para executar o arquivo .jar
ENTRYPOINT ["java","-jar","/app.jar"]
