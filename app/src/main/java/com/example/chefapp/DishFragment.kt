package com.example.chefapp

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.gson.Gson

class DishFragment : Fragment() {
    private val detailedRecipeViewModel: DetailedRecipeViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private var recipe: Recipe? = null
    private lateinit var favoritesManager: FavoritesManager
    private lateinit var favButton: Button
    private lateinit var delButton: Button

    private inner class FavoritesManager(context: Context) {
        private val fileName = "favorites.json"
        private val gson = Gson()
        private val context: Context = context.applicationContext

        fun addFavorite(recipeId: Int) {
            val current = loadFavorites().toMutableList()
            if (!current.contains(recipeId)) {
                current.add(recipeId)
                saveFavorites(current)
            }
        }

        fun removeFavorite(recipeId: Int) {
            val current = loadFavorites().toMutableList()
            if (current.remove(recipeId)) {
                saveFavorites(current)
            }
        }

        private fun saveFavorites(recipeIds: List<Int>) {
            try {
                val jsonString = gson.toJson(recipeIds)
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                    it.write(jsonString.toByteArray())
                }
            } catch (e: Exception) {
                Log.e("FavoritesManager", "Błąd zapisu: ${e.message}")
            }
        }

        fun loadFavorites(): List<Int> {
            return try {
                val inputStream = context.openFileInput(fileName)
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                gson.fromJson(jsonString, Array<Int>::class.java)?.toList() ?: emptyList()
            } catch (e: Exception) {
                Log.e("FavoritesManager", "Błąd odczytu: ${e.message}")
                emptyList()
            }
        }

        fun isFavorite(recipeId: Int): Boolean {
            return loadFavorites().contains(recipeId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dish, container, false)
        favoritesManager = FavoritesManager(requireContext())

        // Inicjalizacja widoków
        val dishImageView: ImageView = view.findViewById(R.id.iv_dish_image)
        val dishNameTextView: TextView = view.findViewById(R.id.tv_dish_name)
        val startCookingButton: Button = view.findViewById(R.id.btn_start_cooking)
        val dishCostTextView: TextView = view.findViewById(R.id.tv_dish_cost)
        val dishDifficultyTextView: TextView = view.findViewById(R.id.tv_dish_difficulty)
        val dishCaloriesTextView: TextView = view.findViewById(R.id.tv_dish_calories)
        val dishDescriptionTextView: TextView = view.findViewById(R.id.tv_dish_description)
        val dishDietTextView: TextView = view.findViewById(R.id.tv_diet_type)
        val btnAddAllIngredients: Button = view.findViewById(R.id.btn_add_to_cart)
        val ingredientsListView: ListView = view.findViewById(R.id.lv_ingredients)
        favButton = view.findViewById(R.id.fav)
        delButton = view.findViewById(R.id.del)

        // Obsługa przycisków ulubionych
        favButton.setOnClickListener {
            recipe?.id?.let { recipeId ->
                favoritesManager.addFavorite(recipeId)
                Toast.makeText(context, "Dodano do ulubionych!", Toast.LENGTH_SHORT).show()
                updateFavoriteButtons()
            } ?: showRecipeIdError()
        }

        delButton.setOnClickListener {
            recipe?.id?.let { recipeId ->
                favoritesManager.removeFavorite(recipeId)
                Toast.makeText(context, "Usunięto z ulubionych!", Toast.LENGTH_SHORT).show()
                updateFavoriteButtons()
            } ?: showRecipeIdError()
        }

        // Obserwacja zmian przepisu
        detailedRecipeViewModel.selectedRecipe.observe(viewLifecycleOwner) { selectedRecipe ->
            recipe = selectedRecipe
            updateUI(dishImageView, dishNameTextView, dishCostTextView, dishDifficultyTextView,
                dishCaloriesTextView, dishDescriptionTextView, dishDietTextView, ingredientsListView)
            updateFavoriteButtons()
        }

        // Obsługa innych przycisków
        btnAddAllIngredients.setOnClickListener {
            recipe?.extendedIngredients?.let { ingredients ->
                cartViewModel.addIngredients(ingredients)
            }
        }

        startCookingButton.setOnClickListener {
            openCookingInstructionFragment()
        }

        return view
    }

    private fun updateUI(
        dishImageView: ImageView,
        dishNameTextView: TextView,
        dishCostTextView: TextView,
        dishDifficultyTextView: TextView,
        dishCaloriesTextView: TextView,
        dishDescriptionTextView: TextView,
        dishDietTextView: TextView,
        ingredientsListView: ListView
    ) {
        recipe?.let {
            dishNameTextView.text = it.title
            dishCostTextView.text = "${it.pricePerServing} cents per serving"
            dishDifficultyTextView.text = "${it.readyInMinutes} minutes"
            dishCaloriesTextView.text = "${it.nutrition?.nutrients?.getOrNull(0)?.amount} calories"
            dishDescriptionTextView.text = removeHtmlTags(it.summary ?: "")
            dishDietTextView.text = it.diets?.joinToString(", ")?.lowercase() ?: ""

            it.extendedIngredients?.let { ingredients ->
                ingredientsListView.adapter = ExtendedIngredientAdapter(requireContext(), ingredients)
            }

            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.placeholder_image)
                .into(dishImageView)
        }
    }

    private fun updateFavoriteButtons() {
        recipe?.id?.let { recipeId ->
            if (favoritesManager.isFavorite(recipeId)) {
                favButton.visibility = View.GONE
                delButton.visibility = View.VISIBLE
            } else {
                favButton.visibility = View.VISIBLE
                delButton.visibility = View.GONE
            }
        }
    }

    private fun showRecipeIdError() {
        Toast.makeText(context, "Błąd: brak ID przepisu", Toast.LENGTH_SHORT).show()
    }

    private fun openCookingInstructionFragment() {
        recipe?.id?.let { recipeId ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, CookingInstructionFragment.newInstance(recipeId))
                .addToBackStack(null)
                .commit()
        } ?: showRecipeIdError()
    }

    private fun removeHtmlTags(input: String): String {
        return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    private class ExtendedIngredientAdapter(
        context: Context,
        private var ingredients: List<Ingredient>
    ) : ArrayAdapter<Ingredient>(context, 0, ingredients) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.extended_item_ingredient, parent, false)

            getItem(position)?.let { ingredient ->
                view.findViewById<TextView>(R.id.tv_ingredient_name).text = ingredient.name
                view.findViewById<TextView>(R.id.tv_ingredient_amount).text =
                    "${ingredient.amount} ${ingredient.unit}"
            }

            return view
        }
    }
}