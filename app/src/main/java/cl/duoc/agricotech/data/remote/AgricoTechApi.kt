package cl.duoc.agricotech.data.remote

import cl.duoc.agricotech.data.remote.dto.CalificacionDto
import cl.duoc.agricotech.data.remote.dto.CambioEstadoDto
import cl.duoc.agricotech.data.remote.dto.CotizacionDto
import cl.duoc.agricotech.data.remote.dto.NuevaCalificacionDto
import cl.duoc.agricotech.data.remote.dto.NuevaCotizacionDto
import cl.duoc.agricotech.data.remote.dto.NuevaSolicitudDto
import cl.duoc.agricotech.data.remote.dto.SolicitudDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Endpoints del API group `agricotech` en Xano.
 *
 * Las rutas van en singular porque así se crearon en el servidor:
 * `/solicitud`, `/cotizacion`, `/calificacion`.
 */
interface AgricoTechApi {

    /** Lista las solicitudes publicadas. */
    @GET("solicitud")
    suspend fun listarSolicitudes(): List<SolicitudDto>

    /** Detalle de una solicitud por su id. */
    @GET("solicitud/{id}")
    suspend fun obtenerSolicitud(@Path("id") id: Int): SolicitudDto

    /** Lista todas las cotizaciones; el filtro por solicitud se hace en memoria. */
    @GET("cotizacion")
    suspend fun listarCotizaciones(): List<CotizacionDto>

    /** Lista todas las calificaciones; el promedio se agrupa por correo. */
    @GET("calificacion")
    suspend fun listarCalificaciones(): List<CalificacionDto>

    /** Publica una solicitud nueva (POST). */
    @POST("solicitud")
    suspend fun crearSolicitud(@Body cuerpo: NuevaSolicitudDto): SolicitudDto

    /** Envía una cotización nueva (POST). */
    @POST("cotizacion")
    suspend fun crearCotizacion(@Body cuerpo: NuevaCotizacionDto): CotizacionDto

    /** Registra una calificación nueva (POST). */
    @POST("calificacion")
    suspend fun crearCalificacion(@Body cuerpo: NuevaCalificacionDto): CalificacionDto

    /** Acepta o rechaza una propuesta (PATCH): solo cambia el estado. */
    @PATCH("cotizacion/{id}")
    suspend fun actualizarEstado(
        @Path("id") id: Int,
        @Body cuerpo: CambioEstadoDto
    ): CotizacionDto
}
