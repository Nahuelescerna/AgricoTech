package cl.duoc.agricotech.domain.geo

import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.domain.model.Ubicacion
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private const val RADIO_TIERRA_KM = 6371.0

/**
 * Distancia en kilómetros entre dos coordenadas usando la fórmula de Haversine.
 * Es una función pura (sin Android) para poder probarla con JUnit.
 */
fun distanciaKm(desde: Ubicacion, hastaLatitud: Double, hastaLongitud: Double): Double {
    val diferenciaLatitud = Math.toRadians(hastaLatitud - desde.latitud)
    val diferenciaLongitud = Math.toRadians(hastaLongitud - desde.longitud)

    val a = sin(diferenciaLatitud / 2).pow(2) +
        cos(Math.toRadians(desde.latitud)) * cos(Math.toRadians(hastaLatitud)) *
        sin(diferenciaLongitud / 2).pow(2)

    return 2 * RADIO_TIERRA_KM * asin(sqrt(a))
}

/**
 * Copia la solicitud con la distancia real desde el teléfono del usuario.
 * Si la solicitud no trae coordenadas se deja la distancia que vino de la API.
 */
fun Solicitud.conDistanciaDesde(ubicacion: Ubicacion): Solicitud {
    if (latitud == 0.0 && longitud == 0.0) return this
    return copy(distanciaKm = distanciaKm(ubicacion, latitud, longitud).roundToInt())
}
