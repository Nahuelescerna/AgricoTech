package cl.duoc.agricotech.data.repository

import cl.duoc.agricotech.domain.model.Cotizacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Cotizaciones enviadas durante la sesión.
 * Vive en memoria: es el paso previo a Room, y ya expone un Flow
 * para que las pantallas se enteren de los cambios.
 */
object CotizacionRepositorio {

    private val _cotizaciones = MutableStateFlow<List<Cotizacion>>(emptyList())
    val cotizaciones: StateFlow<List<Cotizacion>> = _cotizaciones.asStateFlow()

    // Se reemplaza la cotización previa de esa solicitud: una sola propuesta por trabajo
    fun registrar(cotizacion: Cotizacion) {
        _cotizaciones.update { actuales ->
            actuales.filterNot { it.idSolicitud == cotizacion.idSolicitud } + cotizacion
        }
    }

    fun porSolicitud(idSolicitud: Int): Cotizacion? =
        _cotizaciones.value.firstOrNull { it.idSolicitud == idSolicitud }
}
