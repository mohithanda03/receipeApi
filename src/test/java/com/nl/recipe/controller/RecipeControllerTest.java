package com.nl.recipe.controller;

import com.nl.recipe.model.Recipe;
import com.nl.recipe.model.RecipesFilterRequest;
import com.nl.recipe.service.RecipeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static com.nl.recipe.utils.RecipeFactory.getRecipe;
import static com.nl.recipe.utils.RecipeFactory.getSavedRecipe;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecipeControllerTest {

    @InjectMocks
    RecipeController recipesController;
    @Mock
    RecipeService recipesService;

    @Test
    @DisplayName("addRecipe : GIVEN a recipe THEN saves the recipe in database")
    public void addRecipe() {
        when(recipesService.addRecipe(getRecipe())).thenReturn(getSavedRecipe());
        ResponseEntity<Recipe> recipeResponseEntity = recipesController.addRecipe(getRecipe());
        assertThat(Objects.requireNonNull(recipeResponseEntity.getBody()).getRecipeName()).isEqualTo("Banana Bread");
        assertEquals(recipeResponseEntity.getBody().getRecipeName(), "Banana Bread");
        assertThat(recipeResponseEntity.getBody().getCategory()).isEqualTo("Desert");
    }

    @Test
    @DisplayName("getAllRecipes : Fetches all the available recipes in the database")
    public void getAllRecipes() {
        List<Recipe> recipeList = Collections.singletonList(getSavedRecipe());
        when(recipesService.getAllRecipes()).thenReturn(recipeList);
        ResponseEntity<List<Recipe>> recipeResponseEntity = recipesController.getAllRecipes();
        assertThat(recipeResponseEntity.getBody()).hasSize(1);
        assertThat(recipeResponseEntity.getBody().get(0).getCategory()).isEqualTo("Desert");
    }

    @Test
    @DisplayName("getRecipesByCategory : GIVEN a category THEN returns all the recipes under the given category")
    public void getRecipesByCategory() {
        List<Recipe> recipeList = Collections.singletonList(getSavedRecipe());
        when(recipesService.getRecipesByCategory(anyString())).thenReturn(recipeList);
        ResponseEntity<List<Recipe>> recipeResponseEntity = recipesController.getRecipesByCategory("Desert");
        assertThat(recipeResponseEntity.getBody()).hasSize(1);
        assertThat(recipeResponseEntity.getBody().get(0).getRecipeName()).isEqualTo("Banana Bread");
    }

    @Test
    @DisplayName("searchRecipesByCriteria : GIVEN a few filters THEN returns the recipes matching the filter criteria")
    public void searchRecipesByIngredients() {
        RecipesFilterRequest recipesFilterRequest = new RecipesFilterRequest();
        recipesFilterRequest.setCategory("Banana Bread");
        recipesFilterRequest.setInstructions("Oven");
        Map<String, Boolean> booleanMap = new HashMap<>();
        booleanMap.put("Sugar", true);
        recipesFilterRequest.setIngredients(booleanMap);
        List<Recipe> recipeList = Collections.singletonList(getSavedRecipe());
        when(recipesService.searchRecipesByCriteria(recipesFilterRequest)).thenReturn(recipeList);
        ResponseEntity<List<Recipe>> recipeResponseEntity = recipesController.searchRecipesByCriteria(recipesFilterRequest);
        assertThat(recipeResponseEntity.getBody()).hasSize(1);
        assertThat(recipeResponseEntity.getBody().get(0).getRecipeName()).isEqualTo("Banana Bread");
    }
}
