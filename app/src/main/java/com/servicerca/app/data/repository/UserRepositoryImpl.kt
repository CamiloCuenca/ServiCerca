package com.servicerca.app.data.repository

import android.util.Log
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.model.UserRole
import com.servicerca.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Anotamos la clase como Singleton para que Hilt gestione una única instancia
class UserRepositoryImpl @Inject constructor(): UserRepository { // Implementamos la interfaz UserRepository

    // Usamos StateFlow para manejar la lista de usuarios de manera reactiva
    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        _users.value = fetchUsers()
    }

    override fun save(user: User) {
        val userIndex = _users.value.indexOfFirst { it.id == user.id }
        if (userIndex != -1) {
            val updatedList = _users.value.toMutableList()
            updatedList[userIndex] = user
            _users.value = updatedList
        } else {
            _users.value += user
        }
    }

    override fun findById(id: String): User? {
        return _users.value.firstOrNull { it.id == id }
    }

    override fun login(email: String, password: String): User? {
        return _users.value.firstOrNull { it.email == email && it.password == password }
    }

    override suspend fun deleteAccount(userId: String): Result<Unit> {
        Log.d("UserRepository", "Intentando eliminar usuario con ID: $userId")
        return try {
            val user = _users.value.firstOrNull { it.id == userId }
            if (user != null) {
                _users.value = _users.value.filter { it.id != userId }
                Log.d("UserRepository", "Usuario eliminado. Nueva lista size: ${_users.value.size}")
                Result.success(Unit)
            } else {
                Log.w("UserRepository", "No se encontró el usuario para eliminar")
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error al borrar usuario", e)
            Result.failure(e)
        }
    }

    override suspend fun verifyEmail(email: String, otpCode: String): Result<Boolean> {
        val trimmedEmail = email.trim()
        val userIndex = _users.value.indexOfFirst { it.email == trimmedEmail }

        if (userIndex != -1) {
            val updatedList = _users.value.toMutableList()
            val user = updatedList[userIndex]
            updatedList[userIndex] = user.copy(isEmailVerified = true)
            _users.value = updatedList
            return Result.success(true)
        }
        return Result.failure(Exception("Usuario no encontrado"))
    }

    override suspend fun initiatePasswordRecovery(email: String): Result<Unit> {
        val trimmedEmail = email.trim()
        val user = _users.value.find { it.email == trimmedEmail }
        return if (user != null) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("No existe una cuenta asociada a este correo"))
        }
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        val trimmedEmail = email.trim()
        val userIndex = _users.value.indexOfFirst { it.email == trimmedEmail }
        if (userIndex != -1) {
            val updatedList = _users.value.toMutableList()
            updatedList[userIndex] = updatedList[userIndex].copy(password = newPassword)
            _users.value = updatedList
            return Result.success(Unit)
        }
        return Result.failure(Exception("Error al restablecer contraseña: Usuario no encontrado"))
    }

    override suspend fun updatePassword(
        userId: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        val userIndex = _users.value.indexOfFirst { it.id == userId }
        if (userIndex == -1) {
            return Result.failure(Exception("Usuario no encontrado"))
        }

        val user = _users.value[userIndex]
        if (user.password != currentPassword) {
            return Result.failure(Exception("La contraseña actual es incorrecta"))
        }

        val updatedList = _users.value.toMutableList()
        updatedList[userIndex] = user.copy(password = newPassword)
        _users.value = updatedList
        return Result.success(Unit)
    }

    override suspend fun toggleInterestingService(userId: String, serviceId: String): Result<Boolean> {
        val userIndex = _users.value.indexOfFirst { it.id == userId }
        if (userIndex == -1) {
            return Result.failure(Exception("Usuario no encontrado"))
        }

        val usersUpdated = _users.value.toMutableList()
        val user = usersUpdated[userIndex]
        val alreadyInList = serviceId in user.listInteresting

        val nextInteresting = if (alreadyInList) {
            user.listInteresting - serviceId
        } else {
            user.listInteresting + serviceId
        }

        usersUpdated[userIndex] = user.copy(listInteresting = nextInteresting)
        _users.value = usersUpdated

        return Result.success(!alreadyInList)
    }

    private fun fetchUsers(): List<User> {


        return listOf(
            User(
                id = "1",
                name1 = "Juan",
                name2 = "Camilo",
                lastname1 = "Cuenca",
                lastname2 = "Sepulveda",
                city = "Ciudad 1",
                address = "Calle 123",
                email = "juanc.cuencas@uqvirtual.edu.co",
                password = "123456",
                profilePictureUrl = "https://m.media-amazon.com/images/I/41g6jROgo0L.png",
                completedServices = 12,
                totalPoints = 1250,
                rating = 4.5,
                memberSince = 2024,
                isEmailVerified = true,
                listInteresting = listOf("1", "6")
            ),

            User(
                id = "2",
                name1 = "Sienna",
                name2 = "Maria",
                lastname1 = "Martinez",
                lastname2 = "Toro",
                city = "Pereira",
                address = "Calle 456",
                email = "maria@email.com",
                password = "222222",
                profilePictureUrl = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEh3ixabRmfyRl92F78X1RNNDj2hhogCUofBWTVX977EHxGS4-BMcuI7Y39Y3ZjK3tJPANYHe8AiIyjv1CeEsNzjIcCaj4aQAklofVLW73gv-dqMxGJEUqYa-poX6UkJW5Z_YBAfSk9BrB8YHT5F4Rz3n7bxxMnBEVyNpi8RKfnVhBbrgEmqL5yVtC70CKU/s320/WhatsApp%20Image%202026-04-16%20at%2002.32.41.jpeg",
                completedServices = 15,
                totalPoints = 1650,
                rating = 4.7,
                memberSince = 2023,
                isEmailVerified = true,
                listInteresting = listOf("3", "6")
            ),

            User(
                id = "3",
                name1 = "Diego",
                name2 = "Alexander",
                lastname1 = "Jimenez",
                lastname2 = "Lothbrok",
                city = "Armenia",
                address = "Calle 777",
                email = "diego@email.com",
                password = "111111",
                profilePictureUrl = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEga-7mA9kd7EnROYLMEYwURS2xlW1uWK8eWC8F6X3RFuCrJQLnd5eJ8KNOqXeVNuUVM0c4X31Uoz7NlQKJ4QxFfF6EDWAwgT6y1F_HgZ23As74U0wOHy14ClTNC9kP5KJHgPouBaogO5IpYsvxGmDCYlJ9do4tNb9eb6fYBMMSIG3zEcAN-7y2lIrvTwOyb/s320/WhatsApp%20Image%202026-03-04%20at%2023.04.58.jpeg",
                completedServices = 7,
                totalPoints = 1000,
                rating = 4.9,
                memberSince = 2024,
                pendingReviews = 8,
                approvedReviews = 15,
                rejectReviews = 3,
                isEmailVerified = true,
                listInteresting = listOf("3", "6")
            ),

            User(
                id = "4",
                name1 = "Carlos",
                name2 = "Scott",
                lastname1 = "Toro",
                lastname2 = "Kennedy",
                city = "Armenia",
                address = "Calle 789",
                email = "carlos@email.com",
                password = "333333",
                profilePictureUrl = "https://i.pinimg.com/originals/ae/90/f5/ae90f5c41e36d420e8175f072367ead9.jpg",
                role = UserRole.ADMIN,
                memberSince = 2022,
                pendingReviews = 12,
                approvedReviews = 20,
                rejectReviews = 6,
                isEmailVerified = true,
                listInteresting = listOf("3", "6")
            )

        )
    }
}
