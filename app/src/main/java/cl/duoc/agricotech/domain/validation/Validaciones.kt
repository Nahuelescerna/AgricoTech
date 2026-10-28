package cl.duoc.agricotech.domain.validation

/**
 * Validaciones de datos del dominio.
 * Viven fuera de la UI para reutilizarlas en cualquier pantalla
 * y poder probarlas sin levantar la interfaz.
 */

private val PATRON_EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val PATRON_RUT = Regex("^\\d{7,8}-[0-9K]$")

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

/**
 * RUT chileno con dígito verificador (módulo 11).
 * Acepta 12345678-9 y 12.345.678-9; la K puede venir en minúscula.
 */
fun esRutValido(rut: String): Boolean {
    val limpio = rut.replace(".", "").replace(" ", "").uppercase()
    if (!PATRON_RUT.matches(limpio)) return false

    val cuerpo = limpio.substringBefore("-")
    val digitoVerificador = limpio.substringAfter("-")

    // Se multiplica de derecha a izquierda por 2,3,4,5,6,7 y se cicla
    var suma = 0
    var factor = 2
    for (indice in cuerpo.indices.reversed()) {
        suma += cuerpo[indice].digitToInt() * factor
        factor = if (factor == 7) 2 else factor + 1
    }

    val resto = 11 - (suma % 11)
    val esperado = when (resto) {
        11 -> "0"
        10 -> "K"
        else -> resto.toString()
    }

    return esperado == digitoVerificador
}

// Da formato 12.345.678-9 mientras el usuario escribe
fun formatearRut(entrada: String): String {
    val limpio = entrada.uppercase().filter { it.isDigit() || it == 'K' }
    if (limpio.isEmpty()) return ""

    val cuerpo = limpio.take(8)
    val digitoVerificador = limpio.drop(8).take(1)
    val conPuntos = cuerpo.reversed().chunked(3).joinToString(".").reversed()

    return if (digitoVerificador.isEmpty()) conPuntos else "$conPuntos-$digitoVerificador"
}
