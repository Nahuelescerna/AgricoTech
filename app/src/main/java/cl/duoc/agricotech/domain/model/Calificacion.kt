package cl.duoc.agricotech.domain.model

/**
 * Evaluación que el productor deja al aplicador al terminar el trabajo (RF-05).
 * Una sola calificación por solicitud.
 */
data class Calificacion(
    val idSolicitud: Int,
    val aplicador: String,
    val estrellas: Int,
    val comentario: String
)
