package com.example.chefapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CartViewModel : ViewModel() {
    private val _ingredients = MutableLiveData<List<Ingredient>>(mutableListOf())
    val ingredients: LiveData<List<Ingredient>> get() = _ingredients

    fun addIngredients(newIngredients: List<Ingredient>) {
        val currentList = _ingredients.value.orEmpty().toMutableList()
        for (newIngredient in newIngredients) {
            val existingIngredient = currentList.find { it.name == newIngredient.name }

            if (existingIngredient != null) {
                existingIngredient.amount += newIngredient.amount
            } else {
                currentList.add(newIngredient)
            }
        }
        _ingredients.value = currentList
    }

    fun updateIngredient(updatedIngredient: Ingredient) {
        val currentList = _ingredients.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.name == updatedIngredient.name }
        if (index != -1) {
            currentList[index] = updatedIngredient
            _ingredients.value = currentList
        }
    }

    fun clearCart() {
        _ingredients.value = emptyList() // Wyczyść listę składników
    }
}