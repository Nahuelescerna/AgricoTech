package cl.duoc.agricotech.domain.orden

import cl.duoc.agricotech.domain.model.Cotizacion

/**
 * Orden de las propuestas (RNF-04): primero la cercanía, después el puntaje
 * y por último la especialidad en el cultivo. No descarta propuestas,
 * solo las ordena.
 *
 * Vive en el dominio (y no en el ViewModel) para poder probarla con JUnit
 * sin levantar Android.
 */
fun ordenarPropuestas(propuestas: List<Cotizacion>, cultivo: String): List<Cotizacion> =
    propuestas.sortedWith(
        compareBy<Cotizacion> { it.distanciaKm }
            .thenByDescending { it.calificacion }
            .thenByDescending { it.especialidad.equals(cultivo, ignoreCase = true) }
    )
