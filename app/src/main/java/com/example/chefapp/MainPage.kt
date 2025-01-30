package com.example.chefapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.widget.TextView
import java.util.*

class MainPage : Fragment() {
    private val recipeViewModel: RecipeViewModel by activityViewModels()
    private val detailedRecipeViewModel: DetailedRecipeViewModel by activityViewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var streakCountTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private var savedRecipes = mutableListOf<Recipe>()  // List of custom recipes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_page, container, false)

        sharedPreferences = requireContext().getSharedPreferences("CookingStreak", Context.MODE_PRIVATE)
        streakCountTextView = view.findViewById(R.id.tv_cooking_streak_count)

        loadAndDisplayStreakCount()

        recyclerView = view.findViewById(R.id.rv_todays_dishes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recipeAdapter = RecipeAdapter(savedRecipes) { selectedRecipe ->
            // When an item is clicked, pass the recipe to the ViewModel
            detailedRecipeViewModel.selectRecipe(selectedRecipe)
            openDishFragment() // Open DishFragment after selecting the recipe
        }
        recyclerView.adapter = recipeAdapter
        recipeViewModel.recipes.observe(viewLifecycleOwner)
        {
            newRecipes ->
            //Log.d("MainPage", "Observed recipes with ingredients size: ${newRecipes.last().extendedIngredients.size}")
            savedRecipes.clear()
            savedRecipes.addAll(newRecipes)

            recipeAdapter.notifyDataSetChanged()

            recipeViewModel.saveRecipesToFile(requireContext())
        }
        //recipeViewModel.loadRecipesFromFile(requireContext())
        return view
    }

    fun addRecipe(recipe: Recipe) {
        Log.d("MainPage","dodaję recipe do mainpage")
        savedRecipes.add(recipe)
        Log.d("MainPage","dodaję recipe do mainpage")
        recipeAdapter.notifyItemInserted(savedRecipes.size - 1)
    }
    private fun openDishFragment() {
        val dishFragment = DishFragment() // No need to pass the recipe directly
        Log.d("MainPage","ingredients size in mainpage: ${savedRecipes[0].extendedIngredients.size}")
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, dishFragment) // Replace with your container ID
            .addToBackStack(null) // Enable back navigation
            .commit()
    }

    private fun loadAndDisplayStreakCount() {
        val streakCount = sharedPreferences.getInt("streakCount", 0)
        streakCountTextView.text = streakCount.toString()
    }
}