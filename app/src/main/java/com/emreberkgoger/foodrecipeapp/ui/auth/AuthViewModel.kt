package com.emreberkgoger.foodrecipeapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreberkgoger.foodrecipeapp.data.dto.request.UserLoginDto
import com.emreberkgoger.foodrecipeapp.data.dto.request.UserRegisterDto
import com.emreberkgoger.foodrecipeapp.data.dto.response.JwtResponseDto
import com.emreberkgoger.foodrecipeapp.data.dto.response.UserDto
import com.emreberkgoger.foodrecipeapp.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: Any) : AuthState() // UserDto veya JwtResponseDto olabilir
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPreferences: android.content.SharedPreferences
) : ViewModel() {
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val response = userRepository.login(UserLoginDto(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()?.token
                    if (!token.isNullOrBlank()) {
                        sharedPreferences.edit().putString("jwt_token", token).apply()
                    }
                    _loginState.value = AuthState.Success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    _loginState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error(e.localizedMessage ?: "Bilinmeyen hata")
            }
        }
    }

    fun register(
        email: String,
        password: String,
        userName: String,
        firstName: String,
        lastName: String,
        dietTypes: List<String>
    ) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            try {
                val request = UserRegisterDto(email, password, userName, firstName, lastName, dietTypes)
                val response = userRepository.register(request)
                if (response.isSuccessful && response.body() != null) {
                    val token = (response.body() as? com.emreberkgoger.foodrecipeapp.data.dto.response.JwtResponseDto)?.token
                    if (!token.isNullOrBlank()) {
                        sharedPreferences.edit().putString("jwt_token", token).apply()
                    }
                    _registerState.value = AuthState.Success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    _registerState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _registerState.value = AuthState.Error(e.localizedMessage ?: "Bilinmeyen hata")
            }
        }
    }
} 