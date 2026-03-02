package com.example.jeffenger.ui.catApi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeffenger.utils.state.LoadingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatViewModel : ViewModel() {

    private val repository = CatRepository(CatApi)

    private val _catImageUrl = MutableStateFlow<String?>(null)
    val catImageUrl = _catImageUrl.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    fun loadRandomCat() {
        viewModelScope.launch {
            Log.d("CatViewModel", "loadRandomCat() called")

            _loadingState.value = LoadingState.Loading("Katze wird geladen...")

            try {
                val response = repository.getRandomCat()

                if (response.isSuccessful) {
                    val imageUrl = response.body()
                        ?.firstOrNull()
                        ?.url

                    if (imageUrl != null) {
                        _catImageUrl.value = imageUrl
                        _loadingState.value = LoadingState.Success()
                    } else {
                        _catImageUrl.value = null
                        _loadingState.value = LoadingState.Error("Keine Katze erhalten.")
                    }

                } else {
                    _catImageUrl.value = null
                    _loadingState.value =
                        LoadingState.Error("Cat API Fehler (${response.code()})")
                }

            } catch (e: Exception) {
                _catImageUrl.value = null
                _loadingState.value =
                    LoadingState.Error(
                        message = e.localizedMessage ?: "Netzwerkfehler",
                        throwable = e
                    )
            }
        }
    }

    fun clearCat() {
        _catImageUrl.value = null
        _loadingState.value = LoadingState.Idle
    }
}
