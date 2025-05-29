package com.emreberkgoger.foodrecipeapp.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.data.model.recipe.Recipe
import com.emreberkgoger.foodrecipeapp.data.model.user.DietType
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.flow.collectLatest
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast

@AndroidEntryPoint
class RecipeSearchFragment : Fragment() {
    private val viewModel: RecipeSearchViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val chipGroupSearchType = view.findViewById<ChipGroup>(R.id.chipGroupSearchType)
        val multiAutoDietTypes = view.findViewById<MultiAutoCompleteTextView>(R.id.multiAutoDietTypes)
        val cbOwnIngredients = view.findViewById<CheckBox>(R.id.cbOwnIngredients)
        val cbFavorites = view.findViewById<CheckBox>(R.id.cbFavorites)
        val btnSearch = view.findViewById<Button>(R.id.btnSearch)
        val btnNextPage = view.findViewById<Button>(R.id.btnNextPage)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewRecipes)
        val etMaxReadyTime = view.findViewById<EditText>(R.id.etMaxReadyTime)

        recipeAdapter = RecipeAdapter(
            onClick = { recipe ->
                Log.d("RecipeSearch", "Tarif kartı tıklandı: ${recipe.title}")
                Toast.makeText(requireContext(), "Tarif: ${recipe.title}", Toast.LENGTH_SHORT).show()
                viewModel.onRecipeClicked(recipe)
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

        // Tüm diyet tiplerini enumdan al
        val allDietTypes = DietType.values().map { it.name }
        val allDietTypeDisplayNames = DietType.values().map { it.displayName }
        val displayNameToEnum = DietType.values().associateBy { it.displayName }
        val enumToDisplayName = DietType.values().associateBy({ it.name }, { it.displayName })
        val displayNameToApiName = DietType.values().associateBy({ it.displayName }, { it.apiName })

        // Diyet tiplerini doldur ve kullanıcınınkileri seçili yap
        viewModel.userDietTypes.observe(viewLifecycleOwner) { userDietTypes ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, allDietTypeDisplayNames)
            multiAutoDietTypes.setAdapter(adapter)
            multiAutoDietTypes.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
            // Kullanıcının diyet tiplerini otomatik seçili göster
            val preselect = userDietTypes.mapNotNull { enumToDisplayName[it] }
            multiAutoDietTypes.setText(preselect.joinToString(", "))
        }

        btnSearch.setOnClickListener {
            val searchText = etSearch.text.toString()
            val searchType = when (chipGroupSearchType.checkedChipId) {
                R.id.chipRecipeName -> "recipe"
                R.id.chipIngredientName -> "ingredient"
                else -> "recipe"
            }
            val selectedDietTypes = multiAutoDietTypes.text.toString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .mapNotNull { displayNameToApiName[it] }
            val onlyOwnIngredients = cbOwnIngredients.isChecked
            val onlyFavorites = cbFavorites.isChecked
            val maxReadyTime = etMaxReadyTime.text.toString().toIntOrNull()
            Log.d("RecipeSearch", "Arama: searchText=$searchText, searchType=$searchType, dietTypes=$selectedDietTypes, ownIngredients=$onlyOwnIngredients, favorites=$onlyFavorites, maxReadyTime=$maxReadyTime")
            Toast.makeText(requireContext(), "Arama yapılıyor...", Toast.LENGTH_SHORT).show()
            viewModel.searchRecipes(searchType, searchText, selectedDietTypes, onlyOwnIngredients, onlyFavorites, maxReadyTime, resetPage = true)
        }

        btnNextPage.setOnClickListener {
            Log.d("RecipeSearch", "Daha fazla göster tıklandı")
            Toast.makeText(requireContext(), "Daha fazla tarif yükleniyor...", Toast.LENGTH_SHORT).show()
            viewModel.loadNextPage()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recipes.collectLatest { recipes ->
                Log.d("RecipeSearch", "Tarif listesi güncellendi. Toplam: ${recipes.size}")
                Toast.makeText(requireContext(), "${recipes.size} tarif bulundu", Toast.LENGTH_SHORT).show()
                recipeAdapter.submitList(recipes)
            }
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                Log.d("RecipeSearch", "Detay ekranına geçiliyor: ${it.title}")
                Toast.makeText(requireContext(), "Detay: ${it.title}", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, RecipeDetailFragment.newInstance(it.id))
                    .addToBackStack(null)
                    .commit()
                viewModel.onNavigatedToDetail()
            }
        }

        // Hata mesajı gözlemi
        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }
} 