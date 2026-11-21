package cl.duoc.agricotech.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Solicitud tal como la devuelve Xano (tabla `solicitud`).
 *
 * Todos los campos tienen valor por defecto: si el servidor agrega una columna
 * nueva, el JSON sigue parseando sin romper la app.
 */
@Serializable
data class SolicitudDto(
    val id: Int = 0,
    @SerialName("productor_correo") val productorCorreo: String = "",
    val parcela: String = "",
    val cultivo: String = "",
    val comuna: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    @SerialName("distancia_km") val distanciaKm: Int = 0,
    val hectareas: Int = 0,
    @SerialName("tipo_producto") val tipoProducto: String = "",
    val turno: String = "",
    @SerialName("agua_disponible") val aguaDisponible: Boolean = false,
    val obstaculos: String = "",
    val estado: String = "",
    @SerialName("calificacion_productor") val calificacionProductor: Double = 0.0,
    @SerialName("trabajos_publicados") val trabajosPublicados: Int = 0
)
