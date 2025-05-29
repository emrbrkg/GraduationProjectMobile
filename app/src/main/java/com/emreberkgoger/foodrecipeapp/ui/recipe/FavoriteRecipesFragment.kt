package com.emreberkgoger.foodrecipeapp.ui.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.data.model.recipe.Recipe
import com.emreberkgoger.foodrecipeapp.ui.recipe.RecipeDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {
    private val viewModel: FavoriteRecipesViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewFavoriteRecipes)
        recipeAdapter = RecipeAdapter(
            onClick = { recipe ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, RecipeDetailFragment.newInstance(recipe.id))
                    .addToBackStack(null)
                    .commit()
            },
            onFavoriteClick = { recipe, shouldFavorite ->
                if (shouldFavorite) {
                    viewModel.addFavorite(recipe)
                } else {
                    viewModel.removeFavorite(recipe)
                }
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recipeAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteRecipes.collect { recipes ->
                recipeAdapter.updateData(recipes)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }

        viewModel.fetchFavoriteRecipes()
    }
} 