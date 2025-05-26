package com.emreberkgoger.foodrecipeapp.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.data.dto.request.UserUpdateDto
import com.emreberkgoger.foodrecipeapp.data.model.user.DietType
import com.emreberkgoger.foodrecipeapp.data.repository.user.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileUpdateFragment : Fragment() {
    @Inject lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val etUserName = view.findViewById<EditText>(R.id.etProfileUserName)
        val etFirstName = view.findViewById<EditText>(R.id.etProfileFirstName)
        val etLastName = view.findViewById<EditText>(R.id.etProfileLastName)
        val etEmail = view.findViewById<EditText>(R.id.etProfileEmail)
        val etNewPassword = view.findViewById<EditText>(R.id.etProfileNewPassword)
        val btnSave = view.findViewById<Button>(R.id.btnSaveProfile)

        // Diyet tipi ChipGroup'u ekle (dinamik)
        val chipGroup = ChipGroup(requireContext())
        chipGroup.id = View.generateViewId()
        val parent = view as ViewGroup
        parent.addView(chipGroup, parent.childCount - 1) // Kaydet butonundan önce ekle
        val chipGroupLayoutParams = chipGroup.layoutParams as ViewGroup.MarginLayoutParams
        chipGroupLayoutParams.setMargins(0, 0, 0, 24)
        chipGroup.layoutParams = chipGroupLayoutParams

        DietType.values().forEach { dietType ->
            val chip = Chip(requireContext())
            chip.text = dietType.displayName
            chip.isCheckable = true
            chip.tag = dietType.name
            chipGroup.addView(chip)
        }

        btnSave.setOnClickListener {
            val userName = etUserName.text.toString()
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val email = etEmail.text.toString()
            val newPassword = etNewPassword.text.toString()
            val selectedDietTypes = mutableListOf<String>()
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (chip.isChecked) {
                    selectedDietTypes.add(chip.tag as String)
                }
            }
            val updateDto = UserUpdateDto(
                userName = userName,
                firstName = firstName,
                lastName = lastName,
                email = email,
                newPassword = if (newPassword.isNotBlank()) newPassword else null,
                // dietTypes = selectedDietTypes // Eğer backend bekliyorsa açabilirsin
            )
            lifecycleScope.launch {
                try {
                    val response = userRepository.updateUserProfile(updateDto)
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(requireContext(), "Profil güncellendi!", Toast.LENGTH_SHORT).show()
                    } else {
                        var errorMsg = response.errorBody()?.string() ?: response.message()
                        errorMsg = errorMsg.replace("\n", " ").take(200)
                        Log.e("PROFILE_UPDATE_ERROR", errorMsg)
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    val errorMsg = (e.localizedMessage ?: "Bilinmeyen hata").replace("\n", " ").take(200)
                    Log.e("PROFILE_UPDATE_ERROR", errorMsg)
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
} 