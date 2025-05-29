package com.emreberkgoger.foodrecipeapp.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
        val etNewPasswordRepeat = view.findViewById<EditText>(R.id.etProfileNewPasswordRepeat)
        val btnSave = view.findViewById<Button>(R.id.btnSaveProfile)

        // Mevcut kullanıcı bilgilerini backend'den çek ve alanlara yerleştir
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = userRepository.getCurrentUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    etUserName.setText(profile.userName)
                    etFirstName.setText("")
                    etLastName.setText("")
                    etEmail.setText(profile.email)
                }
            } catch (e: Exception) {
                Log.e("PROFILE_FETCH_ERROR", e.localizedMessage ?: "Bilinmeyen hata")
            }
        }

        // Kullanıcı bir alana tıkladığında eski veri silinsin (sadece ilk tıklamada)
        fun setClearOnFirstFocus(editText: EditText) {
            var cleared = false
            editText.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus && !cleared) {
                    editText.text.clear()
                    cleared = true
                }
            }
        }
        setClearOnFirstFocus(etUserName)
        setClearOnFirstFocus(etFirstName)
        setClearOnFirstFocus(etLastName)
        setClearOnFirstFocus(etEmail)

        btnSave.setOnClickListener {
            val userName = etUserName.text.toString()
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val email = etEmail.text.toString()
            val newPassword = etNewPassword.text.toString()
            val newPasswordRepeat = etNewPasswordRepeat.text.toString()

            // Şifre alanı doluysa ve iki şifre aynı değilse uyarı ver
            if (newPassword.isNotBlank() && newPassword != newPasswordRepeat) {
                Toast.makeText(requireContext(), "Şifreler aynı olmalı!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updateDto = UserUpdateDto(
                userName = userName,
                firstName = firstName,
                lastName = lastName,
                email = email,
                newPassword = if (newPassword.isNotBlank()) newPassword else null
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