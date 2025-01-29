package com.example.chefapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(
    private var recipes: List<Recipe>,
    private val onRecipeClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishImage: ImageView = itemView.findViewById(R.id.iv_dish_image)
        val dishName: TextView = itemView.findViewById(R.id.tv_dish_name)
        val dishDifficulty: TextView = itemView.findViewById(R.id.tv_dish_difficulty)
        val dishCost: TextView = itemView.findViewById(R.id.tv_dish_cost)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRecipeClick(recipes[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dish, parent, false)
        Log.d("RecipeAdapter", "Tworzenie nowego ViewHolder")
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        holder.dishName.text = recipe.title
        holder.dishDifficulty.text = "Czas gotowania: ${recipe.readyInMinutes} min"
        holder.dishCost.text = "Koszt: ${String.format("%.2f", recipe.pricePerServing)} ¢"

        Glide.with(holder.dishImage.context)
            .load(recipe.image)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.dishImage)

        Log.d("RecipeAdapter", "Wyświetlanie przepisu: ${recipe.title}")
    }

    override fun getItemCount() = recipes.size

    fun updateRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
        Log.d("RecipeAdapter", "Zaktualizowano listę przepisów, nowa liczba: ${recipes.size}")
    }
}