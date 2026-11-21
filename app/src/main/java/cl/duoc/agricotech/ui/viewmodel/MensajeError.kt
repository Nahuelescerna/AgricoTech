package cl.duoc.agricotech.ui.viewmodel

import java.io.IOException

/**
 * Mensaje que ve el usuario cuando falla una llamada a la API.
 * Se centraliza para que las tres pantallas que escriben digan lo mismo.
 */
internal fun mensajeDeError(error: Throwable): String =
    if (error is IOException) {
        "Sin conexión. Revisa tu internet e intenta de nuevo."
    } else {
        "No pudimos completar la operación. Inténtalo otra vez."
    }
