package com.emreberkgoger.foodrecipeapp.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
        val tvUserName = view.findViewById<TextView>(R.id.tvProfileUserName)
        val etUserName = view.findViewById<EditText>(R.id.etProfileUserName)
        val ivEditUserName = view.findViewById<ImageView>(R.id.ivEditUserName)
        val tvFirstName = view.findViewById<TextView>(R.id.tvProfileFirstName)
        val etFirstName = view.findViewById<EditText>(R.id.etProfileFirstName)
        val ivEditFirstName = view.findViewById<ImageView>(R.id.ivEditFirstName)
        val tvLastName = view.findViewById<TextView>(R.id.tvProfileLastName)
        val etLastName = view.findViewById<EditText>(R.id.etProfileLastName)
        val ivEditLastName = view.findViewById<ImageView>(R.id.ivEditLastName)
        val tvEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val etEmail = view.findViewById<EditText>(R.id.etProfileEmail)
        val ivEditEmail = view.findViewById<ImageView>(R.id.ivEditEmail)
        val etNewPassword = view.findViewById<EditText>(R.id.etProfileNewPassword)
        val etNewPasswordRepeat = view.findViewById<EditText>(R.id.etProfileNewPasswordRepeat)
        val btnSave = view.findViewById<Button>(R.id.btnSaveProfile)

        fun showEditMode(editText: EditText, textView: TextView) {
            editText.visibility = View.VISIBLE
            textView.visibility = View.GONE
            editText.setSelection(editText.text.length)
        }
        fun hideEditMode(editText: EditText, textView: TextView) {
            editText.visibility = View.GONE
            textView.visibility = View.VISIBLE
        }
        // Sadece bir alan edit modda olsun
        fun setOnlyEdit(editText: EditText?, textView: TextView?) {
            val allPairs = listOf(
                Pair(etUserName, tvUserName),
                Pair(etFirstName, tvFirstName),
                Pair(etLastName, tvLastName),
                Pair(etEmail, tvEmail)
            )
            if (editText == null || textView == null) {
                allPairs.forEach { (et, tv) -> hideEditMode(et, tv) }
            } else {
                allPairs.forEach { (et, tv) ->
                    if (et == editText) showEditMode(et, tv) else hideEditMode(et, tv)
                }
            }
        }
        // Edit ikonlarına ve TextView'lere tıklama
        tvUserName.setOnClickListener { setOnlyEdit(etUserName, tvUserName) }
        ivEditUserName.setOnClickListener { setOnlyEdit(etUserName, tvUserName) }
        tvFirstName.setOnClickListener { setOnlyEdit(etFirstName, tvFirstName) }
        ivEditFirstName.setOnClickListener { setOnlyEdit(etFirstName, tvFirstName) }
        tvLastName.setOnClickListener { setOnlyEdit(etLastName, tvLastName) }
        ivEditLastName.setOnClickListener { setOnlyEdit(etLastName, tvLastName) }
        tvEmail.setOnClickListener { setOnlyEdit(etEmail, tvEmail) }
        ivEditEmail.setOnClickListener { setOnlyEdit(etEmail, tvEmail) }

        // Mevcut kullanıcı bilgilerini backend'den çek ve alanlara yerleştir
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = userRepository.getCurrentUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    tvUserName.text = profile.userName
                    etUserName.setText(profile.userName)
                    tvFirstName.text = profile.firstName ?: "-"
                    etFirstName.setText(profile.firstName ?: "")
                    tvLastName.text = profile.lastName ?: "-"
                    etLastName.setText(profile.lastName ?: "")
                    tvEmail.text = profile.email
                    etEmail.setText(profile.email)
                }
            } catch (e: Exception) {
                Log.e("PROFILE_FETCH_ERROR", e.localizedMessage ?: "Bilinmeyen hata")
            }
        }
        // Kaydet butonu
        btnSave.setOnClickListener {
            val userName = etUserName.text.toString()
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val email = etEmail.text.toString()
            val newPassword = etNewPassword.text.toString()
            val newPasswordRepeat = etNewPasswordRepeat.text.toString()
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
                        // Güncel değerleri TextView'lere yaz ve edit moddan çık
                        tvUserName.text = userName
                        tvFirstName.text = firstName
                        tvLastName.text = lastName
                        tvEmail.text = email
                        setOnlyEdit(null, null) // Tüm edit modları kapat
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
        // Başlangıçta tüm edit modları kapalı
        setOnlyEdit(null, null)
    }
} 