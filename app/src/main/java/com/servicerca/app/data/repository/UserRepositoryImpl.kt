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
                name = "Diego Alexander Jimenez",
                city = "Armenia",
                address = "Calle 777",
                email = "diego@gmail.com",
                password = "111111",
                profilePictureUrl = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEga-7mA9kd7EnROYLMEYwURS2xlW1uWK8eWC8F6X3RFuCrJQLnd5eJ8KNOqXeVNuUVM0c4X31Uoz7NlQKJ4QxFfF6EDWAwgT6y1F_HgZ23As74U0wOHy14ClTNC9kP5KJHgPouBaogO5IpYsvxGmDCYlJ9do4tNb9eb6fYBMMSIG3zEcAN-7y2lIrvTwOyb/s320/WhatsApp%20Image%202026-03-04%20at%2023.04.58.jpeg"
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