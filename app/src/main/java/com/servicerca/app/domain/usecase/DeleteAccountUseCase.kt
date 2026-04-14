package com.servicerca.app.domain.usecase

import com.servicerca.app.data.datastore.SessionDataStore
import com.servicerca.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            val session = sessionDataStore.sessionFlow.firstOrNull()
            if (session != null) {
                // Solo eliminamos la cuenta del repositorio.
                // La limpieza de la sesión la manejará la UI para evitar conflictos de navegación.
                userRepository.deleteAccount(session.userId)
            } else {
                Result.failure(Exception("Sesión no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
