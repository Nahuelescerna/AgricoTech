package cl.duoc.agricotech.domain.model

/** Tipo de producto que se va a aplicar en el campo. */
enum class TipoProducto(val etiqueta: String) {
    LIQUIDO("Líquido"),
    SOLIDO("Granulado")
}

/** Turno en que el productor necesita el servicio. */
enum class Turno(val etiqueta: String) {
    DIURNO("Diurno"),
    NOCTURNO("Nocturno")
}

/**
 * Solicitud publicada por un productor.
 * Es lo que el aplicador ve como "oferta de trabajo".
 */
data class Solicitud(
    val id: Int,
    val parcela: String,
    val cultivo: String,
    val comuna: String,
    val distanciaKm: Int,
    val hectareas: Int,
    val tipoProducto: TipoProducto,
    val turno: Turno,
    val aguaDisponible: Boolean,
    val obstaculos: String,
    val calificacionProductor: Double,
    val trabajosPublicados: Int
)
