package cl.duoc.agricotech.data.repository

import cl.duoc.agricotech.data.remote.RetrofitCliente
import cl.duoc.agricotech.data.remote.aDominio
import cl.duoc.agricotech.data.remote.aDto
import cl.duoc.agricotech.data.remote.dto.CambioEstadoDto
import cl.duoc.agricotech.data.remote.ejecutarLlamada
import cl.duoc.agricotech.domain.model.Cotizacion
import cl.duoc.agricotech.domain.model.NuevaCotizacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Cotizaciones respaldadas por la API de Xano.
 * La propuesta aceptada por cada solicitud se guarda aparte para que la UI
 * la distinga sin tener que recalcular el estado de todas las filas.
 */
object CotizacionRepositorio {

    private const val ESTADO_ACEPTADA = "ACEPTADA"

    private val api = RetrofitCliente.api

    private val _cotizaciones = MutableStateFlow<List<Cotizacion>>(emptyList())
    val cotizaciones: StateFlow<List<Cotizacion>> = _cotizaciones.asStateFlow()

    // Propuesta aceptada por solicitud: solo puede haber una por trabajo
    private val _aceptadas = MutableStateFlow<Map<Int, Cotizacion>>(emptyMap())
    val aceptadas: StateFlow<Map<Int, Cotizacion>> = _aceptadas.asStateFlow()

    /** GET /cotizacion: todas las propuestas que existen en el servidor. */
    suspend fun sincronizar(): Result<List<Cotizacion>> = ejecutarLlamada {
        val remotas = api.listarCotizaciones().map { it.aDominio() }
        _cotizaciones.value = remotas

        // Si la app se reinicia, las propuestas ya aceptadas se recuperan del servidor
        _aceptadas.value = remotas
            .filter { it.estado.equals(ESTADO_ACEPTADA, ignoreCase = true) }
            .associateBy { it.idSolicitud }

        remotas
    }

    /** POST /cotizacion: envía la propuesta y la deja disponible en la caché. */
    suspend fun crear(nueva: NuevaCotizacion): Result<Cotizacion> = ejecutarLlamada {
        val cotizacion = api.crearCotizacion(nueva.aDto()).aDominio()
        registrar(cotizacion)
        cotizacion
    }

    /** PATCH /cotizacion/{id}: marca la propuesta como aceptada en el servidor. */
    suspend fun aceptar(cotizacion: Cotizacion): Result<Cotizacion> = ejecutarLlamada {
        val actualizada = api.actualizarEstado(cotizacion.id, CambioEstadoDto(ESTADO_ACEPTADA)).aDominio()
        registrar(actualizada)
        _aceptadas.update { it + (actualizada.idSolicitud to actualizada) }
        actualizada
    }

    fun porSolicitud(idSolicitud: Int): List<Cotizacion> =
        _cotizaciones.value.filter { it.idSolicitud == idSolicitud }

    fun aceptadaDe(idSolicitud: Int): Cotizacion? = _aceptadas.value[idSolicitud]

    // Una sola propuesta por aplicador y solicitud: si vuelve a cotizar, se reemplaza
    private fun registrar(cotizacion: Cotizacion) {
        _cotizaciones.update { actuales ->
            actuales.filterNot {
                it.idSolicitud == cotizacion.idSolicitud &&
                    it.aplicadorCorreo == cotizacion.aplicadorCorreo
            } + cotizacion
        }
    }
}
