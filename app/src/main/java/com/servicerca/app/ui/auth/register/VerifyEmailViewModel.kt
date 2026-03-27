package com.servicerca.app.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.core.utils.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VerifyEmailViewModel : ViewModel() {

    // ── Estado del código OTP ingresado ──────────────────────────────────────
    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    // ── Estado de carga ──────────────────────────────────────────────────────
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ── Resultado de la verificación ─────────────────────────────────────────
    private val _verifyResult = MutableStateFlow<RequestResult?>(null)
    val verifyResult: StateFlow<RequestResult?> = _verifyResult.asStateFlow()

    // ── Resultado del reenvío ────────────────────────────────────────────────
    private val _resendResult = MutableStateFlow<RequestResult?>(null)
    val resendResult: StateFlow<RequestResult?> = _resendResult.asStateFlow()

    /** Actualiza el código OTP mientras el usuario escribe. */
    fun onOtpChanged(code: String) {
        _otpCode.value = code
    }

    /**
     * Simula la verificación del código OTP.
     * En una implementación real aquí iría la llamada al repositorio / Firebase.
     */
    fun verifyEmail() {
        if (_otpCode.value.length < 6) {
            _verifyResult.value = RequestResult.Failure("Ingresa el código completo de 6 dígitos")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: llamar al repositorio cuando se implemente la lógica real
                _verifyResult.value = RequestResult.Success("¡Cuenta activada! Redirigiendo al inicio de sesión…")
            } catch (e: Exception) {
                _verifyResult.value = RequestResult.Failure("Error al verificar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Simula el reenvío del correo de verificación.
     * En una implementación real aquí iría `firebaseUser.sendEmailVerification()`.
     */
    fun resendEmail() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: llamar al repositorio cuando se implemente la lógica real
                _resendResult.value = RequestResult.Success("Correo reenviado exitosamente")
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
