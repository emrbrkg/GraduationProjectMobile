package com.emreberkgoger.foodrecipeapp.ui.ingredient

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.data.repository.user.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IngredientListFragment : Fragment() {
    @Inject lateinit var userRepository: UserRepository
    private lateinit var adapter: IngredientAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingredient_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvIngredients = view.findViewById<RecyclerView>(R.id.rvIngredients)
        val btnAddIngredient = view.findViewById<Button>(R.id.btnAddIngredient)
        val layoutAddOptions = view.findViewById<View>(R.id.layoutAddOptions)
        val btnScanReceipt = view.findViewById<Button>(R.id.btnScanReceipt)
        val btnAddManualIngredient = view.findViewById<Button>(R.id.btnAddManualIngredient)
        adapter = IngredientAdapter(emptyList())
        rvIngredients.layoutManager = LinearLayoutManager(requireContext())
        rvIngredients.adapter = adapter

        // Malzeme listesini backend'den çek
        lifecycleScope.launch {
            try {
                val response = userRepository.getUserIngredients()
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateData(response.body()!!)
                } else {
                    var errorMsg = response.errorBody()?.string() ?: response.message()
                    errorMsg = errorMsg.replace("\n", " ").take(200)
                    Log.e("INGREDIENT_LIST_ERROR", errorMsg)
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                val errorMsg = (e.localizedMessage ?: "Bilinmeyen hata").replace("\n", " ").take(200)
                Log.e("INGREDIENT_LIST_ERROR", errorMsg)
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        // Yeni malzeme ekleme butonu (varsa) için tıklama işlemi burada eklenebilir
        btnAddIngredient.setOnClickListener {
            layoutAddOptions.visibility = if (layoutAddOptions.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        btnScanReceipt.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, com.emreberkgoger.foodrecipeapp.ui.ocr.OcrScanFragment())
                .addToBackStack(null)
                .commit()
            layoutAddOptions.visibility = View.GONE
        }

        btnAddManualIngredient.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, com.emreberkgoger.foodrecipeapp.ui.ingredient.IngredientAddFragment())
                .addToBackStack(null)
                .commit()
            layoutAddOptions.visibility = View.GONE
        }
    }
}