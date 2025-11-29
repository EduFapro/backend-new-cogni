# Usar JDK para compilar o projeto
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Imagem final com JRE
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar o JAR gerado pelo Gradle (ajustar nome se diferente)
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
