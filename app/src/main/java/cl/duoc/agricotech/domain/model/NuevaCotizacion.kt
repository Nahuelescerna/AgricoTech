package cl.duoc.agricotech.domain.model

/**
 * Datos que el aplicador envía al cotizar.
 * La calificación y la especialidad viajan con la propuesta para poder
 * ordenarla después sin consultar otras tablas (RNF-04).
 */
data class NuevaCotizacion(
    val idSolicitud: Int,
    val aplicadorCorreo: String,
    val aplicadorNombre: String,
    val distanciaKm: Int,
    val calificacion: Double,
    val especialidad: String,
    val tarifaPorHectarea: Double,
    val total: Double,
    val observacion: String
)
