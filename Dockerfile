# Use Maven 3.8 with OpenJDK 17 for the build stage
FROM maven:latest AS build


# Set the working directory inside the container
WORKDIR /app

# Copy only the pom.xml to download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source files for your project
COPY src ./src

# Build the project using Maven
RUN mvn clean package -DskipTests

# Use OpenJDK 17 for the runtime stage
FROM openjdk:17-jdk

# Set the working directory for the runtime
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/08-spring-security-0.0.1-SNAPSHOT.jar .

# Expose the port your application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/08-spring-security-0.0.1-SNAPSHOT.jar"]
