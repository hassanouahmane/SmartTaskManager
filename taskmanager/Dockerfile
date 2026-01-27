# =========================
# STAGE 1 : BUILD AVEC MAVEN
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copier les fichiers Maven
COPY pom.xml .
COPY src ./src

# Compiler le projet
RUN mvn clean package -DskipTests


# =========================
# STAGE 2 : RUN
# =========================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copier le jar généré depuis le stage build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
