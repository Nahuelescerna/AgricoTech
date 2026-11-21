package cl.duoc.agricotech.data.repository

import cl.duoc.agricotech.data.remote.RetrofitCliente
import cl.duoc.agricotech.data.remote.aDominio
import cl.duoc.agricotech.data.remote.aDto
import cl.duoc.agricotech.data.remote.ejecutarLlamada
import cl.duoc.agricotech.domain.model.Calificacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Calificaciones respaldadas por la API de Xano.
 * El promedio se calcula sobre las estrellas recibidas por cada aplicador,
 * identificado por su correo.
 */
object CalificacionRepositorio {

    private val api = RetrofitCliente.api

    private val _calificaciones = MutableStateFlow<List<Calificacion>>(emptyList())
    val calificaciones: StateFlow<List<Calificacion>> = _calificaciones.asStateFlow()

    /** GET /calificacion: todas las calificaciones registradas en el servidor. */
    suspend fun sincronizar(): Result<List<Calificacion>> = ejecutarLlamada {
        val remotas = api.listarCalificaciones().map { it.aDominio() }
        _calificaciones.value = remotas
        remotas
    }

    /** POST /calificacion: envía la calificación y reemplaza la anterior de la solicitud. */
    suspend fun registrar(calificacion: Calificacion): Result<Calificacion> = ejecutarLlamada {
        api.crearCalificacion(calificacion.aDto())

        _calificaciones.update { actuales ->
            actuales.filterNot { it.idSolicitud == calificacion.idSolicitud } + calificacion
        }
        calificacion
    }

    fun porSolicitud(idSolicitud: Int): Calificacion? =
        _calificaciones.value.firstOrNull { it.idSolicitud == idSolicitud }

    fun promedioDe(aplicadorCorreo: String): Double? {
        val recibidas = _calificaciones.value.filter {
            it.aplicadorCorreo.equals(aplicadorCorreo, ignoreCase = true)
        }
        if (recibidas.isEmpty()) return null
        return recibidas.map { it.estrellas }.average()
    }

    fun cantidadDe(aplicadorCorreo: String): Int =
        _calificaciones.value.count {
            it.aplicadorCorreo.equals(aplicadorCorreo, ignoreCase = true)
        }
}
