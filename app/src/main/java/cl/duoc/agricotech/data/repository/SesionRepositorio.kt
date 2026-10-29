package cl.duoc.agricotech.data.repository

import cl.duoc.agricotech.domain.model.RolUsuario

/**
 * Sesión del usuario dentro de la app.
 * Guarda a quién corresponde la sesión para que el perfil pueda
 * leer sus datos desde Room.
 */
object SesionRepositorio {

    private var _usuarioId: Long = 0
    private var _usuarioEmail: String = ""
    private var _nombreCompleto: String = ""
    private var _rolActual: RolUsuario = RolUsuario.APLICADOR

    val usuarioId: Long get() = _usuarioId
    val usuarioEmail: String get() = _usuarioEmail
    val nombreCompleto: String get() = _nombreCompleto
    val rolActual: RolUsuario get() = _rolActual

    fun iniciarSesion(id: Long, email: String, nombreCompleto: String, rol: RolUsuario) {
        _usuarioId = id
        _usuarioEmail = email
        _nombreCompleto = nombreCompleto
        _rolActual = rol
    }

    fun cerrarSesion() {
        _usuarioId = 0
        _usuarioEmail = ""
        _nombreCompleto = ""
        _rolActual = RolUsuario.APLICADOR
    }
}
