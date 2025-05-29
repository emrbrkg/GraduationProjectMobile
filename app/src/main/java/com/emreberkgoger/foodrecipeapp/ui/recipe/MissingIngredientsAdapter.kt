package com.emreberkgoger.foodrecipeapp.ui.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.data.dto.response.IngredientDto

class MissingIngredientsAdapter : ListAdapter<IngredientDto, MissingIngredientsAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_missing_ingredient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ingredient: IngredientDto) {
            itemView.findViewById<TextView>(R.id.tvIngredientName).text = ingredient.name
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<IngredientDto>() {
        override fun areItemsTheSame(oldItem: IngredientDto, newItem: IngredientDto): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: IngredientDto, newItem: IngredientDto): Boolean = oldItem == newItem
    }
} 