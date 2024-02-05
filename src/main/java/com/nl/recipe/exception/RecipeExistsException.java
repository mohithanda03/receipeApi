package com.nl.recipe.exception;

/**
 * @author Mohit Handa
 * Type RecipeExistsException thrown when a user tries to add an already existing recipe.
 */
public class RecipeExistsException extends RuntimeException{
    public RecipeExistsException(String message){
        super(message);
    }
}
