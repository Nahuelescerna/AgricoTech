package cl.duoc.agricotech.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Cotización tal como la devuelve Xano (tabla `cotizacion`).
 * Los campos de distancia, calificación y especialidad permiten ordenar
 * las propuestas sin consultar otra tabla (RNF-04).
 */
@Serializable
data class CotizacionDto(
    val id: Int = 0,
    @SerialName("solicitud_id") val solicitudId: Int = 0,
    @SerialName("aplicador_correo") val aplicadorCorreo: String = "",
    @SerialName("aplicador_nombre") val aplicadorNombre: String = "",
    @SerialName("tarifa_uf_ha") val tarifaUfHa: Double = 0.0,
    @SerialName("total_uf") val totalUf: Double = 0.0,
    val mensaje: String = "",
    val estado: String = "",
    @SerialName("distancia_km") val distanciaKm: Int = 0,
    val calificacion: Double = 0.0,
    val especialidad: String = ""
)
