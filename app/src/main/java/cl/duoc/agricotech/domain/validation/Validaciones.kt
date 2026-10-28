package cl.duoc.agricotech.domain.validation

/**
 * Validaciones de datos del dominio.
 * Viven fuera de la UI para poder reutilizarlas en cualquier pantalla
 * y probarlas sin levantar la interfaz.
 */
private val PATRON_EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

fun esEmailValido(correo: String): Boolean = PATRON_EMAIL.matches(correo.trim())
