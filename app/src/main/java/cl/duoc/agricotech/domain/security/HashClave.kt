package cl.duoc.agricotech.domain.security

import java.security.MessageDigest

/**
 * Huella de la contraseña con SHA-256.
 * Nunca se guarda la contraseña en texto plano en la base de datos.
 */
fun hashClave(clave: String): String =
    MessageDigest.getInstance("SHA-256")
        .digest(clave.toByteArray())
        .joinToString(separator = "") { byte -> "%02x".format(byte) }
