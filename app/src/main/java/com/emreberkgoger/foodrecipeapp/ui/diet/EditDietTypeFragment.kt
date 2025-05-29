package com.emreberkgoger.foodrecipeapp.ui.diet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import androidx.fragment.app.Fragment
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.data.model.user.DietType
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log

@AndroidEntryPoint
class EditDietTypeFragment : Fragment() {
    private val viewModel: DietTypeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_diet_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            super.onViewCreated(view, savedInstanceState)
            val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupDietTypes)
            val btnSave = view.findViewById<Button>(R.id.btnSaveDietType)
            val tvCurrentDietType = view.findViewById<android.widget.TextView>(R.id.tvCurrentDietType)

            // Mevcut diyet tipini gözlemle ve göster
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.currentDietTypes.collect { dietTypes ->
                    val text = if (dietTypes.isNullOrEmpty()) {
                        "Mevcut Diyet Tipi: -"
                    } else {
                        val displayNames = dietTypes.mapNotNull { typeName ->
                            DietType.values().find { it.name == typeName }?.displayName
                        }
                        "Mevcut Diyet Tipi: ${displayNames.joinToString(", \n")}"
                    }
                    tvCurrentDietType.text = text
                }
            }
            viewModel.fetchCurrentDietTypes()

            // Enum'dan ChipGroup'u doldur
            DietType.values().forEach { dietType ->
                val chip = Chip(requireContext())
                chip.text = dietType.displayName
                chip.isCheckable = true
                chip.tag = dietType.name
                chipGroup.addView(chip)
            }

            btnSave.setOnClickListener {
                val selectedDietTypes = mutableListOf<String>()
                for (i in 0 until chipGroup.childCount) {
                    val chip = chipGroup.getChildAt(i) as Chip
                    if (chip.isChecked) {
                        selectedDietTypes.add(chip.tag as String)
                    }
                }
                viewModel.updateDietPreferences(selectedDietTypes)
            }

            // Güncelleme sonucunu gözlemle
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.updateResult.collect { result ->
                    result?.let {
                        if (it.isSuccess) {
                            Toast.makeText(requireContext(), "Diyet tipleri güncellendi", Toast.LENGTH_SHORT).show()
                            viewModel.clearUpdateResult()
                        } else {
                            val errorMsg = it.exceptionOrNull()?.message ?: "Bilinmeyen hata"
                            Toast.makeText(requireContext(), "Güncelleme başarısız: $errorMsg", Toast.LENGTH_SHORT).show()
                            Log.e("EditDietTypeFragment", "Diyet tipi güncelleme hatası", it.exceptionOrNull())
                            viewModel.clearUpdateResult()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("EditDietTypeFragment", "onViewCreated error", e)
        }
    }
} 