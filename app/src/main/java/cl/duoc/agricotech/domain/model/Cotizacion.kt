package cl.duoc.agricotech.domain.model

/**
 * Propuesta económica que un aplicador envía sobre una solicitud.
 * Incluye los datos que el productor necesita para comparar (RF-04):
 * precio, distancia, calificación y especialidad.
 */
data class Cotizacion(
    val idSolicitud: Int,
    val aplicador: String,
    val distanciaKm: Int,
    val calificacion: Double,
    val especialidad: String,
    val tarifaPorHectarea: Double,
    val total: Double,
    val observacion: String
)
