# Usar JDK para compilar o projeto
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean shadowJar -x test

# Imagem final com JRE
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create a non-root user
RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --ingroup appgroup appuser

# Copiar o JAR gerado pelo Gradle (ajustar nome se diferente)
COPY --from=build /app/build/libs/*-all.jar app.jar

# Change ownership
RUN chown -R appuser:appgroup /app

# Switch user
USER appuser

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
