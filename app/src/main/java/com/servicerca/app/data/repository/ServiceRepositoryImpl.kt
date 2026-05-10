package com.servicerca.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
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
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ServiceRepository {

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    override val services: StateFlow<List<Service>> = _services.asStateFlow()

    private val servicesCollection = firestore.collection("services")

    init {
        observeAllServices()
    }

    private fun observeAllServices() {
        servicesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("ServiceRepository", "Error observando servicios", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                _services.value = snapshot.documents.mapNotNull { it.toService() }
            }
        }
    }

    override suspend fun save(service: Service) {
        try {
            servicesCollection.document(service.id).set(service.toFirestoreMap()).await()
        } catch (e: Exception) {
            Log.e("ServiceRepository", "Error guardando servicio", e)
            throw e
        }
    }

    override suspend fun update(service: Service) {
        try {
            servicesCollection.document(service.id).set(service.toFirestoreMap()).await()
        } catch (e: Exception) {
            Log.e("ServiceRepository", "Error actualizando servicio", e)
            throw e
        }
    }

    override suspend fun delete(id: String) {
        try {
            servicesCollection.document(id).update("status", ServiceStatus.DELETED.name).await()
        } catch (e: Exception) {
            Log.e("ServiceRepository", "Error eliminando servicio", e)
            throw e
        }
    }

    override suspend fun findById(id: String): Service? {
        return try {
            val doc = servicesCollection.document(id).get().await()
            doc.toService()
        } catch (e: Exception) {
            Log.e("ServiceRepository", "Error buscando servicio por id", e)
            null
        }
    }

    override fun findByOwnerId(ownerId: String): StateFlow<List<Service>> {
        return _services.map { list ->
            list.filter { it.ownerId == ownerId }
        }.stateIn(
            scope = GlobalScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _services.value.filter { it.ownerId == ownerId }
        )
    }

    override fun findByStatus(status: ServiceStatus): StateFlow<List<Service>> {
        return _services.map { list ->
            list.filter { it.status == status }
        }.stateIn(
            scope = GlobalScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _services.value.filter { it.status == status }
        )
    }

    override fun findByType(type: String): StateFlow<List<Service>> {
        return _services.map { list ->
            list.filter { it.type == type }
        }.stateIn(
            scope = GlobalScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _services.value.filter { it.type == type }
        )
    }

    override suspend fun toggleLike(serviceId: String, userId: String) {
        try {
            val service = _services.value.find { it.id == serviceId } ?: return
            val fieldUpdate: Any = if (service.likedBy.contains(userId)) {
                FieldValue.arrayRemove(userId)
            } else {
                FieldValue.arrayUnion(userId)
            }
            servicesCollection.document(serviceId).update("likedBy", fieldUpdate).await()
        } catch (e: Exception) {
            Log.e("ServiceRepository", "Error en toggleLike", e)
        }
    }

    private fun Service.toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "latitude" to location.latitude,
        "longitude" to location.longitude,
        "priceMin" to priceMin,
        "priceMax" to priceMax,
        "status" to status.name,
        "type" to type,
        "photoUrl" to photoUrl,
        "ownerId" to ownerId,
        "likedBy" to likedBy
    )

    private fun DocumentSnapshot.toService(): Service? {
        return try {
            Service(
                id = getString("id") ?: this.id,
                title = getString("title") ?: "",
                description = getString("description") ?: "",
                location = Location(
                    latitude = getDouble("latitude") ?: 0.0,
                    longitude = getDouble("longitude") ?: 0.0
                ),
                priceMin = getDouble("priceMin") ?: 0.0,
                priceMax = getDouble("priceMax") ?: 0.0,
                status = ServiceStatus.valueOf(getString("status") ?: ServiceStatus.PENDING.name),
                type = getString("type") ?: "",
                photoUrl = getString("photoUrl") ?: "",
                ownerId = getString("ownerId") ?: "",
                likedBy = (get("likedBy") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            )
        } catch (e: Exception) {
            Log.e("ServiceRepository", "Error parseando documento ${this.id}", e)
            null
        }
    }
}
