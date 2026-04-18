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
        _services.value = _services.value.map {
            if (it.id == id) it.copy(status = ServiceStatus.DELETED) else it
        }
    }

    override suspend fun findById(id: String): Service? {
        return _services.value.find { it.id == id }
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
                type = "Hogar",
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
                status = ServiceStatus.PENDING,
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
                status = ServiceStatus.APPROVED,
                type = "Hogar",
                photoUrl = "https://extremecleangm.com/wp-content/uploads/2025/01/Lavado-de-Muebles-Iniciando-el-2025.jpg",
                ownerId = "1"
            ),
            Service(
                id = "4",
                title = "Reparación de equipos de Cómputo",
                description = "Ofrezco servicio técnico especializado para computadores de escritorio y portátiles. " +
                        "Diagnosticamos y solucionamos fallas de hardware y software, realizamos mantenimiento preventivo " +
                        "y correctivo, eliminación de virus, optimización del sistema, instalación de programas y reemplazo " +
                        "de componentes. Nuestro objetivo es recuperar el rendimiento de tu equipo de forma rápida, segura y confiable.",
                location = Location(4.511738004282885, -75.69229701639676),
                priceMin = 150000.0,
                priceMax = 300000.0,
                status = ServiceStatus.REJECTED,
                type = "Tecnología",
                photoUrl = "https://cdn.pixabay.com/photo/2014/08/26/21/27/service-428539_1280.jpg",
                ownerId = "3"
            ),
            Service(
                id = "5",
                title = "Decoraciones para eventos",
                description = "Ofrezco servicio de decoración para todo tipo de eventos, creando ambientes únicos y memorables " +
                        "según el estilo y la ocasión. Realizo decoraciones para cumpleaños, bodas, aniversarios, bautizos, " +
                        "eventos empresariales y celebraciones especiales. Utilizo globos, telas, arreglos temáticos y detalles" +
                        " personalizados para transformar cada espacio y hacer de tu evento un momento especial e inolvidable.",
                location = Location(4.535000, -75.675000),
                priceMin = 500000.0,
                priceMax = 1800000.0,
                status = ServiceStatus.REJECTED,
                type = "Hogar",
                photoUrl = "https://cdn.pixabay.com/photo/2018/09/05/08/05/party-3655712_1280.jpg",
                ownerId = "2"
            ),
            Service(
                id = "6",
                title = "Desarrollador de páginas web",
                description = "Ofrezco servicio de desarrollo de páginas web modernas, funcionales y adaptadas a las necesidades " +
                        "de cada cliente. Diseñamos sitios web profesionales para empresas, emprendimientos y proyectos personales," +
                        " optimizados para dispositivos móviles y con una navegación rápida y segura. Nuestro objetivo es ayudarte " +
                        "a fortalecer tu presencia en internet y atraer más clientes a través de una plataforma digital atractiva y " +
                        "eficiente.",
                location = Location(4.511738004282885, -75.69229701639676),
                priceMin = 1000000.0,
                priceMax = 2500000.0,
                status = ServiceStatus.APPROVED,
                type = "Tecnología",
                photoUrl = "https://cdn.pixabay.com/photo/2025/09/09/08/52/design-9824072_1280.jpg",
                ownerId = "3"
            )
        )
    }
}