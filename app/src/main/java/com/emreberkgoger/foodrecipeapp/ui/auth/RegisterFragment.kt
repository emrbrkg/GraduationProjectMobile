package com.emreberkgoger.foodrecipeapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.data.model.user.DietType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val userNameEditText = view.findViewById<EditText>(R.id.userNameEditText)
        val firstNameEditText = view.findViewById<EditText>(R.id.firstNameEditText)
        val lastNameEditText = view.findViewById<EditText>(R.id.lastNameEditText)
        val dietTypeChipGroup = view.findViewById<ChipGroup>(R.id.dietTypeChipGroup)
        val registerButton = view.findViewById<Button>(R.id.registerButton)
        val goToLoginText = view.findViewById<TextView>(R.id.goToLoginText)

        // Diyet tiplerini ChipGroup'a ekle
        DietType.values().forEach { dietType ->
            val chip = Chip(requireContext())
            chip.text = dietType.displayName
            chip.isCheckable = true
            chip.tag = dietType.name
            dietTypeChipGroup.addView(chip)
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val userName = userNameEditText.text.toString()
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val selectedDietTypes = mutableListOf<String>()
            for (i in 0 until dietTypeChipGroup.childCount) {
                val chip = dietTypeChipGroup.getChildAt(i) as Chip
                if (chip.isChecked) {
                    selectedDietTypes.add(chip.tag as String)
                }
            }
            viewModel.register(email, password, userName, firstName, lastName, selectedDietTypes)
        }

        goToLoginText.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.registerState.collectLatest { state ->
                when (state) {
                    is AuthState.Loading -> {
                        // Progress göster
                    }
                    is AuthState.Success -> {
                        Toast.makeText(requireContext(), "Kayıt başarılı", Toast.LENGTH_SHORT).show()
                    }
                    is AuthState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}