package cl.duoc.agricotech.data.remote

import cl.duoc.agricotech.data.remote.dto.CalificacionDto
import cl.duoc.agricotech.data.remote.dto.CotizacionDto
import cl.duoc.agricotech.data.remote.dto.NuevaCalificacionDto
import cl.duoc.agricotech.data.remote.dto.NuevaCotizacionDto
import cl.duoc.agricotech.data.remote.dto.NuevaSolicitudDto
import cl.duoc.agricotech.data.remote.dto.SolicitudDto
import cl.duoc.agricotech.domain.model.Calificacion
import cl.duoc.agricotech.domain.model.Cotizacion
import cl.duoc.agricotech.domain.model.NuevaCotizacion
import cl.duoc.agricotech.domain.model.NuevaSolicitud
import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.domain.model.TipoProducto
import cl.duoc.agricotech.domain.model.Turno

/**
 * Conversión entre el JSON de Xano y los modelos de dominio.
 *
 * La UI nunca conoce los DTO: así un cambio en la API se ajusta solo acá.
 */

fun SolicitudDto.aDominio(fotos: List<String> = emptyList()): Solicitud = Solicitud(
    id = id,
    parcela = parcela,
    cultivo = cultivo,
    comuna = comuna,
    distanciaKm = distanciaKm,
    hectareas = hectareas,
    tipoProducto = TipoProducto.desde(tipoProducto),
    turno = Turno.desde(turno),
    aguaDisponible = aguaDisponible,
    obstaculos = obstaculos,
    fotos = fotos,
    calificacionProductor = calificacionProductor,
    trabajosPublicados = trabajosPublicados
)

fun CotizacionDto.aDominio(): Cotizacion = Cotizacion(
    id = id,
    idSolicitud = solicitudId,
    aplicador = aplicadorNombre.ifBlank { aplicadorCorreo },
    aplicadorCorreo = aplicadorCorreo,
    distanciaKm = distanciaKm,
    calificacion = calificacion,
    especialidad = especialidad,
    tarifaPorHectarea = tarifaUfHa,
    total = totalUf,
    observacion = mensaje,
    estado = estado
)

fun CalificacionDto.aDominio(): Calificacion = Calificacion(
    idSolicitud = solicitudId,
    aplicador = aplicadorCorreo,
    aplicadorCorreo = aplicadorCorreo,
    estrellas = estrellas,
    comentario = comentario
)

// Envío (POST): de dominio a JSON

fun NuevaSolicitud.aDto(): NuevaSolicitudDto = NuevaSolicitudDto(
    productorCorreo = productorCorreo,
    parcela = parcela,
    cultivo = cultivo,
    comuna = comuna,
    latitud = latitud,
    longitud = longitud,
    distanciaKm = distanciaKm,
    hectareas = hectareas,
    tipoProducto = tipoProducto.name,
    turno = turno.name,
    aguaDisponible = aguaDisponible,
    obstaculos = obstaculos
)

fun NuevaCotizacion.aDto(): NuevaCotizacionDto = NuevaCotizacionDto(
    solicitudId = idSolicitud,
    aplicadorCorreo = aplicadorCorreo,
    aplicadorNombre = aplicadorNombre,
    tarifaUfHa = tarifaPorHectarea,
    totalUf = total,
    mensaje = observacion,
    distanciaKm = distanciaKm,
    calificacion = calificacion,
    especialidad = especialidad
)

fun Calificacion.aDto(): NuevaCalificacionDto = NuevaCalificacionDto(
    solicitudId = idSolicitud,
    aplicadorCorreo = aplicadorCorreo,
    estrellas = estrellas,
    comentario = comentario
)
