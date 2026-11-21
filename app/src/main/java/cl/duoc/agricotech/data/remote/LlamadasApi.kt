package cl.duoc.agricotech.data.remote

import kotlinx.coroutines.CancellationException

/**
 * Envuelve una llamada a la API para no repetir try/catch en cada repositorio.
 *
 * Devuelve [Result] para que la capa de UI decida qué mostrar ante un error,
 * pero deja pasar [CancellationException]: cancelar una corrutina no es un fallo.
 */
internal suspend fun <T> ejecutarLlamada(bloque: suspend () -> T): Result<T> =
    try {
        Result.success(bloque())
    } catch (cancelacion: CancellationException) {
        throw cancelacion
    } catch (error: Exception) {
        Result.failure(error)
    }
