package com.emreberkgoger.foodrecipeapp.ui.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreberkgoger.foodrecipeapp.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DietTypeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _currentDietTypes = MutableStateFlow<List<String>>(emptyList())
    val currentDietTypes: StateFlow<List<String>> = _currentDietTypes

    private val _updateResult = MutableStateFlow<Result<Unit>?>(null)
    val updateResult: StateFlow<Result<Unit>?> = _updateResult

    fun fetchCurrentDietTypes() {
        viewModelScope.launch {
            val response = userRepository.getCurrentUserProfile()
            if (response.isSuccessful) {
                _currentDietTypes.value = response.body()?.dietTypes ?: emptyList()
            }
        }
    }

    fun updateDietPreferences(dietTypes: List<String>) {
        viewModelScope.launch {
            try {
                val response = userRepository.updateDietPreferences(dietTypes)
                if (response.isSuccessful) {
                    _updateResult.value = Result.success(Unit)
                    fetchCurrentDietTypes()
                } else {
                    _updateResult.value = Result.failure(Exception("Güncelleme başarısız: ${response.message()}"))
                }
            } catch (e: Exception) {
                _updateResult.value = Result.failure(e)
            }
        }
    }

    fun clearUpdateResult() {
        _updateResult.value = null
    }
} 