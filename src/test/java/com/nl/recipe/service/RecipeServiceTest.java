package com.nl.recipe.service;

import com.nl.recipe.exception.RecipeExistsException;
import com.nl.recipe.model.Ingredient;
import com.nl.recipe.model.Recipe;
import com.nl.recipe.model.RecipesFilterRequest;
import com.nl.recipe.repository.RecipeFilterRepository;
import com.nl.recipe.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.nl.recipe.utils.RecipeFactory.getRecipe;
import static com.nl.recipe.utils.RecipeFactory.getSavedRecipe;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Nested
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeFilterRepository recipeFilterRepository;

    @Test
    @DisplayName("addRecipe : GIVEN a recipe THEN saves the recipe in database")
    void addRecipe() {
        when(recipeRepository.existsByRecipeName(anyString())).thenReturn(false);
        when(recipeRepository.save(getRecipe())).thenReturn(getSavedRecipe());
        Recipe recipesList = recipeService.addRecipe(getRecipe());
        assertThat(recipesList.getRecipeName()).isEqualTo("Banana Bread");
    }

    @Test
    @DisplayName("addExistingRecipe : GIVEN a recipe which already exists  THEN returns an Exception with message")
    void addExistingRecipe() {
        when(recipeRepository.existsByRecipeName(anyString())).thenReturn(true);
        assertThatThrownBy(() -> recipeService.addRecipe(getRecipe()))
                .isInstanceOf(RecipeExistsException.class)
                .hasMessage("Recipe already exist with Banana Bread");
    }

    @Test
    @DisplayName("getAllRecipes : Fetches all the available recipes` in the database")
    void getAllRecipes() {
        List<Recipe> recipeList = Collections.singletonList(getSavedRecipe());
        when(recipeRepository.findAll()).thenReturn(recipeList);
        List<Recipe> recipesList = recipeService.getAllRecipes();
        assertThat(recipesList).hasSize(1);
    }

    @Test
    @DisplayName("updateRecipeById : GIVEN a recipe with latest details THEN updates that in the database")
    void updateRecipeById() {
        Recipe recipe = getSavedRecipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Dry fruits");
        recipe.getIngredients().add(ingredient);
        when(recipeRepository.findById(recipe.getRecipeId())).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        Recipe recipesList = recipeService.updateRecipeById(recipe);
        assertThat(recipesList.getIngredients()).hasSize(4);
    }

    @Test
    @DisplayName("deleteRecipeById : GIVEN a recipeId  THEN deletes that from the database")
    void deleteRecipeById() {
        Recipe recipe = getSavedRecipe();
        String recipeId = recipe.getRecipeId();
        doNothing().when(recipeRepository).deleteById(recipeId);
        when(recipeRepository.existsById(recipeId)).thenReturn(true);

        // Act
        Boolean result = recipeService.deleteRecipeById(recipeId);
        // Assert
        verify(recipeRepository, times(1)).deleteById(recipeId);
        assertTrue(result, "The recipe should be deleted and return TRUE");
    }

    @Test
    @DisplayName("deleteRecipeById should not delete the recipe when it does not exist")
    void deleteRecipeById_RecipeDoesNotExist() {
        String nonExistingRecipeId = UUID.randomUUID().toString();
        // Arrange
        when(recipeRepository.existsById(nonExistingRecipeId)).thenReturn(false);

        // Act
        Boolean result = recipeService.deleteRecipeById(nonExistingRecipeId);

        // Assert
        verify(recipeRepository, never()).deleteById(nonExistingRecipeId);
        assertFalse(result, "The recipe should not be deleted and return FALSE");
    }

    @Test
    @DisplayName("searchRecipesByCriteria : GIVEN a few filters THEN returns the recipes matching the filter criteria")
    void searchRecipesByIngredients() {
        RecipesFilterRequest recipesSearchRequest = new RecipesFilterRequest();
        recipesSearchRequest.setCategory("Fish");
        recipesSearchRequest.setServings(1);
        recipesSearchRequest.setInstructions("Oven");
        Map<String, Boolean> map = new HashMap<>();
        map.put("pepper", true);
        recipesSearchRequest.setIngredients(map);
        List<Recipe> recipeList = Collections.singletonList(getSavedRecipe());
        when(recipeFilterRepository.filterRecipiesByCriteria(recipesSearchRequest)).thenReturn(recipeList);
        List<Recipe> listRecipes = recipeService.searchRecipesByCriteria(recipesSearchRequest);
        assertEquals(1, listRecipes.size());

    }

    @Test
    @DisplayName("getRecipesByCategory : GIVEN a category THEN returns all the recipes under the given category")
    void getRecipesByCategory() {
        List<Recipe> recipeList = Collections.singletonList(getSavedRecipe());
        when(recipeRepository.findByCategory(anyString())).thenReturn(Optional.of(recipeList));
        List<Recipe> recipesList = recipeService.getRecipesByCategory("Desert");
        assertEquals(1, recipesList.size());
    }
}