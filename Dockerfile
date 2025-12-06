# Usar JDK para compilar o projeto
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean shadowJar -x test

# Imagem final com JRE
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar o JAR gerado pelo Gradle (ajustar nome se diferente)
COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
