package com.emreberkgoger.foodrecipeapp.ui.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.data.model.recipe.Recipe

class RecipeAdapter(
    private val onClick: (Recipe) -> Unit,
    private val onFavoriteClick: (Recipe, Boolean) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateData(newList: List<Recipe>) {
        submitList(newList)
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(recipe: Recipe) {
            itemView.findViewById<TextView>(R.id.tvRecipeName).text = recipe.title
            itemView.findViewById<TextView>(R.id.tvRecipeDescription).text = ""
            itemView.setOnClickListener { onClick(recipe) }
            val btnFavorite = itemView.findViewById<android.widget.ImageButton>(R.id.btnFavorite)
            if (recipe.isFavorite == true) {
                btnFavorite.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                btnFavorite.setImageResource(android.R.drawable.btn_star_big_off)
            }
            btnFavorite.setOnClickListener {
                onFavoriteClick(recipe, recipe.isFavorite != true)
            }
        }
    }
}

class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean = oldItem == newItem
} 