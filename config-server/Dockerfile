# Build Stage
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Package Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]