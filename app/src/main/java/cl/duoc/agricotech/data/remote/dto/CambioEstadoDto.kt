package cl.duoc.agricotech.data.remote.dto

import kotlinx.serialization.Serializable

/** Cuerpo del `PATCH /cotizacion/{id}`: solo cambia el estado de la propuesta. */
@Serializable
data class CambioEstadoDto(val estado: String)
