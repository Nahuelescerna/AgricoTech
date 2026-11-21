package cl.duoc.agricotech.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Calificación tal como la devuelve Xano (tabla `calificacion`).
 * El correo del aplicador es la llave para calcular su promedio en el perfil.
 */
@Serializable
data class CalificacionDto(
    val id: Int = 0,
    @SerialName("solicitud_id") val solicitudId: Int = 0,
    @SerialName("aplicador_correo") val aplicadorCorreo: String = "",
    val estrellas: Int = 0,
    val comentario: String = ""
)
