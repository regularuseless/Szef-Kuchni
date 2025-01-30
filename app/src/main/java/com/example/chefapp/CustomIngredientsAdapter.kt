package com.example.chefapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomIngredientsAdapter(private val ingredients: List<Ingredient>) : RecyclerView.Adapter<CustomIngredientsAdapter.IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient_custom, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient)
    }

    override fun getItemCount(): Int = ingredients.size

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tvIngredientNameCustom)
        private val amountTextView: TextView = itemView.findViewById(R.id.tvQuantityCustom)
        private val unitTextView: TextView = itemView.findViewById(R.id.tvUnitCustom)

        fun bind(ingredient: Ingredient) {
            nameTextView.text = ingredient.name
            amountTextView.text = ingredient.amount.toString()
            unitTextView.text = ingredient.unit
        }
    }
}
