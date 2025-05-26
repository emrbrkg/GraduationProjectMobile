package com.emreberkgoger.foodrecipeapp.ui.ingredient

import android.app.DatePickerDialog
import android.os.Bundle
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

        btnAddIngredient.setOnClickListener {
            val name = actvIngredientName.text.toString().trim()
            val quantity = etQuantity.text.toString().toFloatOrNull()
            val unit = actvUnit.text.toString().trim().ifBlank { null }
            val expiryDate = etExpiryDate.text.toString().ifBlank { null }

            if (name.isBlank() || quantity == null || unit == null) {
                Toast.makeText(requireContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedIngredient = ingredientList.find { it.name.equals(name, ignoreCase = true) }
            val ingredientId = selectedIngredient?.id

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