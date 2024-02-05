package com.nl.recipe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String recipeId;

    @Column(nullable = false, unique = true)
    @NotEmpty(message="Please provide the Recipe Name")
    private String recipeName;

    @NotEmpty(message = "Please choose a category of the Recipe")
    private String category;

    @Column
    private String instructions;

    @Column
    private int servings;

    @OneToMany(targetEntity = Ingredient.class, cascade = { CascadeType.ALL })
    @PrimaryKeyJoinColumn(name = "recipeName", referencedColumnName = "recipeId")
    @NotEmpty(message = "Please specify the ingredients of recipe")
    private List<Ingredient> ingredients;
}
