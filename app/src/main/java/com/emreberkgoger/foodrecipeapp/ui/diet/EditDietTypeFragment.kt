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

class EditDietTypeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_diet_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupDietTypes)
        val btnSave = view.findViewById<Button>(R.id.btnSaveDietType)

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
            Toast.makeText(requireContext(), "Seçili diyet tipleri: ${'$'}selectedDietTypes", Toast.LENGTH_SHORT).show()
            // Burada seçili tipleri backend'e gönderebilirsin
        }
    }
} 