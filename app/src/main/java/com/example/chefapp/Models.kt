package com.example.chefapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeSearchResponse(
    val results: List<Recipe>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
) : Parcelable

@Parcelize
data class Recipe(
    val id: Int,
    var title: String,
    val image: String,
    var readyInMinutes: Int,
    val pricePerServing: Double,
    val nutrition: Nutrition,
    val sourceName: String,
    val summary: String,
    val diets: List<String>,
    val extendedIngredients: List<Ingredient>,
    val analyzedInstructions: List<AnalyzedInstruction> // Dodane pole
) : Parcelable

@Parcelize
data class Nutrition(
    val nutrients: List<Nutrients>
) : Parcelable

@Parcelize
data class Nutrients(
    val name: String,
    val amount: Double,
    val unit: String,
    val percentOfDailyNeeds: Double
) : Parcelable

@Parcelize
data class RecipeDetails(
    val id: Int,
    val title: String,
    val image: String,
    val instructions: String?,
    val extendedIngredients: List<Ingredient>,
    val nutrition: Nutrition,
    val summary: String,
    val diets: List<String>,
    val sourceName: String,
    val pricePerServing: Double,
    val readyInMinutes: Int,
    val analyzedInstructions: List<AnalyzedInstruction>
) : Parcelable

@Parcelize
data class Ingredient(
    val id: Int,
    val name: String,
    var amount: Double,
    val unit: String
) : Parcelable

@Parcelize
data class AnalyzedInstruction(
    val name: String,
    val steps: List<InstructionStep>
) : Parcelable

@Parcelize
data class InstructionStep(
    val number: Int,
    val step: String,
    val ingredients: List<Ingredient>,
    val equipment: List<Equipment>,
    val length: Length?
) : Parcelable

@Parcelize
data class Equipment(
    val id: Int,
    val name: String,
    val image: String,
    val temperature: Temperature?
) : Parcelable

@Parcelize
data class Temperature(
    val number: Double,
    val unit: String
) : Parcelable

@Parcelize
data class Length(
    val number: Int,
    val unit: String
) : Parcelable