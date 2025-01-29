package com.example.chefapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class AllergenAdapter(private val allergens: List<String>) : RecyclerView.Adapter<AllergenAdapter.AllergenViewHolder>() {

    private val selectedAllergens = mutableSetOf<String>()

    inner class AllergenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.cb_allergen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_allergen, parent, false)
        return AllergenViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllergenViewHolder, position: Int) {
        val allergen = allergens[position]
        holder.checkBox.text = allergen
        holder.checkBox.isChecked = selectedAllergens.contains(allergen)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedAllergens.add(allergen)
            } else {
                selectedAllergens.remove(allergen)
            }
        }
    }

    override fun getItemCount(): Int {
        return allergens.size
    }

    fun getSelectedAllergens(): Set<String> {
        return selectedAllergens
    }

    fun setSelectedAllergens(selectedAllergens: Set<String>) {
        this.selectedAllergens.clear()
        this.selectedAllergens.addAll(selectedAllergens)
    }
}