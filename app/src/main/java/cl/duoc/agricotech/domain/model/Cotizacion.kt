package cl.duoc.agricotech.domain.model

/**
 * Propuesta económica que un aplicador envía sobre una solicitud.
 * Se guarda en memoria por ahora; con Room pasa a ser una entidad.
 */
data class Cotizacion(
    val idSolicitud: Int,
    val tarifaPorHectarea: Double,
    val total: Double,
    val observacion: String
)
