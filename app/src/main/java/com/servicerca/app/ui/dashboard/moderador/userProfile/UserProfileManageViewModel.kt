package com.servicerca.app.ui.dashboard.moderador.userProfile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.servicerca.app.domain.model.User
import com.servicerca.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UserProfileManageViewModel @Inject constructor(
    private val repository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String? = savedStateHandle["userId"]

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        userId?.let { id ->
            _user.value = repository.findById(id)
        }
    }
}
