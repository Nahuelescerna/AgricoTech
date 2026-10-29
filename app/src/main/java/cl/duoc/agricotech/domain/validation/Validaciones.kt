package cl.duoc.agricotech.domain.validation

/**
 * Validaciones de datos del dominio.
 * Viven fuera de la UI para reutilizarlas en cualquier pantalla
 * y poder probarlas sin levantar la interfaz.
 */

private val PATRON_EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
// Acepta 12345678-9 o 12.345.678-9 (no se valida el dígito verificador)
private val PATRON_RUT = Regex("^\\d{7,8}-[0-9Kk]$|^\\d{1,2}\\.\\d{3}\\.\\d{3}-[0-9Kk]$")

fun esEmailValido(correo: String): Boolean = PATRON_EMAIL.matches(correo.trim())

fun esContrasenaValida(contrasena: String): Boolean = contrasena.length >= 6

// Nombre o apellido: al menos 2 caracteres y solo letras, espacios, apóstrofo o guion
fun esNombreValido(texto: String): Boolean {
    val limpio = texto.trim()
    if (limpio.length < 2) return false
    return limpio.all { it.isLetter() || it == ' ' || it == '\'' || it == '-' }
}

// Móvil chileno: 9 dígitos que empiezan con 9 (acepta el prefijo +56)
fun esTelefonoValido(telefono: String): Boolean {
    val digitos = telefono.filter { it.isDigit() }
    val nacional = when {
        digitos.startsWith("56") && digitos.length == 11 -> digitos.drop(2)
        else -> digitos
    }
    return nacional.length == 9 && nacional.startsWith("9")
}

// RUT chileno: solo se valida el formato (7 u 8 dígitos, guion y dígito verificador)
fun esRutValido(rut: String): Boolean = PATRON_RUT.matches(rut.trim())

