package cl.duoc.agricotech.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Cuerpo del `POST /cotizacion`. */
@Serializable
data class NuevaCotizacionDto(
    @SerialName("solicitud_id") val solicitudId: Int,
    @SerialName("aplicador_correo") val aplicadorCorreo: String,
    @SerialName("aplicador_nombre") val aplicadorNombre: String,
    @SerialName("tarifa_uf_ha") val tarifaUfHa: Double,
    @SerialName("total_uf") val totalUf: Double,
    val mensaje: String,
    @SerialName("distancia_km") val distanciaKm: Int,
    val calificacion: Double,
    val especialidad: String
)
