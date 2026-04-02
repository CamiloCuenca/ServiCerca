package com.servicerca.app.data.repository

import com.servicerca.app.domain.model.Location
import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import com.servicerca.app.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        val filtered = _services.value.filter { it.status == status }
        return MutableStateFlow(filtered).asStateFlow()
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
                photoUrl = "https://picsum.photos/200?random=10",
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
                photoUrl = "https://picsum.photos/200?random=11",
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
                photoUrl = "https://picsum.photos/200?random=12",
                ownerId = "1"
            )
        )
    }
}