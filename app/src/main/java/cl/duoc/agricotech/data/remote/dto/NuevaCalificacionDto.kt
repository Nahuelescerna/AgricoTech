package cl.duoc.agricotech.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Cuerpo del `POST /calificacion`. */
@Serializable
data class NuevaCalificacionDto(
    @SerialName("solicitud_id") val solicitudId: Int,
    @SerialName("aplicador_correo") val aplicadorCorreo: String,
    val estrellas: Int,
    val comentario: String
)
