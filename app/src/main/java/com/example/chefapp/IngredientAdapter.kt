package com.example.chefapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientAdapter(
    private val ingredients: MutableList<Ingredient_test>,
    private val onQuantityChanged: (Ingredient_test) -> Unit // Callback for when quantity changes
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvIngredientName)
        val quantityTextView: TextView = itemView.findViewById(R.id.tvQuantity)
        val minusButton: ImageButton = itemView.findViewById(R.id.btnMinus)
        val plusButton: ImageButton = itemView.findViewById(R.id.btnPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]

        // Bind data to views
        holder.nameTextView.text = ingredient.name
        holder.quantityTextView.text = ingredient.quantity.toString()

        // Set up button click listeners
        holder.minusButton.setOnClickListener {
            if (ingredient.quantity > 0) {
                ingredient.quantity--
                notifyItemChanged(position) // Update the UI for this item
                onQuantityChanged(ingredient) // Trigger callback
            }
        }

        holder.plusButton.setOnClickListener {
            ingredient.quantity++
            notifyItemChanged(position) // Update the UI for this item
            onQuantityChanged(ingredient) // Trigger callback
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}
