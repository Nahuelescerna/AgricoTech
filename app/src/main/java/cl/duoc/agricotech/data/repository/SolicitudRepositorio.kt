package cl.duoc.agricotech.data.repository

import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.domain.model.TipoProducto
import cl.duoc.agricotech.domain.model.Turno
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Solicitudes del MVP con datos ficticios (el caso exige datos sintéticos).
 * Vive en memoria y expone un Flow: cuando el productor publica una,
 * el home del aplicador se entera sin recargar.
 */
object SolicitudRepositorio {

    private val _solicitudes = MutableStateFlow(solicitudesIniciales())
    val solicitudes: StateFlow<List<Solicitud>> = _solicitudes.asStateFlow()

    fun obtenerSolicitud(id: Int): Solicitud? =
        _solicitudes.value.firstOrNull { it.id == id }

    fun agregar(solicitud: Solicitud) {
        _solicitudes.update { actuales -> actuales + solicitud }
    }

    // Id correlativo para la solicitud que se acaba de publicar
    fun siguienteId(): Int = (_solicitudes.value.maxOfOrNull { it.id } ?: 0) + 1

    private fun solicitudesIniciales(): List<Solicitud> = listOf(
        Solicitud(
            id = 1,
            parcela = "Parcela 12 ha · Paltos",
            cultivo = "Paltos",
            comuna = "Rancagua",
            distanciaKm = 8,
            hectareas = 12,
            tipoProducto = TipoProducto.LIQUIDO,
            turno = Turno.DIURNO,
            aguaDisponible = true,
            obstaculos = "Cercos perimetrales y una línea de riego en el borde norte.",
            calificacionProductor = 4.8,
            trabajosPublicados = 24
        ),
        Solicitud(
            id = 2,
            parcela = "Fundo El Roble · Trigo",
            cultivo = "Trigo",
            comuna = "San Vicente",
            distanciaKm = 14,
            hectareas = 30,
            tipoProducto = TipoProducto.SOLIDO,
            turno = Turno.NOCTURNO,
            aguaDisponible = false,
            obstaculos = "Tendido eléctrico bajo y camino de tierra en el acceso.",
            calificacionProductor = 4.5,
            trabajosPublicados = 11
        ),
        Solicitud(
            id = 3,
            parcela = "Parcela 5 ha · Cerezos",
            cultivo = "Cerezos",
            comuna = "Requínoa",
            distanciaKm = 21,
            hectareas = 5,
            tipoProducto = TipoProducto.LIQUIDO,
            turno = Turno.DIURNO,
            aguaDisponible = true,
            obstaculos = "Sin obstáculos relevantes. Acceso por camino interior.",
            calificacionProductor = 4.9,
            trabajosPublicados = 37
        )
    )
}
