FROM eclipse-temurin:11-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/auth-service-1.0.0.jar app.jar

HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/api/auth/health || exit 1

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]