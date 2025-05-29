package com.emreberkgoger.foodrecipeapp.ui.ingredient

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.data.dto.request.UserIngredientAddDto
import com.emreberkgoger.foodrecipeapp.data.dto.response.IngredientDto
import com.emreberkgoger.foodrecipeapp.data.dto.response.UserIngredientResponseDto
import com.emreberkgoger.foodrecipeapp.data.repository.ingredient.IngredientRepository
import com.emreberkgoger.foodrecipeapp.data.repository.user.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class IngredientAddFragment : Fragment() {
    @Inject lateinit var ingredientRepository: IngredientRepository
    @Inject lateinit var userRepository: UserRepository

    private var ingredientList: List<IngredientDto> = emptyList()
    private val unitList = listOf("g", "ml", "adet", "kg", "lt")
    private var editIngredient: UserIngredientResponseDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editIngredient = arguments?.getSerializable(ARG_EDIT_INGREDIENT) as? UserIngredientResponseDto
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingredient_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actvIngredientName = view.findViewById<AutoCompleteTextView>(R.id.actvIngredientName)
        val etQuantity = view.findViewById<EditText>(R.id.etQuantity)
        val actvUnit = view.findViewById<AutoCompleteTextView>(R.id.actvUnit)
        val etExpiryDate = view.findViewById<EditText>(R.id.etExpiryDate)
        val btnAddIngredient = view.findViewById<Button>(R.id.btnAddIngredient)

        // Birim için autocomplete
        actvUnit.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, unitList))

        // Malzeme listesini çek ve autocomplete'e ver
        lifecycleScope.launch {
            val response = ingredientRepository.getAllIngredients()
            if (response.isSuccessful && response.body() != null) {
                ingredientList = response.body()!!
                val names = ingredientList.map { it.name }
                actvIngredientName.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names))
            } else {
                Toast.makeText(requireContext(), "Malzeme listesi alınamadı", Toast.LENGTH_SHORT).show()
            }
        }

        // Tarih seçici
        etExpiryDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val m = (month + 1).toString().padStart(2, '0')
                val d = dayOfMonth.toString().padStart(2, '0')
                etExpiryDate.setText("$year-$m-$d")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Eğer edit modundaysa alanları doldur
        editIngredient?.let { ingredient ->
            actvIngredientName.setText(ingredient.ingredientName ?: "")
            etQuantity.setText(ingredient.quantity?.toString() ?: "")
            actvUnit.setText(ingredient.unit ?: "")
            etExpiryDate.setText(ingredient.expiryDate ?: "")
            btnAddIngredient.text = "Güncelle"
        }

        btnAddIngredient.setOnClickListener {
            val name = actvIngredientName.text.toString().trim()
            val quantity = etQuantity.text.toString().toFloatOrNull()
            val unit = actvUnit.text.toString().trim().ifBlank { null }
            val expiryDate = etExpiryDate.text.toString().ifBlank { null }

            if (name.isBlank() || quantity == null || unit == null) {
                Toast.makeText(requireContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ekleme veya güncelleme için ingredientId belirle
            val selectedIngredient = ingredientList.find { it.name.equals(name, ignoreCase = true) }
            val ingredientId = selectedIngredient?.id ?: editIngredient?.ingredientId

            if (ingredientId == null) {
                Toast.makeText(requireContext(), "Malzeme bulunamadı", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dto = UserIngredientAddDto(
                ingredientId = ingredientId,
                ingredientName = name,
                quantity = quantity,
                unit = unit,
                expiryDate = expiryDate
            )

            lifecycleScope.launch {
                if (editIngredient != null) {
                    // Güncelleme
                    try {
                        val response = userRepository.updateUserIngredient(editIngredient!!.id, dto)
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Malzeme güncellendi", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        } else {
                            val errorMsg = response.errorBody()?.string() ?: response.message()
                            Log.e("INGREDIENT_UPDATE_ERROR", errorMsg)
                            Toast.makeText(requireContext(), "Güncelleme başarısız: $errorMsg", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        val errorMsg = e.localizedMessage ?: "Bilinmeyen hata"
                        Log.e("INGREDIENT_UPDATE_ERROR", errorMsg)
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Ekleme
                    val response = userRepository.addUserIngredient(dto)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Malzeme eklendi", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Ekleme başarısız: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_EDIT_INGREDIENT = "edit_ingredient"
        fun newInstanceForEdit(ingredient: UserIngredientResponseDto): IngredientAddFragment {
            val fragment = IngredientAddFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_EDIT_INGREDIENT, ingredient)
            fragment.arguments = bundle
            return fragment
        }
    }
} 