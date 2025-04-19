# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Copy the project’s jar file into the container
COPY target/socketv2-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 9001

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
