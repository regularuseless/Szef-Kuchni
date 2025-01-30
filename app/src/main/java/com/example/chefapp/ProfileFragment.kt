package com.example.chefapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var favoritesManager: FavoritesManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private val apiKey = "26a6b66669354a3fab1a34af17b17baf"
    private val detailedRecipeViewModel: DetailedRecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inicjalizacja RecyclerView
        recyclerView = view.findViewById(R.id.ulubione)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recipeAdapter = RecipeAdapter(emptyList()) { recipe ->
            // Przekazanie przepisu do ViewModel i nawigacja
            detailedRecipeViewModel.selectRecipe(recipe)
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, DishFragment())
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = recipeAdapter

        // Inicjalizacja menedżera ulubionych
        favoritesManager = FavoritesManager(requireContext())

        // Załaduj dane profilu
        loadProfileData(view)
        loadFavoriteRecipes()

        return view
    }

    private fun loadProfileData(view: View) {
        val llAllergensList = view.findViewById<LinearLayout>(R.id.ll_allergens_list)
        llAllergensList.removeAllViews()
        loadSelectedAllergens().forEach { allergen ->
            llAllergensList.addView(createProfileTextView(allergen))
        }

        val llDietsList = view.findViewById<LinearLayout>(R.id.ll_diets_list)
        llDietsList.removeAllViews()
        loadSelectedDiets().forEach { diet ->
            llDietsList.addView(createProfileTextView(diet))
        }
    }

    private fun createProfileTextView(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 16f
            setPadding(0, 8, 0, 8)
        }
    }

    private fun loadFavoriteRecipes() {
        lifecycleScope.launch {
            try {
                val favoriteIds = withContext(Dispatchers.IO) {
                    favoritesManager.loadFavorites()
                }

                if (favoriteIds.isEmpty()) {
                    showEmptyState()
                    return@launch
                }

                showLoading()

                val recipes = mutableListOf<Recipe>()
                favoriteIds.forEach { recipeId ->
                    try {
                        val response = getRecipeDetails(recipeId)
                        if (response.isSuccessful) {
                            response.body()?.let { details ->
                                recipes.add(mapToRecipe(details))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileFragment", "Błąd przy przepisie $recipeId: ${e.message}")
                    }
                }

                withContext(Dispatchers.Main) {
                    if (recipes.isNotEmpty()) {
                        showRecipes(recipes)
                    } else {
                        showError()
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Ogólny błąd: ${e.message}")
                showError()
            }
        }
    }

    private suspend fun getRecipeDetails(recipeId: Int): Response<RecipeDetails> {
        return withContext(Dispatchers.IO) {
            RetrofitInstance.api.getRecipeDetails(
                recipeId,
                true,
                apiKey
            ).execute()
        }
    }

    private fun mapToRecipe(details: RecipeDetails): Recipe {
        return Recipe(
            id = details.id,
            title = details.title,
            image = details.image,
            readyInMinutes = details.readyInMinutes,
            pricePerServing = details.pricePerServing,
            nutrition = details.nutrition,
            sourceName = details.sourceName ?: "",
            summary = details.summary ?: "",
            diets = details.diets ?: emptyList(),
            extendedIngredients = details.extendedIngredients
        )
    }

    private fun showRecipes(recipes: List<Recipe>) {
        recyclerView.visibility = View.VISIBLE
        recipeAdapter.updateRecipes(recipes)
    }

    private fun showLoading() {
        // Implementacja ładowania
    }

    private fun showError() {
        // Implementacja błędu
    }

    private fun showEmptyState() {
        // Implementacja pustego stanu
    }

    private fun loadSelectedAllergens(): Set<String> {
        val file = File(requireContext().filesDir, "allergens.json")
        return if (file.exists()) {
            Gson().fromJson(file.readText(), Set::class.java) as Set<String>
        } else emptySet()
    }

    private fun loadSelectedDiets(): Set<String> {
        val file = File(requireContext().filesDir, "diets.json")
        return if (file.exists()) {
            Gson().fromJson(file.readText(), Set::class.java) as Set<String>
        } else emptySet()
    }

    private inner class FavoritesManager(context: Context) {
        private val fileName = "favorites.json"
        private val gson = Gson()
        private val context = context.applicationContext

        fun loadFavorites(): List<Int> {
            return try {
                val inputStream = context.openFileInput(fileName)
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                gson.fromJson(jsonString, Array<Int>::class.java)?.toList() ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}