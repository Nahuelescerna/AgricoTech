package cl.duoc.agricotech.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Cuerpo del `POST /solicitud`. */
@Serializable
data class NuevaSolicitudDto(
    @SerialName("productor_correo") val productorCorreo: String,
    val parcela: String,
    val cultivo: String,
    val comuna: String,
    val latitud: Double,
    val longitud: Double,
    @SerialName("distancia_km") val distanciaKm: Int,
    val hectareas: Int,
    @SerialName("tipo_producto") val tipoProducto: String,
    val turno: String,
    @SerialName("agua_disponible") val aguaDisponible: Boolean,
    val obstaculos: String
)
