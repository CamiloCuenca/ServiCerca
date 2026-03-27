package com.servicerca.app.ui.auth.login

import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.model.UserRole
import com.servicerca.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.*
import org.junit.Test

class LoginViewModelTest {

    class FakeUserRepository : UserRepository {
        private val _users = MutableStateFlow<List<User>>(emptyList())
        override val users: StateFlow<List<User>> = _users

        fun setUsers(list: List<User>) {
            _users.value = list
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
    }

    @Test
    fun login_success_regularUser() {
        val repo = FakeUserRepository()
        val user = User(
            id = "1",
            name = "Juan",
            city = "Ciudad",
            address = "Calle",
            email = "juan@email.com",
            password = "111111",
            profilePictureUrl = "",
            role = UserRole.USER
        )
        repo.setUsers(listOf(user))

        val vm = LoginViewModel(repo)
        vm.email.onChange(user.email)
        vm.password.onChange(user.password)

        vm.login()
        val result = vm.loginResult.value
        assertTrue(result is com.servicerca.app.core.utils.RequestResult.Success)
        assertEquals("Login exitoso", (result as com.servicerca.app.core.utils.RequestResult.Success).message)
    }

    @Test
    fun login_success_adminUser_navigatesToModerator() {
        val repo = FakeUserRepository()
        val admin = User(
            id = "3",
            name = "Carlos",
            city = "Ciudad",
            address = "Calle",
            email = "carlos@email.com",
            password = "333333",
            profilePictureUrl = "",
            role = UserRole.ADMIN
        )
        repo.setUsers(listOf(admin))

        val vm = LoginViewModel(repo)
        vm.email.onChange(admin.email)
        vm.password.onChange(admin.password)

        vm.login()
        val result = vm.loginResult.value
        assertTrue(result is com.servicerca.app.core.utils.RequestResult.Success)
        assertEquals("moderator", (result as com.servicerca.app.core.utils.RequestResult.Success).message)
    }

    @Test
    fun login_failure_invalidCredentials() {
        val repo = FakeUserRepository()
        repo.setUsers(emptyList())

        val vm = LoginViewModel(repo)
        vm.email.onChange("wrong@email.com")
        vm.password.onChange("badpass")

        vm.login()
        val result = vm.loginResult.value
        assertTrue(result is com.servicerca.app.core.utils.RequestResult.Failure)
    }
}
