# recipeApi
RecipeApi

## General info
An api to manage the recipes. A user can perform below operations using recipeApi:
1. add a new recipe 
2. update the existing recipe
3. search all the available recipes
4. delete a recipe
5. search recipes on a filter criteria.

## Technologies
This API has been developed using 
Java 17
Spring-boot 3.2.2
Maven

All the entities are mapped using the JPA.  
Used Docker composer to run api in the docker container.
Used h2 in-memory h2 database.

## Instructions to run this api:

Build : mvn clean install
Run: docker-compose up
Prerequisite: Please make sure that docker desktop is running.

## Api Links
Health check using Spring actuator url : http://localhost:8080/actuator/health

Swagger-ui url : http://localhost:8080/swagger-ui/index.html

Please refer to the postman collection file "recepieApi.postman_collection.json" for the sample requests.