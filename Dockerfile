FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 9097
ENTRYPOINT ["java","-jar","app.jar"]
