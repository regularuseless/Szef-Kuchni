package com.example.chefapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomDishFragment : Fragment() {
    private val recipeViewModel: RecipeViewModel by activityViewModels()
    private lateinit var recipeList: MutableList<Recipe>
    private lateinit var customIngredientsAdapter: CustomIngredientsAdapter
    private val ingredientList: MutableList<Ingredient> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_custom_dish, container, false)

        val etDishName: EditText = view.findViewById(R.id.et_dish_name)
        val etDishCost: EditText = view.findViewById(R.id.et_dish_cost)
        val etDishDifficulty: EditText = view.findViewById(R.id.et_dish_difficulty)
        val etDishCalories: EditText = view.findViewById(R.id.et_dish_calories)
        val etDishDescription: EditText = view.findViewById(R.id.et_dish_description)
        val etDietType: EditText = view.findViewById(R.id.et_diet_type)
        val etIngredientName: EditText = view.findViewById(R.id.et_ingredient_name)
        val etIngredientAmount: EditText = view.findViewById(R.id.et_ingredient_amount)
        val etIngredientUnit: EditText = view.findViewById(R.id.et_ingredient_units)
        val btnAddIngredient: Button = view.findViewById(R.id.btn_add_ingredient)
        val btnSaveRecipe: Button = view.findViewById(R.id.btn_save_recipe)
        val etInstructions: EditText = view.findViewById(R.id.et_instructions)

        val rvIngredients: RecyclerView = view.findViewById(R.id.rv_ingredients)

        recipeList = mutableListOf()
        customIngredientsAdapter = CustomIngredientsAdapter(ingredientList)

        rvIngredients.layoutManager = LinearLayoutManager(context)
        rvIngredients.adapter = customIngredientsAdapter

        btnAddIngredient.setOnClickListener {
            val ingredientName = etIngredientName.text.toString().trim()
            val ingredientAmount = etIngredientAmount.text.toString().trim().toDoubleOrNull()
            val ingredientUnit = etIngredientUnit.text.toString().trim()

            if (ingredientName.isEmpty() || ingredientAmount == null || ingredientUnit.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all ingredient fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newIngredient = Ingredient(
                id = ingredientList.size + 1,
                name = ingredientName,
                amount = ingredientAmount,
                unit = ingredientUnit
            )

            ingredientList.add(newIngredient)
            customIngredientsAdapter.notifyDataSetChanged()

            etIngredientName.text.clear()
            etIngredientAmount.text.clear()
            etIngredientUnit.text.clear()
        }

        btnSaveRecipe.setOnClickListener {
            val dishName = etDishName.text.toString().trim()
            val dishCost = etDishCost.text.toString().trim().toDoubleOrNull()
            val dishDifficulty = etDishDifficulty.text.toString().trim().toIntOrNull()
            val dishCalories = etDishCalories.text.toString().trim().toDoubleOrNull()
            val dishDescription = etDishDescription.text.toString().trim()
            val dietType = etDietType.text.toString().trim()
            val instructionsText = etInstructions.text.toString().trim()

            if (dishName.isEmpty() || dishCost == null || dishDifficulty == null || dishCalories == null || dishDescription.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all the recipe fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tworzenie kroków z instrukcji
            val steps = instructionsText.split("\n").mapIndexed { index, stepText ->
                InstructionStep(
                    number = index + 1,
                    step = stepText,
                    ingredients = emptyList(),
                    equipment = emptyList(),
                    length = null
                )
            }

            val analyzedInstructions = listOf(
                AnalyzedInstruction(
                    name = "Custom Instructions",
                    steps = steps
                )
            )

            // Tworzenie obiektu Recipe
            val recipe = Recipe(
                id = recipeList.size + 1,
                title = dishName,
                image = "",
                readyInMinutes = dishDifficulty!!,
                pricePerServing = dishCost!!,
                nutrition = Nutrition(nutrients = listOf(Nutrients("Calories", dishCalories, "kcal", 0.0))),
                sourceName = "Custom",
                summary = dishDescription,
                diets = listOf(dietType),
                extendedIngredients = ingredientList,
                analyzedInstructions = analyzedInstructions // Dodane analyzedInstructions
            )

            recipeList.add(recipe)
            recipeViewModel.addRecipe(recipe)
            Toast.makeText(requireContext(), "Recipe saved successfully!", Toast.LENGTH_SHORT).show()
            navigateToMainPage()
        }

        return view
    }

    private fun navigateToMainPage() {
        val mainActivity = activity as? MainActivity
        mainActivity?.switchToTab(2)
        val mainPage = MainPage()
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, mainPage)
            .addToBackStack(null)
            .commit()
    }
}