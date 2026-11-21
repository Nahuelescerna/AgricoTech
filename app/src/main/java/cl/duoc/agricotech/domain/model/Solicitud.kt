package cl.duoc.agricotech.domain.model

/** Tipo de producto que se va a aplicar en el campo. */
enum class TipoProducto(val etiqueta: String) {
    LIQUIDO("Líquido"),
    SOLIDO("Granulado");

    companion object {
        // La API entrega texto libre: si llega algo desconocido se usa el valor por defecto
        fun desde(valor: String): TipoProducto =
            entries.firstOrNull { it.name.equals(valor, ignoreCase = true) } ?: LIQUIDO
    }
}

/** Turno en que el productor necesita el servicio. */
enum class Turno(val etiqueta: String) {
    DIURNO("Diurno"),
    NOCTURNO("Nocturno");

    companion object {
        fun desde(valor: String): Turno =
            entries.firstOrNull { it.name.equals(valor, ignoreCase = true) } ?: DIURNO
    }
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
    val fotos: List<String> = emptyList(),
    val calificacionProductor: Double,
    val trabajosPublicados: Int,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0
)
