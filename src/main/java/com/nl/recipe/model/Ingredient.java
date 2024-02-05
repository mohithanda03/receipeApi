package com.nl.recipe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Table(name = "ingredient")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ingredientId;

    @Column
    @NotEmpty(message = "Please specify the name of the ingredient")
    private String name;

    public Ingredient(String name){
        this.name = name;
    }
}