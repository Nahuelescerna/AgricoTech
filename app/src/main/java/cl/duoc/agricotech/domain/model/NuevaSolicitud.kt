package cl.duoc.agricotech.domain.model

/**
 * Datos que el productor completa para publicar una solicitud.
 * Es la entrada del `POST /solicitud`: no lleva id ni estado, los asigna el servidor.
 */
data class NuevaSolicitud(
    val productorCorreo: String,
    val parcela: String,
    val cultivo: String,
    val comuna: String,
    val latitud: Double,
    val longitud: Double,
    val distanciaKm: Int,
    val hectareas: Int,
    val tipoProducto: TipoProducto,
    val turno: Turno,
    val aguaDisponible: Boolean,
    val obstaculos: String,
    val fotos: List<String> = emptyList()
)
