package cl.duoc.agricotech.data.repository

import cl.duoc.agricotech.domain.model.Cotizacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Cotizaciones de la sesión, con algunas de ejemplo para poder comparar.
 * Vive en memoria: es el paso previo a Room.
 */
object CotizacionRepositorio {

    private val _cotizaciones = MutableStateFlow(cotizacionesDeEjemplo())
    val cotizaciones: StateFlow<List<Cotizacion>> = _cotizaciones.asStateFlow()

    // Propuesta aceptada por solicitud: solo puede haber una por trabajo
    private val _aceptadas = MutableStateFlow<Map<Int, Cotizacion>>(emptyMap())
    val aceptadas: StateFlow<Map<Int, Cotizacion>> = _aceptadas.asStateFlow()

    fun porSolicitud(idSolicitud: Int): List<Cotizacion> =
        _cotizaciones.value.filter { it.idSolicitud == idSolicitud }

    fun aceptadaDe(idSolicitud: Int): Cotizacion? = _aceptadas.value[idSolicitud]

    // Una sola propuesta por aplicador y solicitud: si vuelve a cotizar, se reemplaza
    fun registrar(cotizacion: Cotizacion) {
        _cotizaciones.update { actuales ->
            actuales.filterNot {
                it.idSolicitud == cotizacion.idSolicitud && it.aplicador == cotizacion.aplicador
            } + cotizacion
        }
    }

    fun aceptar(cotizacion: Cotizacion) {
        _aceptadas.update { it + (cotizacion.idSolicitud to cotizacion) }
    }

    private fun cotizacionesDeEjemplo(): List<Cotizacion> = listOf(
        Cotizacion(
            idSolicitud = 1,
            aplicador = "Esteban Pérez",
            distanciaKm = 6,
            calificacion = 4.9,
            especialidad = "Paltos",
            tarifaPorHectarea = 11.5,
            total = 138.0,
            observacion = "Incluyo traslado del dron y operador certificado."
        ),
        Cotizacion(
            idSolicitud = 1,
            aplicador = "María Soto",
            distanciaKm = 12,
            calificacion = 5.0,
            especialidad = "Paltos",
            tarifaPorHectarea = 13.0,
            total = 156.0,
            observacion = "Trabajo con equipo de 20 L y registro fotográfico."
        ),
        Cotizacion(
            idSolicitud = 1,
            aplicador = "Servicios Agrícolas Rivas",
            distanciaKm = 18,
            calificacion = 4.4,
            especialidad = "Trigo y cereales",
            tarifaPorHectarea = 9.8,
            total = 117.6,
            observacion = "Disponibilidad inmediata."
        ),
        Cotizacion(
            idSolicitud = 2,
            aplicador = "María Soto",
            distanciaKm = 9,
            calificacion = 5.0,
            especialidad = "Trigo y cereales",
            tarifaPorHectarea = 10.2,
            total = 306.0,
            observacion = "Puedo cubrir las 30 ha en dos turnos nocturnos."
        ),
        Cotizacion(
            idSolicitud = 2,
            aplicador = "Esteban Pérez",
            distanciaKm = 15,
            calificacion = 4.9,
            especialidad = "Paltos",
            tarifaPorHectarea = 11.0,
            total = 330.0,
            observacion = "Requiero confirmar acceso para camión."
        )
    )
}
