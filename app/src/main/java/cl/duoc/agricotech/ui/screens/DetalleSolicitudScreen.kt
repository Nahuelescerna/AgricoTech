package cl.duoc.agricotech.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agricotech.domain.model.Cotizacion
import cl.duoc.agricotech.domain.model.RolUsuario
import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.ui.components.ChipDato
import cl.duoc.agricotech.ui.components.MiniaturaAerea
import cl.duoc.agricotech.ui.components.MiniaturaFoto
import cl.duoc.agricotech.ui.components.colorDeProducto
import cl.duoc.agricotech.ui.components.colorDeTurno
import cl.duoc.agricotech.ui.theme.AgricotechTheme
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.Crema
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta
import cl.duoc.agricotech.ui.theme.Trigo
import cl.duoc.agricotech.ui.viewmodel.DetalleSolicitudViewModel

@Composable
fun DetalleSolicitudScreen(
    id: Int,
    rol: RolUsuario,
    alVolver: () -> Unit,
    alCotizar: (Solicitud) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DetalleSolicitudViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Se pide la solicitud una sola vez cuando cambia el id
    LaunchedEffect(id) { viewModel.cargar(id) }

    // ON_RESUME se dispara al volver de cotizar: así se ve la propuesta enviada
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refrescar() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Crema)
            .safeDrawingPadding()
    ) {
        BarraDetalle(alVolver = alVolver)

        val solicitud = estado.solicitud

        when {
            estado.noEncontrada -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No encontramos esta solicitud.",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
            }

            solicitud == null -> Box(modifier = Modifier.fillMaxSize())

            else -> ContenidoDetalle(
                solicitud = solicitud,
                rol = rol,
                cotizacion = estado.cotizacion,
                alCotizar = alCotizar
            )
        }
    }
}

@Composable
private fun ColumnScope.ContenidoDetalle(
    solicitud: Solicitud,
    rol: RolUsuario,
    cotizacion: Cotizacion?,
    alCotizar: (Solicitud) -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        MiniaturaAerea(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = solicitud.parcela,
            color = Tinta,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${solicitud.comuna} · a ${solicitud.distanciaKm} km de ti",
            color = TextoSecundario,
            fontSize = 12.5.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChipDato(
                texto = solicitud.tipoProducto.etiqueta,
                color = colorDeProducto(solicitud.tipoProducto)
            )
            ChipDato(
                texto = solicitud.turno.etiqueta,
                color = colorDeTurno(solicitud.turno)
            )
            ChipDato(texto = "${solicitud.hectareas} ha", color = Tinta)
            ChipDato(
                texto = if (solicitud.aguaDisponible) "Con agua" else "Sin agua",
                color = if (solicitud.aguaDisponible) Trigo else TextoSecundario
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Detalle del servicio",
            color = Tinta,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Blanco, shape = RoundedCornerShape(14.dp))
                .border(width = 1.dp, color = Arena, shape = RoundedCornerShape(14.dp))
        ) {
            FilaDato(etiqueta = "Cultivo", valor = solicitud.cultivo)
            HorizontalDivider(thickness = 1.dp, color = Arena)
            FilaDato(etiqueta = "Superficie", valor = "${solicitud.hectareas} hectáreas")
            HorizontalDivider(thickness = 1.dp, color = Arena)
            FilaDato(
                etiqueta = "Agua en terreno",
                valor = if (solicitud.aguaDisponible) "Disponible" else "No disponible"
            )
            HorizontalDivider(thickness = 1.dp, color = Arena)
            FilaDato(etiqueta = "Obstáculos", valor = solicitud.obstaculos)
            HorizontalDivider(thickness = 1.dp, color = Arena)
            FilaDato(
                etiqueta = "Productor",
                valor = "${solicitud.calificacionProductor} estrellas · ${solicitud.trabajosPublicados} solicitudes"
            )
        }

        if (solicitud.fotos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Fotos del terreno",
                color = Tinta,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                solicitud.fotos.forEach { ruta ->
                    MiniaturaFoto(
                        uri = Uri.parse(ruta),
                        modifier = Modifier.size(72.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Postulaciones",
            color = Tinta,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (rol == RolUsuario.APLICADOR && cotizacion != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Blanco, shape = RoundedCornerShape(14.dp))
                    .border(width = 1.dp, color = Arena, shape = RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Tu propuesta",
                    color = TextoSecundario,
                    fontSize = 11.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "UF ${"%.1f".format(cotizacion.total)} · ${"%.1f".format(cotizacion.tarifaPorHectarea)} UF/ha",
                    color = Tinta,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (cotizacion.observacion.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = cotizacion.observacion,
                        color = TextoSecundario,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        } else {
            Text(
                text = if (rol == RolUsuario.APLICADOR) {
                    "Todavía no has enviado una cotización para esta solicitud."
                } else {
                    "Aún no hay aplicadores postulando. Cuando lleguen, podrás compararlos aquí."
                },
                color = TextoSecundario,
                fontSize = 12.5.sp,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (rol == RolUsuario.APLICADOR) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = { alCotizar(solicitud) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(10.dp),
                        ambientColor = AzulAccion,
                        spotColor = AzulAccion
                    ),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulAccion,
                    contentColor = Blanco
                )
            ) {
                Text(
                    text = if (cotizacion == null) "Enviar cotización" else "Actualizar cotización",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BarraDetalle(alVolver: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = alVolver) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Tinta
            )
        }

        Text(
            text = "Solicitud",
            color = Tinta,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }

    HorizontalDivider(thickness = 1.dp, color = Arena)
}

// Fila etiqueta / valor de la ficha técnica
@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = etiqueta,
            color = TextoSecundario,
            fontSize = 12.sp,
            modifier = Modifier.width(110.dp)
        )

        Text(
            text = valor,
            color = Tinta,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun DetalleSolicitudPreview() {
    AgricotechTheme {
        DetalleSolicitudScreen(id = 1, rol = RolUsuario.APLICADOR, alVolver = {})
    }
}
