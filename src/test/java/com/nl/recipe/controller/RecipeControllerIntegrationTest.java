package com.nl.recipe.controller;

import com.nl.recipe.model.ErrorDetails;
import com.nl.recipe.model.Ingredient;
import com.nl.recipe.model.Recipe;
import com.nl.recipe.model.RecipesFilterRequest;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.*;

import static com.nl.recipe.utils.RecipeFactory.getRecipe;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Type RecipeControllerIntegrationTest Tests the api operations end to end with real inputs.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecipeControllerIntegrationTest {
    private static final String HOST_NAME = "http://localhost:";
    public static Recipe recipe;
    private static TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    public RecipeControllerIntegrationTest() {
        super();
    }

    @BeforeAll
    public static void init() {
        restTemplate = new TestRestTemplate();
    }

    @BeforeEach
    public void setUp() {
        recipe = getRecipe();
        restTemplate.postForEntity(HOST_NAME + port + "/recipe", recipe, Recipe.class);

    }

    @Test
    @DisplayName("add Recipe: GIVEN a recipe THEN adds it to the database")
    void addRecipe() {
        //Recipe recipe = getRecipe();
        recipe = new Recipe(UUID.randomUUID().toString(), "Paneer Tikka",
                "Vegetarian",
                "Marinate the paneer and keep it in the tandoor",
                2,
                Lists.newArrayList(new Ingredient("Paneer"),
                        new Ingredient("Curd"),
                        new Ingredient("Salt"),
                        new Ingredient("Pepper")));
        ResponseEntity<Recipe> recipeResponse = restTemplate.postForEntity(HOST_NAME + port + "/recipe", recipe, Recipe.class);
        assertThat(Objects.requireNonNull(recipeResponse.getBody()).getRecipeId()).isNotNull();
        assertThat(recipeResponse.getBody().getIngredients()).hasSize(4);
        assertThat(recipeResponse.getBody().getRecipeName()).isEqualTo("Paneer Tikka");
    }

    @Test
    @DisplayName("getAllRecipes : Returns all the available recipes")
    void getAllRecipes() {
        ResponseEntity<List> recipes = restTemplate.getForEntity(HOST_NAME + port + "/recipes", List.class);
        assertThat(recipes.getBody()).isNotEmpty();
        assertThat(recipes.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("getRecipesByCategory : GIVEN a category THEN returns all the available recipes under that category")
    void getRecipesByCategory() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<Recipe>> recipeByCategory = restTemplate.exchange(HOST_NAME + port + "/recipes?category=Desert", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>() {
        });
        assertThat(Objects.requireNonNull(recipeByCategory.getBody())).hasSize(1);
        assertThat(recipeByCategory.getBody().get(0).getRecipeName()).isEqualTo("Banana Bread");
    }

    @Test
    @DisplayName("updateRecipeById: GIVEN a recipe THEN updates the given recipe")
    void updateRecipeById() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<Recipe>> recipes = restTemplate.exchange(HOST_NAME + port + "/recipes?category=Desert", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>() {
        });
        assertThat(recipes.getBody()).isNotEmpty();
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Dry-fruits");
        recipes.getBody().get(0).getIngredients().add(ingredient);
        assertThat(restTemplate.exchange(HOST_NAME + port + "/recipe", HttpMethod.PUT, ResponseEntity.ok(recipes.getBody().get(0)), String.class).getBody()).isEqualTo("Recipe updated successfully.");
        assertThat(recipes.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("removeRecipeById : GIVEN a recipeId THEN removes the recipe from the database")
    void removeRecipeById() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<Recipe>> recipes = restTemplate.exchange(HOST_NAME + port + "/recipes/category/Desert", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>() {
        });
        assertThat(recipes.getBody()).isNotEmpty();
        assertThat(restTemplate.exchange(HOST_NAME + port + "/recipe/" + recipes.getBody().get(0).getRecipeId(), HttpMethod.DELETE, null, String.class).getBody()).isEqualTo("Recipe deleted successfully.");
        assertThat(recipes.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("getRecipeByCategoryForExceptionTest: GIVEN a category and no recipes found under that category THEN returns an exception with message")
    void getRecipeByCategoryForExceptionTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<ErrorDetails> exception = restTemplate.exchange(HOST_NAME + port + "/recipes/category/Fruit", HttpMethod.GET, entity, ErrorDetails.class);
        assertThat(Objects.requireNonNull(exception.getBody()).getMessage()).isEqualTo("No recipes found under :Fruit");
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("searchRecipesByCriteria : GIVEN a filter criteria THEN returns the recipes that match the filter criteria")
    void searchRecipesByIngredients() {
        RecipesFilterRequest recipesFilterRequest = new RecipesFilterRequest();
        recipesFilterRequest.setCategory("Fish");
        recipesFilterRequest.setServings(1);
        recipesFilterRequest.setInstructions("Oven");
        Map<String, Boolean> map = new HashMap<>();
        map.put("pepper", true);
        recipesFilterRequest.setIngredients(map);
        ResponseEntity<List> recipes = restTemplate.postForEntity(HOST_NAME + port + "/search/recipes", recipesFilterRequest, List.class);
        assertThat(recipes.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
