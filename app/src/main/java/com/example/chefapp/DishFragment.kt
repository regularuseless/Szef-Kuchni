package com.example.chefapp

import android.content.Context
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class DishFragment : Fragment() {
    private val detailedRecipeViewModel: DetailedRecipeViewModel by activityViewModels()
    private var recipe: Recipe? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_dish, container, false)
        val dishImageView: ImageView = view.findViewById(R.id.iv_dish_image)
        val dishNameTextView: TextView = view.findViewById(R.id.tv_dish_name)
        val startCookingButton: Button = view.findViewById(R.id.btn_start_cooking)
        val dishCostTextView: TextView = view.findViewById(R.id.tv_dish_cost)
        val dishDifficultyTextView: TextView = view.findViewById(R.id.tv_dish_difficulty)
        val dishCaloriesTextView: TextView = view.findViewById(R.id.tv_dish_calories)
        val dishDescriptionTextView: TextView = view.findViewById(R.id.tv_dish_description)
        val dishDietTextView: TextView = view.findViewById(R.id.tv_diet_type)

        //val dishAllergensListView: ListView
        val ingredientsListView: ListView = view.findViewById(R.id.lv_ingredients)
        // Observe ViewModel correctly in onCreateView
        detailedRecipeViewModel.selectedRecipe.observe(viewLifecycleOwner) { selectedRecipe ->
            recipe = selectedRecipe
            Log.d("IngredientAdapter","Recipe ingredients: ${recipe?.extendedIngredients}")
            dishNameTextView.text = recipe?.title

            dishCostTextView.text = "${recipe?.pricePerServing.toString()} cents per serving"

            var diets = recipe?.diets?.joinToString(", ")?.lowercase()
            dishDietTextView.text = diets

            val calories = recipe?.nutrition?.nutrients?.getOrNull(0)?.amount
            dishCaloriesTextView.text = "${calories.toString()} calories"

            var summary = recipe?.summary.toString()
            dishDescriptionTextView.text = removeHtmlTags(summary)
            dishDifficultyTextView.text = "${recipe?.readyInMinutes.toString()} minutes"

            var ingredients = recipe?.extendedIngredients ?: emptyList()
            val extendedIngredientAdapter = ExtendedIngredientAdapter(requireContext(), ingredients)
            ingredientsListView.adapter = extendedIngredientAdapter

            // Load image safely
            recipe?.image?.let {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.placeholder_image)
                    .into(dishImageView)
            }
        }


        // Handle button click
        startCookingButton.setOnClickListener {
            openCookingInstructionFragment()
        }

        return view
    }

    private fun openCookingInstructionFragment() {
        val fragment = CookingInstructionFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
    fun removeHtmlTags(input: String): String {
        return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY).toString()
    }
}
class ExtendedIngredientAdapter(
    context: Context,
    private var ingredients: List<Ingredient>

) : ArrayAdapter<Ingredient>(context, 0, ingredients) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Log.d("IngredientAdapter", "populating at ${position}")
        val ingredient = getItem(position)
        Log.d("IngredientAdapter", "Ingredient at position $position: $ingredient")
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.extended_item_ingredient, parent, false)
        Log.d("IngredientAdapter", "Ingredient at position $position: $ingredient")
        val nameTextView: TextView = view.findViewById(R.id.tv_ingredient_name)
        val amountTextView: TextView = view.findViewById(R.id.tv_ingredient_amount)
        Log.d("IngredientAdapter", "Ingredient at position $position: $ingredient")
        nameTextView.text = ingredient?.name
        amountTextView.text = "${ingredient?.amount} ${ingredient?.unit}"
        Log.d("IngredientAdapter", "Ingredient at position $position: $ingredient")
        return view
    }
}
