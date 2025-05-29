package com.emreberkgoger.foodrecipeapp.ui.auth

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    @Inject lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val goToRegisterText = view.findViewById<TextView>(R.id.goToRegisterText)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            viewModel.login(email, password)
        }

        goToRegisterText.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginState.collectLatest { state ->
                when (state) {
                    is AuthState.Loading -> {
                        // Progress göster
                    }
                    is AuthState.Success -> {
                        Toast.makeText(requireContext(), "Giriş başarılı", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, HomeFragment())
                            .commit()
                    }
                    is AuthState.Error -> {
                        Log.e("LOGIN_ERROR", state.message)
                        val userMessage = if (state.message.contains("401") || state.message.contains("403") || state.message.contains("Unauthorized", true)) {
                            "E-posta veya şifre hatalı."
                        } else {
                            state.message
                        }
                        Toast.makeText(requireContext(), userMessage, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}