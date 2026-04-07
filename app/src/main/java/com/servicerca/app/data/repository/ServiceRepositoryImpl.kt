package com.servicerca.app.data.repository


import com.servicerca.app.domain.model.Location
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.ServiceRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepositoryImpl @Inject constructor() : ServiceRepository {

    // Lista reactiva de servicios en memoria
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    override val services: StateFlow<List<Service>> = _services.asStateFlow()

    init {
        _services.value = fetchInitialServices()
    }

    override suspend fun save(service: Service) {
        _services.value += service
    }

    override suspend fun update(service: Service) {
        _services.value = _services.value.map {
            if (it.id == service.id) service else it
        }
    }

    override suspend fun delete(id: String) {
        _services.value = _services.value.filter { it.id != id }
    }

    override suspend fun findById(id: String): Service? {
        return _services.value.find { it.id == id }
    }

    override fun findByOwnerId(ownerId: String): StateFlow<List<Service>> {
        // Filtramos la lista actual para el owner específico
        val filtered = _services.value.filter { it.ownerId == ownerId }
        return MutableStateFlow(filtered).asStateFlow()
    }

    override fun findByStatus(status: ServiceStatus): StateFlow<List<Service>> {
        return _services.map { list ->
            list.filter { it.status == status }
        }.stateIn(
            scope = GlobalScope, // O un scope manejado
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _services.value.filter { it.status == status }
        )
    }

    override fun findByType(type: String): StateFlow<List<Service>> {
        val filtered = _services.value.filter { it.type == type }
        return MutableStateFlow(filtered).asStateFlow()
    }

    private fun fetchInitialServices(): List<Service> {
        return listOf(
            Service(
                id = "1",
                title = "Plomería General",
                description = "Reparación de tuberías, fugas de agua y mantenimiento de baños.",
                location = Location(4.533887, -75.671333),
                priceMin = 30000.0,
                priceMax = 80000.0,
                status = ServiceStatus.PENDING,
                type = "Plomería",
                photoUrl = "https://projectssdn.com/wp-content/uploads/elementor/thumbs/plomeria-en-general-qp5x9n6u64ze4tk30xqoxt57okaxdr7apr7hp13vds.png",
                ownerId = "1"
            ),
            Service(
                id = "2",
                title = "Electricista Residencial",
                description = "Instalaciones eléctricas, cambio de breakers y cableado.",
                location = Location(4.535000, -75.675000),
                priceMin = 45000.0,
                priceMax = 150000.0,
                status = ServiceStatus.IN_PROGRESS,
                type = "Electricidad",
                photoUrl = "https://comfenalcoquindio.com/wp-content/uploads/2022/05/tecnico-electricista-en-construccion-residencial-1.jpg",
                ownerId = "2"
            ),
            Service(
                id = "3",
                title = "Limpieza de Muebles",
                description = "Lavado profundo de sofás, colchones y sillas de comedor.",
                location = Location(4.540000, -75.660000),
                priceMin = 60000.0,
                priceMax = 200000.0,
                status = ServiceStatus.RESOLVED,
                type = "Limpieza",
                photoUrl = "https://extremecleangm.com/wp-content/uploads/2025/01/Lavado-de-Muebles-Iniciando-el-2025.jpg",
                ownerId = "1"
            )
        )
    }
}