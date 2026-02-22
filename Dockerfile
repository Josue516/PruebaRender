# Imagen con Maven para compilar
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Imagen ligera para ejecutar
FROM eclipse-temurin:17-jdk

# Configuración de la zona horaria
RUN apt-get update && apt-get install -y tzdata && \
    ln -sf /usr/share/zoneinfo/America/Lima /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
