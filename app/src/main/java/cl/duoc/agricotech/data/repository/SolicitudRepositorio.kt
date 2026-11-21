package cl.duoc.agricotech.data.repository

import cl.duoc.agricotech.data.remote.RetrofitCliente
import cl.duoc.agricotech.data.remote.aDominio
import cl.duoc.agricotech.data.remote.ejecutarLlamada
import cl.duoc.agricotech.domain.model.Solicitud
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Solicitudes respaldadas por la API de Xano.
 *
 * La lista se guarda en un StateFlow para que las pantallas se enteren de los
 * cambios sin volver a consultar en cada navegación, y las funciones `suspend`
 * son las que hablan con el servidor.
 */
object SolicitudRepositorio {

    private const val ESTADO_ADJUDICADA = "ADJUDICADA"

    private val api = RetrofitCliente.api

    private val _solicitudes = MutableStateFlow<List<Solicitud>>(emptyList())
    val solicitudes: StateFlow<List<Solicitud>> = _solicitudes.asStateFlow()

    // Fotos de la cámara: se quedan en el teléfono, la API guarda el resto de la solicitud
    private val fotosPorSolicitud = mutableMapOf<Int, List<String>>()

    /** GET /solicitud: reemplaza la caché con lo publicado en el servidor. */
    suspend fun sincronizar(): Result<List<Solicitud>> = ejecutarLlamada {
        val remotas = api.listarSolicitudes()
            .filterNot { it.estado.equals(ESTADO_ADJUDICADA, ignoreCase = true) }
            .map { dto -> dto.aDominio(fotosPorSolicitud[dto.id].orEmpty()) }
        _solicitudes.value = remotas
        remotas
    }

    /** GET /solicitud/{id}: trae el detalle y lo deja en la caché. */
    suspend fun obtener(id: Int): Result<Solicitud> = ejecutarLlamada {
        val dto = api.obtenerSolicitud(id)
        val solicitud = dto.aDominio(fotosPorSolicitud[dto.id].orEmpty())
        _solicitudes.update { actuales -> actuales.filterNot { it.id == id } + solicitud }
        solicitud
    }

    /** Lectura de la caché: al llegar al detalle la lista del home ya está cargada. */
    fun obtenerSolicitud(id: Int): Solicitud? =
        _solicitudes.value.firstOrNull { it.id == id }

    /** Alta en la caché para que la lista se actualice apenas se publica. */
    fun agregar(solicitud: Solicitud) {
        if (solicitud.fotos.isNotEmpty()) fotosPorSolicitud[solicitud.id] = solicitud.fotos
        _solicitudes.update { actuales -> actuales + solicitud }
    }

    fun siguienteId(): Int = (_solicitudes.value.maxOfOrNull { it.id } ?: 0) + 1
}
