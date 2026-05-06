package com.servicerca.app.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _verifyResult = MutableStateFlow<RequestResult?>(null)
    val verifyResult: StateFlow<RequestResult?> = _verifyResult.asStateFlow()

    private val _resendResult = MutableStateFlow<RequestResult?>(null)
    val resendResult: StateFlow<RequestResult?> = _resendResult.asStateFlow()

    fun onOtpChanged(code: String) {
        _otpCode.value = code
    }

    fun verifyEmail(email: String) {
        if (_otpCode.value.length < 6) {
            _verifyResult.value = RequestResult.Failure("Ingresa el código completo de 6 dígitos")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = userRepository.verifyEmail(email, _otpCode.value)
                if (result.isSuccess) {
                    _verifyResult.value = RequestResult.Success("¡Cuenta activada! Redirigiendo al inicio de sesión…")
                } else {
                    _verifyResult.value = RequestResult.Failure(
                        result.exceptionOrNull()?.message ?: "Error al verificar"
                    )
                }
            } catch (e: Exception) {
                _verifyResult.value = RequestResult.Failure("Error al verificar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resendEmail(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = userRepository.resendVerificationEmail(email)
                if (result.isSuccess) {
                    _resendResult.value = RequestResult.Success(
                        "Código reenviado. Revisa tu correo (o Logcat en desarrollo)."
                    )
                } else {
                    _resendResult.value = RequestResult.Failure(
                        result.exceptionOrNull()?.message ?: "Error al reenviar"
                    )
                }
            } catch (e: Exception) {
                _resendResult.value = RequestResult.Failure("Error al reenviar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetVerifyResult() {
        _verifyResult.value = null
    }

    fun resetResendResult() {
        _resendResult.value = null
    }
}
