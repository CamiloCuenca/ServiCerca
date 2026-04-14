package com.servicerca.app.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.repository.UserRepository
import com.servicerca.app.domain.usecase.DeleteAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeleteProfileUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val showConfirmationDialog: Boolean = false,
    val showReAuthDialog: Boolean = false
)

@HiltViewModel
class DeleteProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeleteProfileUiState())
    val uiState: StateFlow<DeleteProfileUiState> = _uiState.asStateFlow()

    fun onShowConfirmationDialog() {
        _uiState.value = _uiState.value.copy(showConfirmationDialog = true)
    }

    fun onDismissConfirmationDialog() {
        _uiState.value = _uiState.value.copy(showConfirmationDialog = false)
    }

    fun onConfirmDelete() {
        _uiState.value = _uiState.value.copy(
            showConfirmationDialog = false,
            showReAuthDialog = true
        )
    }

    fun onDismissReAuthDialog() {
        _uiState.value = _uiState.value.copy(showReAuthDialog = false)
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            Log.d("DeleteProfileVM", "Iniciando proceso de eliminación")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val session = sessionDataStore.sessionFlow.firstOrNull()
                if (session == null) {
                    Log.e("DeleteProfileVM", "Sesión nula")
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Sesión no encontrada")
                    return@launch
                }

                val user = userRepository.findById(session.userId)
                if (user == null || user.password != password) {
                    Log.w("DeleteProfileVM", "Contraseña incorrecta o usuario no encontrado")
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Contraseña incorrecta")
                    return@launch
                }

                Log.d("DeleteProfileVM", "Contraseña verificada. Llamando a UseCase")
                val result = deleteAccountUseCase()
                if (result.isSuccess) {
                    Log.d("DeleteProfileVM", "Eliminación exitosa en el UseCase")
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true, showReAuthDialog = false)
                } else {
                    Log.e("DeleteProfileVM", "Error en el UseCase: ${result.exceptionOrNull()?.message}")
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                }
            } catch (e: Exception) {
                Log.e("DeleteProfileVM", "Excepción durante la eliminación", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

}
