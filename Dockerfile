# 1. Image de base Java 17
FROM eclipse-temurin:17-jdk-alpine

# 2. Ajouter le jar
ARG JAR_FILE=target/SmartTaskManager-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 3. Exposer le port
EXPOSE 8080

# 4. Commande de démarrage
ENTRYPOINT ["java","-jar","/app.jar"]
