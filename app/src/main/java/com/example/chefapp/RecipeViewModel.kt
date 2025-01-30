package com.example.chefapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import android.content.Context
import com.google.gson.Gson
import java.io.FileOutputStream

class RecipeViewModel : ViewModel() {
    private val _recipes = MutableLiveData<MutableList<Recipe>>(mutableListOf())
    val recipes: LiveData<MutableList<Recipe>> get() = _recipes

    private val gson = Gson()

    fun addRecipe(recipe: Recipe) {
        val updatedList = _recipes.value.orEmpty().toMutableList().apply {
            add(recipe)
        }
        _recipes.value = updatedList
        Log.d("RecipeViewModel", "Recipe added with ingredients: ${recipe.extendedIngredients.size}")
    }

    // Save recipes to JSON file
    fun saveRecipesToFile(context: Context) {
        val recipesJson = gson.toJson(_recipes.value)
        val fileName = "recipes.json"

        try {
            val fileOutputStream: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(recipesJson.toByteArray())
            fileOutputStream.close()
            Log.d("RecipeViewModel", "Recipes saved to JSON file")
        } catch (e: Exception) {
            Log.e("RecipeViewModel", "Error saving recipes to JSON file", e)
        }
    }

    // Load recipes from JSON file
    fun loadRecipesFromFile(context: Context) {
        val fileName = "recipes.json"

        try {
            val fileInputStream = context.openFileInput(fileName)
            val jsonString = fileInputStream.bufferedReader().use { it.readText() }
            val recipesList: MutableList<Recipe> = gson.fromJson(jsonString, Array<Recipe>::class.java).toMutableList()
            _recipes.value = recipesList
            Log.d("RecipeViewModel", "Recipes loaded from JSON file")
        } catch (e: Exception) {
            Log.e("RecipeViewModel", "Error loading recipes from JSON file", e)
        }
    }
}
