FROM maven:3.8-openjdk-17-slim as create
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.2-jdk-slim 
COPY --from=create /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
