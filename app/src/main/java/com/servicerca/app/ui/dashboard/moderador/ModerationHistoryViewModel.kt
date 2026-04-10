package com.servicerca.app.ui.dashboard.moderador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicerca.app.domain.model.ModerationItem
import com.servicerca.app.domain.model.ModerationStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ModerationHistoryViewModel : ViewModel() {

    // Estado para la pestaña seleccionada (0: Todos, 1: Aprobados, 2: Rechazados)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Fuente de datos completa (esto vendría de un Repositorio o API en el futuro)
    private val _allHistoryItems = MutableStateFlow<List<ModerationItem>>(emptyList())

    // Flujo que combina la lista y el filtro de la pestaña para obtener la lista filtrada
    val filteredHistory: StateFlow<List<ModerationItem>> = combine(
        _allHistoryItems,
        _selectedTab
    ) { items, tab ->
        when (tab) {
            1 -> items.filter { it.status == ModerationStatus.APRBADA }
            2 -> items.filter { it.status == ModerationStatus.RECHAZADA }
            else -> items
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadModerationHistory()
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }

    private fun loadModerationHistory() {
        // Simulamos la carga de datos. Aquí es donde conectarías con tu servicio.
        // Nota: He usado 'resultado' y 'status' según tu modelo ModerationItem.kt
        _allHistoryItems.value = listOf(
            ModerationItem(
                title = "Servicio de Limpieza",
                resultado = "APROBADA",
                userName = "Juan Pérez",
                actionPerformed = "Verificación de Antecedentes",
                reason = "Documentación completa y verificada",
                date = "24/05/2024",
                time = "10:30 AM",
                status = ModerationStatus.APRBADA
            ),
            ModerationItem(
                title = "Plomería Express",
                resultado = "RECHAZADA",
                userName = "Maria Garcia",
                actionPerformed = "Revisión de Perfil",
                reason = "Certificado de identidad ilegible",
                date = "23/05/2024",
                time = "03:15 PM",
                status = ModerationStatus.RECHAZADA
            ),
            ModerationItem(
                title = "Electricista Certificado",
                resultado = "APROBADA",
                userName = "Carlos Ruiz",
                actionPerformed = "Validación de Título",
                reason = "Experiencia comprobada",
                date = "22/05/2024",
                time = "09:00 AM",
                status = ModerationStatus.APRBADA
            )
        )
    }
}