package com.emreberkgoger.foodrecipeapp.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emreberkgoger.foodrecipeapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import android.util.Log

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {
    private val viewModel: RecipeDetailViewModel by viewModels()
    private lateinit var missingIngredientsAdapter: MissingIngredientsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvRecipeName = view.findViewById<TextView>(R.id.tvRecipeName)
        val tvReadyInMinutes = view.findViewById<TextView>(R.id.tvReadyInMinutes)
        val tvServings = view.findViewById<TextView>(R.id.tvServings)
        val chipGroupDietTypes = view.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroupDietTypes)
        val tvIngredients = view.findViewById<TextView>(R.id.tvIngredients)
        val tvInstructions = view.findViewById<TextView>(R.id.tvInstructions)
        val tvMissingIngredients = view.findViewById<TextView>(R.id.tvMissingIngredients)

        val recipeId = arguments?.getLong(ARG_RECIPE_ID) ?: return
        viewModel.loadRecipeDetail(recipeId)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recipeDetail.collectLatest { detail ->
                try {
                    detail?.let {
                        tvRecipeName.text = it.title
                        tvReadyInMinutes.text = "Hazırlık: ${it.readyInMinutes ?: "-"} dk"
                        tvServings.text = "Kişi: ${it.servings ?: "-"}"
                        chipGroupDietTypes.removeAllViews()
                        it.supportedDietTypes?.forEach { diet ->
                            val chip = com.google.android.material.chip.Chip(requireContext())
                            chip.text = diet
                            chip.isClickable = false
                            chip.isCheckable = false
                            chipGroupDietTypes.addView(chip)
                        }
                        tvIngredients.text = it.ingredients?.joinToString(", ") { ing -> ing.name } ?: "-"
                        val cleanInstructions = it.instructions?.replace(Regex("<.*?>"), "")?.trim() ?: "-"
                        tvInstructions.text = cleanInstructions
                    }
                } catch (e: Exception) {
                    Log.e("RecipeDetailFragment", "Error binding recipe detail: ", e)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.missingIngredients.collectLatest { ingredients ->
                tvMissingIngredients.text = ingredients.joinToString(", ") { it.name }
            }
        }
    }

    companion object {
        private const val ARG_RECIPE_ID = "recipe_id"
        fun newInstance(recipeId: Long): RecipeDetailFragment {
            val fragment = RecipeDetailFragment()
            val args = Bundle()
            args.putLong(ARG_RECIPE_ID, recipeId)
            fragment.arguments = args
            return fragment
        }
    }
}