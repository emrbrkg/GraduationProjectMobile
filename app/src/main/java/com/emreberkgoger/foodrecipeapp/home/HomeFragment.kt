package com.emreberkgoger.foodrecipeapp.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.ui.auth.LoginFragment
import com.emreberkgoger.foodrecipeapp.ui.diet.EditDietTypeFragment
import com.emreberkgoger.foodrecipeapp.ui.ocr.OcrScanFragment
import com.emreberkgoger.foodrecipeapp.ui.ingredient.IngredientAddFragment
import com.emreberkgoger.foodrecipeapp.ui.ingredient.IngredientListFragment
import com.emreberkgoger.foodrecipeapp.ui.recipe.RecipeSuggestFragment
import com.emreberkgoger.foodrecipeapp.ui.profile.ProfileUpdateFragment
import javax.inject.Inject

class HomeFragment : Fragment() {
    @Inject lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btnEditDietType).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, EditDietTypeFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<Button>(R.id.btnScanReceipt).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, OcrScanFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<Button>(R.id.btnAddIngredient).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, IngredientAddFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<Button>(R.id.btnMyIngredients).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, IngredientListFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<Button>(R.id.btnSuggestRecipe).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecipeSuggestFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<Button>(R.id.btnUpdateProfile).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileUpdateFragment())
                .addToBackStack(null)
                .commit()
        }
        val logoutButton = view.findViewById<Button?>(R.id.logoutButton)
        logoutButton?.setOnClickListener {
            sharedPreferences.edit().remove("jwt_token").apply()
            Toast.makeText(requireContext(), "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .commit()
        }
    }
}