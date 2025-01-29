package com.example.chefapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class DietAdapter(private val diets: List<String>) : RecyclerView.Adapter<DietAdapter.DietViewHolder>() {

    private val selectedDiets = mutableSetOf<String>()

    inner class DietViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.cb_diet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diet, parent, false)
        return DietViewHolder(view)
    }

    override fun onBindViewHolder(holder: DietViewHolder, position: Int) {
        val diet = diets[position]
        holder.checkBox.text = diet
        holder.checkBox.isChecked = selectedDiets.contains(diet)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedDiets.add(diet)
            } else {
                selectedDiets.remove(diet)
            }
        }
    }

    override fun getItemCount(): Int {
        return diets.size
    }

    fun getSelectedDiets(): Set<String> {
        return selectedDiets
    }

    fun setSelectedDiets(selectedDiets: Set<String>) {
        this.selectedDiets.clear()
        this.selectedDiets.addAll(selectedDiets)

    }
}