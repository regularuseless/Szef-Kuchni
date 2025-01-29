package com.example.chefapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(private val recipes: List<Recipe>, private val onRecipeClick: (Recipe) -> Unit /* Lambda do obsługi kliknięć) */) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    // ViewHolder class to hold the view elements
    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishImage: ImageView = view.findViewById(R.id.iv_dish_image)
        val dishName: TextView = view.findViewById(R.id.tv_dish_name)
        val dishDifficulty: TextView = view.findViewById(R.id.tv_dish_difficulty)
        val dishCost: TextView = view.findViewById(R.id.tv_dish_cost)
    }

    // Create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dish, parent, false)
        Log.d("ShowRecipes","start")
        return RecipeViewHolder(view)
    }

    // Bind the data to the view
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        Log.d("ShowRecipes","cokolwiek: ${recipe.title}")
        // Set the data to the views
        holder.dishName.text = recipe.title
        // For the difficulty, you could determine it based on your own criteria.
        //holder.dishDifficulty.text = "Difficulty: ${recipe.usedIngredientCount ?: "N/A"}"
        //holder.dishCost.text = "Cost: ${recipe.missedIngredientCount ?: "N/A"}"

        // Obsługa kliknięcia na element
        holder.itemView.setOnClickListener {
            onRecipeClick(recipe) //wywołanie lambdy z kliknietym przepisem
        }

        // Załaduj obraz za pomocą Glide
        Glide.with(holder.dishImage.context)
            .load(recipe.image) // URL obrazu z modelu
            .placeholder(R.drawable.placeholder_image) // Obraz zastępczy
            //.error(R.drawable.error_image) // Obraz błędu
            .into(holder.dishImage)
    }

    // Return the total item count
    override fun getItemCount(): Int {
        return recipes.size
    }
}
