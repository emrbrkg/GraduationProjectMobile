package com.emreberkgoger.foodrecipeapp.ui.ingredient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.data.dto.response.UserIngredientResponseDto

class IngredientAdapter(
    private var items: List<UserIngredientResponseDto>
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    fun updateData(newItems: List<UserIngredientResponseDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvIngredientName)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvIngredientAmount)

        fun bind(item: UserIngredientResponseDto) {
            tvName.text = item.ingredientName ?: "İsim yok"
            tvAmount.text = if (item.quantity != null && item.unit != null) {
                "${item.quantity} ${item.unit}"
            } else {
                "Miktar yok"
            }
        }
    }
} 