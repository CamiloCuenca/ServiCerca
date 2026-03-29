package com.servicerca.app.domain.repository

import com.servicerca.app.domain.model.Service
import com.servicerca.app.domain.model.ServiceStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ServiceRepository {

    val services: StateFlow<List<Service>>

    suspend fun save(service: Service)

    suspend fun update(service: Service)

    suspend fun delete(id: String)

    suspend fun findById(id: String): Service?

    // Retornamos StateFlow si queremos que la UI se actualice si cambian los servicios del dueño
    fun findByOwnerId(ownerId: String): StateFlow<List<Service>>

    fun findByStatus(status: ServiceStatus): StateFlow<List<Service>>

    fun findByType(type: String): StateFlow<List<Service>>
}