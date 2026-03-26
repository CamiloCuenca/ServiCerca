package com.servicerca.app.data.repository

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
        _users.value += user
    }

    override fun findById(id: String): User? {
        return _users.value.firstOrNull { it.id == id }
    }

    override fun login(email: String, password: String): User? {
        return _users.value.firstOrNull { it.email == email && it.password == password }
    }

    private fun fetchUsers(): List<User> {
        return listOf(
            User(
                id = "1",
                name = "Juan",
                city = "Ciudad 1",
                address = "Calle 123",
                email = "juanc.cuencas@uqvirtual.edu.co",
                password = "123456",
                profilePictureUrl = "https://m.media-amazon.com/images/I/41g6jROgo0L.png"
            ),
            User(
                id = "2",
                name = "Maria",
                city = "Pereira",
                address = "Calle 456",
                email = "maria@email.com",
                password = "222222",
                profilePictureUrl = "https://picsum.photos/200?random=2"
            ),
            User(
                id = "3",
                name = "Carlos",
                city = "Armenia",
                address = "Calle 789",
                email = "carlos@email.com",
                password = "333333",
                profilePictureUrl = "https://picsum.photos/200?random=3",
                role = UserRole.ADMIN
            )
        )
    }
}