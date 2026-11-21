package cl.duoc.agricotech.data.remote

import cl.duoc.agricotech.data.remote.dto.CalificacionDto
import cl.duoc.agricotech.data.remote.dto.CotizacionDto
import cl.duoc.agricotech.data.remote.dto.SolicitudDto
import cl.duoc.agricotech.domain.model.Calificacion
import cl.duoc.agricotech.domain.model.Cotizacion
import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.domain.model.TipoProducto
import cl.duoc.agricotech.domain.model.Turno

/**
 * Conversión de DTO (JSON de Xano) a modelo de dominio.
 *
 * La UI nunca conoce los DTO: así un cambio en la API solo se ajusta acá.
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
    observacion = mensaje
)

fun CalificacionDto.aDominio(): Calificacion = Calificacion(
    idSolicitud = solicitudId,
    aplicador = aplicadorCorreo,
    aplicadorCorreo = aplicadorCorreo,
    estrellas = estrellas,
    comentario = comentario
)
