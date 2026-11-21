package cl.duoc.agricotech.domain.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas de las validaciones del dominio (no necesitan Android).
 */
class ValidacionesTest {

    @Test
    fun `un email con formato correcto es valido`() {
        assertTrue(esEmailValido("ana@correo.cl"))
        assertTrue(esEmailValido("productor.agricola@duocuc.cl"))
    }

    @Test
    fun `un email sin arroba es invalido`() {
        assertFalse(esEmailValido("ana.correo.cl"))
        assertFalse(esEmailValido(""))
    }

    @Test
    fun `un rut con guion y digito verificador es valido`() {
        assertTrue(esRutValido("12345678-9"))
        assertTrue(esRutValido("12.345.678-K"))
    }

    @Test
    fun `un rut sin guion es invalido`() {
        assertFalse(esRutValido("123456789"))
        assertFalse(esRutValido("123-4"))
    }

    @Test
    fun `un telefono movil chileno de nueve digitos es valido`() {
        assertTrue(esTelefonoValido("+56 9 1234 5678"))
        assertFalse(esTelefonoValido("12345678"))
    }
}
