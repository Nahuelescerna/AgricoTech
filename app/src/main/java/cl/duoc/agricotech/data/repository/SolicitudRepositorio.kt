package cl.duoc.agricotech.data.repository

import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.domain.model.TipoProducto
import cl.duoc.agricotech.domain.model.Turno

/**
 * Datos ficticios para probar el matching (el caso exige datos sintéticos).
 * Cuando entre Room, esta clase pasa a leer de la base de datos
 * y las pantallas no cambian.
 */
object SolicitudRepositorio {

    fun obtenerSolicitud(id: Int): Solicitud? =
        obtenerSolicitudes().firstOrNull { it.id == id }

    fun obtenerSolicitudes(): List<Solicitud> = listOf(
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
