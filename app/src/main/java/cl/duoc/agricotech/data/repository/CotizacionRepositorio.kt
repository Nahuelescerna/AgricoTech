package cl.duoc.agricotech.data.repository

import cl.duoc.agricotech.data.remote.RetrofitCliente
import cl.duoc.agricotech.data.remote.aDominio
import cl.duoc.agricotech.data.remote.ejecutarLlamada
import cl.duoc.agricotech.domain.model.Cotizacion
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
        remotas
    }

    fun porSolicitud(idSolicitud: Int): List<Cotizacion> =
        _cotizaciones.value.filter { it.idSolicitud == idSolicitud }

    fun aceptadaDe(idSolicitud: Int): Cotizacion? = _aceptadas.value[idSolicitud]

    // Una sola propuesta por aplicador y solicitud: si vuelve a cotizar, se reemplaza
    fun registrar(cotizacion: Cotizacion) {
        _cotizaciones.update { actuales ->
            actuales.filterNot {
                it.idSolicitud == cotizacion.idSolicitud &&
                    it.aplicadorCorreo == cotizacion.aplicadorCorreo
            } + cotizacion
        }
    }

    fun aceptar(cotizacion: Cotizacion) {
        _aceptadas.update { it + (cotizacion.idSolicitud to cotizacion) }
    }
}
