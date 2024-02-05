FROM openjdk:17-alpine
VOLUME /tmp
EXPOSE 8080
ADD target/recipeApi-1.0.0.jar recipeApi-1.0.0.jar
ENTRYPOINT ["java","-jar","recipeApi-1.0.0.jar"]