package com.servicerca.app.data.repository

import com.servicerca.app.domain.model.User
import android.util.Log
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.EmailAuthProvider
import com.servicerca.app.BuildConfig
import com.servicerca.app.core.email.JavaMailSender
import com.servicerca.app.domain.model.UserRole
import com.servicerca.app.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
                Log.e("UserRepository", "Error observando usuarios", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                _users.value = snapshot.documents.mapNotNull { document ->
                    document.toObject(User::class.java)
                        ?.let { if (it.id.isBlank()) it.copy(id = document.id) else it }
                }
            }
        }
    }

    override fun observeAllUsers(): Flow<List<User>> = callbackFlow {
        val registration = usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("UserRepository", "Error observing all users", error)
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val usersList = snapshot.documents.mapNotNull { document ->
                    document.toObject(User::class.java)
                        ?.let { if (it.id.isBlank()) it.copy(id = document.id) else it }
                }
                trySend(usersList)
                _users.value = usersList
            }
        }
        awaitClose { registration.remove() }
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
                val user = snapshot?.toObject(User::class.java)
                    ?.let { if (it.id.isBlank()) it.copy(id = snapshot.id) else it }
                trySend(user)
            }
        awaitClose { registration.remove() }
    }

    override suspend fun findById(id: String): User? {
        return try {
            val document = usersCollection.document(id).get().await()
            document.toObject(User::class.java)
                ?.let { if (it.id.isBlank()) it.copy(id = document.id) else it }
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

    override suspend fun googleSignIn(idToken: String): User? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val responseUser = auth.signInWithCredential(credential).await()
            val uid = responseUser.user?.uid ?: throw Exception("Usuario no encontrado")
            var user = findById(uid)
            
            if (user == null) {
                // Nuevo usuario de Google
                val displayName = responseUser.user?.displayName ?: ""
                val names = displayName.split(" ")
                val firstName = names.firstOrNull() ?: ""
                val lastName = names.drop(1).joinToString(" ")
                
                val newUser = User(
                    id = uid,
                    email = responseUser.user?.email ?: "",
                    name1 = firstName,
                    lastname1 = lastName,
                    isEmailVerified = true,
                    role = UserRole.USER
                )
                usersCollection.document(uid).set(newUser).await()
                user = newUser
                Log.d("UserRepository", "Nuevo usuario registrado vía Google: $uid")
            } else {
                // Asegurar que esté verificado (ya que Google siempre verifica los emails)
                if (!user.isEmailVerified) {
                    val updatedUser = user.copy(isEmailVerified = true)
                    usersCollection.document(uid).set(updatedUser).await()
                    user = updatedUser
                }
                Log.d("UserRepository", "Inicio de sesión vía Google exitoso: $uid")
            }
            user
        } catch (e: Exception) {
            Log.e("UserRepository", "Error en el inicio de sesión con Google", e)
            null
        }
    }

    override suspend fun findByEmail(email: String): User? {
        return try {
            val snapshot = usersCollection.whereEqualTo("email", email.trim()).get().await()
            val document = snapshot.documents.firstOrNull()
            document?.toObject(User::class.java)
                ?.let { if (it.id.isBlank()) it.copy(id = document.id) else it }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al buscar usuario por email", e)
            null
        }
    }

    override suspend fun verifyEmail(email: String, otpCode: String): Result<Boolean> {
        return try {
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

            if (status == "blocked") return Result.failure(Exception("Demasiados intentos. Solicita un nuevo código."))
            if (System.currentTimeMillis() > expiresAt) {
                otpRef.delete().await()
                return Result.failure(Exception("El código ha expirado. Solicita uno nuevo."))
            }

            if (storedCode != otpCode.trim()) {
                val nextAttempts = attempts + 1
                val updates = mutableMapOf<String, Any>("attempts" to nextAttempts)
                if (nextAttempts >= 5) {
                    updates["status"] = "blocked"
                }
                otpRef.update(updates).await()
                return Result.failure(Exception("Código incorrecto. Intentos restantes: ${maxOf(0, 5 - nextAttempts)}"))
            }

            usersCollection.document(userId).update("isEmailVerified", true).await()
            otpRef.delete().await()
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
            usersCollection.document(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al borrar usuario $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun suspendAccount(userId: String): Result<Unit> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val currentlySuspended = doc.getBoolean("isSuspended") ?: false
            usersCollection.document(userId).update("isSuspended", !currentlySuspended).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al suspender usuario $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun initiatePasswordRecovery(email: String): Result<Unit> {
        val trimmedEmail = email.trim()
        return try {
            val actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://servicerca-6ee07.web.app/reset")
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.servicerca.app", true, "1")
                .build()
                
            auth.sendPasswordResetEmail(trimmedEmail, actionCodeSettings).await()
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
            val credential = EmailAuthProvider.getCredential(
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

    override suspend fun updateProviderStats(providerId: String, newRating: Double, xpIncrement: Int) {
        try {
            usersCollection.document(providerId).update(
                mapOf(
                    "rating" to newRating,
                    "totalPoints" to FieldValue.increment(xpIncrement.toLong()),
                    "completedServices" to FieldValue.increment(1L)
                )
            ).await()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error actualizando stats del proveedor $providerId", e)
        }
    }

    override suspend fun toggleInterestingService(userId: String, serviceId: String): Result<Boolean> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val user = doc.toObject(User::class.java) ?: return Result.failure(Exception("Usuario no encontrado"))
            
            val alreadyInList = serviceId in user.listInteresting
            val fieldUpdate: Any = if (alreadyInList) {
                FieldValue.arrayRemove(serviceId)
            } else {
                FieldValue.arrayUnion(serviceId)
            }

            usersCollection.document(userId).update("listInteresting", fieldUpdate).await()
            Result.success(!alreadyInList)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error en toggleInterestingService", e)
            Result.failure(e)
        }
    }

    override suspend fun updateOnlineStatus(userId: String, isOnline: Boolean) {
        try {
            usersCollection.document(userId).update("isOnline", isOnline).await()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating online status for $userId", e)
        }
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

        try {
            if (BuildConfig.SMTP_USER.isNotBlank() && BuildConfig.SMTP_PASSWORD.isNotBlank()) {
                val subject = "Verifica tu correo en ServiCerca — Código OTP"
                val text = "Tu código de verificación en ServiCerca es: $code\n\nSi no creaste esta cuenta, ignora este correo."
                val html = "<p>Hola,</p><p>Tu código de verificación en <strong>ServiCerca</strong> es:</p><h2 style=\"letter-spacing:6px\">$code</h2><p>Si no solicitaste este código, puedes ignorar este correo.</p>"

                val sendResult = JavaMailSender.sendEmail(email, subject, text, html)
                if (sendResult.isSuccess) {
                    verificationCodesCollection.document(userId).update(mapOf(
                        "status" to "sent",
                        "sentAt" to FieldValue.serverTimestamp(),
                        "lastSentAt" to FieldValue.serverTimestamp()
                    )).await()
                }
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al enviar OTP por SMTP", e)
        }
    }
}
