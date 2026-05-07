package com.servicerca.app.data.repository

import com.servicerca.app.domain.model.User
import android.util.Log
import com.servicerca.app.BuildConfig
import com.servicerca.app.core.email.JavaMailSender
import com.servicerca.app.domain.model.UserRole
import com.servicerca.app.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.Timestamp

@Singleton
class UserRepositoryImpl @Inject
constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users: StateFlow<List<User>> = _users.asStateFlow()

    private val usersCollection = firestore.collection("users")
    private val verificationCodesCollection = firestore.collection("emailVerificationCodes")

    init {
        usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("UserRepository", "Error listening to users", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                try {
                    val usersList = snapshot.documents.mapNotNull { document ->
                        try {
                            document.toObject(User::class.java)
                        } catch (e: Exception) {
                            Log.e("UserRepository", "Error converting document to User", e)
                            null
                        }
                    }
                    _users.value = usersList
                } catch (e: Exception) {
                    Log.e("UserRepository", "Error processing snapshot", e)
                }
            }
        }
    }

    override suspend fun save(user: User) {
        try {
            if (user.password.isNotEmpty() && user.id.isEmpty()) {
                val newAuthUser = auth.createUserWithEmailAndPassword(user.email, user.password).await()
                val uid = newAuthUser.user?.uid
                    ?: throw Exception("Error al obtener el UID del usuario creado")

                val userCopy = user.copy(
                    id = uid,
                    password = "",
                    isEmailVerified = false
                )

                usersCollection.document(uid).set(userCopy).await()

                generateAndStoreOtp(uid, user.email)

                // Mantenemos la sesión activa para que verifyEmail() pueda leer Firestore.
                // El signOut se hace dentro de verifyEmail() al confirmar el OTP.

                Log.d("UserRepository", "Usuario creado: $uid")
            } else if (user.id.isNotEmpty()) {
                usersCollection.document(user.id).set(user).await()
                Log.d("UserRepository", "Usuario actualizado: ${user.id}")
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al guardar usuario", e)
            throw e
        }
    }

    override fun observeUser(userId: String): Flow<User?> = callbackFlow {
        val registration = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserRepository", "Error observing user $userId", error)
                    trySend(null)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(User::class.java))
            }
        awaitClose { registration.remove() }
    }

    override suspend fun findById(id: String): User? {
        return try {
            val document = usersCollection.document(id).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al buscar usuario por ID", e)
            null
        }
    }

    override suspend fun login(email: String, password: String): User? {
        return try {
            val responseUser = auth.signInWithEmailAndPassword(email, password).await()
            val uid = responseUser.user?.uid ?: throw Exception("Usuario no encontrado")
            val user = findById(uid)
            if (user != null && !user.isEmailVerified) {
                auth.signOut()
            }
            user
        } catch (e: Exception) {
            Log.e("UserRepository", "Error en el inicio de sesión", e)
            null
        }
    }

    override suspend fun verifyEmail(email: String, otpCode: String): Result<Boolean> {
        return try {
            // Usamos el UID del usuario autenticado directamente — evita una query a la colección
            // que Firestore rechazaría por reglas de seguridad (PERMISSION_DENIED).
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Sesión expirada. Vuelve a registrarte."))

            val otpRef = verificationCodesCollection.document(userId)
            val otpDoc = otpRef.get().await()

            if (!otpDoc.exists()) {
                return Result.failure(Exception("Código expirado o no encontrado. Solicita uno nuevo."))
            }

            val storedCode = otpDoc.getString("code")
                ?: return Result.failure(Exception("Código inválido"))
            val expiresAt = otpDoc.getLong("expiresAt") ?: 0L
            val attempts = (otpDoc.getLong("attempts") ?: 0L).toInt()
            val status = otpDoc.getString("status") ?: "pending"

            // Verificar estado
            if (status == "blocked") return Result.failure(Exception("Demasiados intentos. Solicita un nuevo código."))
            if (System.currentTimeMillis() > expiresAt) {
                otpRef.delete().await()
                return Result.failure(Exception("El código ha expirado. Solicita uno nuevo."))
            }

            if (storedCode != otpCode.trim()) {
                // Incrementar intentos y bloquear si supera umbral
                val nextAttempts = attempts + 1
                val updates = mutableMapOf<String, Any>("attempts" to nextAttempts)
                if (nextAttempts >= 5) {
                    updates["status"] = "blocked"
                }
                otpRef.update(updates).await()
                return Result.failure(Exception("Código incorrecto. Intentos restantes: ${maxOf(0, 5 - nextAttempts)}"))
            }

            // Código correcto: marcar usuario verificado y limpiar OTP
            usersCollection.document(userId).update("isEmailVerified", true).await()
            otpRef.delete().await()

            // Cerrar sesión para que el usuario haga login explícito
            auth.signOut()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resendVerificationEmail(email: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Sesión expirada. Vuelve a registrarte."))

            val otpRef = verificationCodesCollection.document(userId)
            val otpDoc = otpRef.get().await()

            val MIN_RESEND_INTERVAL_MS = 60_000L
            if (otpDoc.exists()) {
                val lastSent = otpDoc.getTimestamp("lastSentAt")?.toDate()?.time ?: 0L
                if (System.currentTimeMillis() - lastSent < MIN_RESEND_INTERVAL_MS) {
                    return Result.failure(Exception("Espera un momento antes de reenviar el código."))
                }
            }

            generateAndStoreOtp(userId, email.trim())

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al reenviar verificación", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(userId: String): Result<Unit> {
        return try {
            val user = _users.value.firstOrNull { it.id == userId }
            if (user != null) {
                usersCollection.document(userId).delete().await()
                auth.currentUser?.delete()?.await()
                _users.value = _users.value.filter { it.id != userId }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al borrar usuario", e)
            Result.failure(e)
        }
    }

    override suspend fun suspendAccount(userId: String): Result<Unit> {
        return try {
            val userIndex = _users.value.indexOfFirst { it.id == userId }
            if (userIndex != -1) {
                val user = _users.value[userIndex]
                val updatedUser = user.copy(isSuspended = !user.isSuspended)
                usersCollection.document(userId).set(updatedUser).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun initiatePasswordRecovery(email: String): Result<Unit> {
        val trimmedEmail = email.trim()
        return try {
            auth.sendPasswordResetEmail(trimmedEmail).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al iniciar recuperación de contraseña", e)
            Result.failure(Exception("No existe una cuenta asociada a este correo"))
        }
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        return try {
            auth.confirmPasswordReset(code, newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al restablecer contraseña", e)
            Result.failure(Exception("Error al restablecer contraseña: ${e.message}"))
        }
    }

    override suspend fun updatePassword(
        userId: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(
                user.email ?: throw Exception("Email del usuario no disponible"),
                currentPassword
            )
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al actualizar contraseña", e)
            Result.failure(Exception("La contraseña actual es incorrecta o error en la actualización"))
        }
    }

    override suspend fun toggleInterestingService(userId: String, serviceId: String): Result<Boolean> {
        val userIndex = _users.value.indexOfFirst { it.id == userId }
        if (userIndex == -1) return Result.failure(Exception("Usuario no encontrado"))

        val user = _users.value[userIndex]
        val alreadyInList = serviceId in user.listInteresting
        val nextInteresting = if (alreadyInList) {
            user.listInteresting - serviceId
        } else {
            user.listInteresting + serviceId
        }
        val updatedUser = user.copy(listInteresting = nextInteresting)
        return try {
            usersCollection.document(userId).set(updatedUser).await()
            Result.success(!alreadyInList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun findByEmail(email: String): User? {
        return users.value.firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    private suspend fun generateAndStoreOtp(userId: String, email: String) {
        val code = (100000..999999).random().toString()
        val expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000L)

        verificationCodesCollection.document(userId).set(
            mapOf(
                "code" to code,
                "email" to email,
                "createdAt" to FieldValue.serverTimestamp(),
                "lastSentAt" to FieldValue.serverTimestamp(),
                "attempts" to 0,
                "status" to "pending",
                "expiresAt" to expiresAt
            )
        ).await()

        // Intentar enviar correo usando JavaMail (SMTP) si las credenciales están configuradas
        try {
            // Evitar enviar en blanco (BuildConfig tiene valores por defecto vacíos si no se configuran)
            if (BuildConfig.SMTP_USER.isNotBlank() && BuildConfig.SMTP_PASSWORD.isNotBlank()) {
                val subject = "Verifica tu correo en ServiCerca — Código OTP"
                val text = "Tu código de verificación en ServiCerca es: $code\n\nSi no creaste esta cuenta, ignora este correo."
                val html = "<p>Hola,</p><p>Tu código de verificación en <strong>ServiCerca</strong> es:</p><h2 style=\"letter-spacing:6px\">$code</h2><p>Si no solicitaste este código, puedes ignorar este correo.</p>"

                val sendResult = JavaMailSender.sendEmail(email, subject, text, html)
                if (sendResult.isSuccess) {
                    Log.d("UserRepository", "OTP enviado por SMTP a $email")
                    // Marcar enviado y actualizar sentAt/lastSentAt
                    verificationCodesCollection.document(userId).update(mapOf(
                        "status" to "sent",
                        "sentAt" to FieldValue.serverTimestamp(),
                        "lastSentAt" to FieldValue.serverTimestamp()
                    )).await()
                } else {
                    Log.e("UserRepository", "Fallo envío SMTP a $email: ${sendResult.exceptionOrNull()}")
                    Log.d("OTP_DEBUG", "Código de verificación para $email: $code")
                }
            } else {
                // Sin credenciales SMTP, usar Logcat como fallback (desarrollo)
                Log.d("OTP_DEBUG", "Código de verificación para $email: $code")
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al enviar OTP por SMTP", e)
            Log.d("OTP_DEBUG", "Código de verificación para $email: $code")
        }
    }
}
