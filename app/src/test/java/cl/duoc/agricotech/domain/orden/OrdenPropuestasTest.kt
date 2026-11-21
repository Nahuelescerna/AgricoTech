package cl.duoc.agricotech.domain.orden

import cl.duoc.agricotech.domain.model.Cotizacion
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas del orden de propuestas (RNF-04): cercanía, puntaje y especialidad.
 * También comprueba que el orden no descarte ninguna propuesta.
 */
class OrdenPropuestasTest {

    private var id = 0

    private fun propuesta(
        aplicador: String,
        distanciaKm: Int,
        calificacion: Double,
        especialidad: String
    ) = Cotizacion(
        id = ++id,
        idSolicitud = 1,
        aplicador = aplicador,
        aplicadorCorreo = "$aplicador@correo.cl",
        distanciaKm = distanciaKm,
        calificacion = calificacion,
        especialidad = especialidad,
        tarifaPorHectarea = 2.0,
        total = 24.0,
        observacion = ""
    )

    @Test
    fun `la propuesta mas cercana queda primero aunque tenga menos puntaje`() {
        val lejos = propuesta("Lejos", distanciaKm = 20, calificacion = 5.0, especialidad = "Paltos")
        val cerca = propuesta("Cerca", distanciaKm = 5, calificacion = 4.0, especialidad = "Trigo")

        val ordenadas = ordenarPropuestas(listOf(lejos, cerca), cultivo = "Paltos")

        assertEquals("Cerca", ordenadas.first().aplicador)
    }

    @Test
    fun `a igual distancia gana el mejor puntaje`() {
        val mejor = propuesta("Mejor", distanciaKm = 8, calificacion = 4.9, especialidad = "Trigo")
        val peor = propuesta("Peor", distanciaKm = 8, calificacion = 4.2, especialidad = "Paltos")

        val ordenadas = ordenarPropuestas(listOf(peor, mejor), cultivo = "Paltos")

        assertEquals("Mejor", ordenadas.first().aplicador)
    }

    @Test
    fun `a igual distancia y puntaje gana la especialidad del cultivo`() {
        val especialista = propuesta("Especialista", 8, 4.5, "Paltos")
        val otro = propuesta("Otro", 8, 4.5, "Trigo")

        val ordenadas = ordenarPropuestas(listOf(otro, especialista), cultivo = "Paltos")

        assertEquals("Especialista", ordenadas.first().aplicador)
    }

    @Test
    fun `el orden mantiene todas las propuestas`() {
        val propuestas = listOf(
            propuesta("A", 15, 4.0, "Trigo"),
            propuesta("B", 5, 4.8, "Paltos"),
            propuesta("C", 9, 4.5, "Cerezos")
        )

        assertEquals(propuestas.size, ordenarPropuestas(propuestas, cultivo = "Paltos").size)
    }
}
