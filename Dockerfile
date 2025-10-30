FROM maven:3.9.6-eclipse-temurin-21

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos del wrapper de Maven/Gradle para que el comando funcione
# El resto del código fuente será sincronizado por el volumen de docker-compose
COPY .mvn/ .mvn
COPY mvnw pom.xml ./