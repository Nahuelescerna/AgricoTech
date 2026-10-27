package cl.duoc.agricotech.domain.model

/**
 * Perfil con el que se registra el usuario.
 * [etiqueta] es el texto que se muestra en los chips de la pantalla de registro.
 */
enum class RolUsuario(val etiqueta: String) {
    PRODUCTOR("Soy un productor"),
    APLICADOR("Soy un aplicador")
}
