package cl.duoc.agricotech.data.repository

import cl.duoc.agricotech.domain.model.RolUsuario

/**
 * Sesión del usuario dentro de la app.
 * Vive en memoria: recuerda el rol con el que se registró para que el login
 * abra el home correcto. Con Room, el rol saldrá del usuario guardado.
 */
object SesionRepositorio {

    private var _rolActual: RolUsuario = RolUsuario.APLICADOR

    val rolActual: RolUsuario
        get() = _rolActual

    fun iniciarSesion(rol: RolUsuario) {
        _rolActual = rol
    }
}
