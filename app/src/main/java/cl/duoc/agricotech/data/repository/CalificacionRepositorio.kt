package cl.duoc.agricotech.data.repository

import cl.duoc.agricotech.domain.model.Calificacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Calificaciones de la sesión (en memoria, igual que las cotizaciones).
 * El promedio se calcula sobre las estrellas recibidas por cada aplicador.
 */
object CalificacionRepositorio {

    private val _calificaciones = MutableStateFlow<List<Calificacion>>(emptyList())
    val calificaciones: StateFlow<List<Calificacion>> = _calificaciones.asStateFlow()

    // Si se vuelve a calificar la misma solicitud, se reemplaza la anterior
    fun registrar(calificacion: Calificacion) {
        _calificaciones.update { actuales ->
            actuales.filterNot { it.idSolicitud == calificacion.idSolicitud } + calificacion
        }
    }

    fun porSolicitud(idSolicitud: Int): Calificacion? =
        _calificaciones.value.firstOrNull { it.idSolicitud == idSolicitud }

    fun promedioDe(aplicador: String): Double? {
        val recibidas = _calificaciones.value.filter { it.aplicador == aplicador }
        if (recibidas.isEmpty()) return null
        return recibidas.map { it.estrellas }.average()
    }

    fun cantidadDe(aplicador: String): Int =
        _calificaciones.value.count { it.aplicador == aplicador }
}
