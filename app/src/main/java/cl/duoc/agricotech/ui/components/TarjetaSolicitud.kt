package cl.duoc.agricotech.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.domain.model.TipoProducto
import cl.duoc.agricotech.domain.model.Turno
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulTexto
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.Noche
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tierra
import cl.duoc.agricotech.ui.theme.Tinta
import cl.duoc.agricotech.ui.theme.Trigo
import cl.duoc.agricotech.ui.theme.TrigoOscuro

/**
 * Tarjeta de una solicitud en la lista del aplicador.
 * El color de cada chip depende del dato (producto, turno), no del gusto.
 */
@Composable
fun TarjetaSolicitud(solicitud: Solicitud, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Blanco, shape = RoundedCornerShape(14.dp))
            .border(width = 1.dp, color = Arena, shape = RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MiniaturaAerea(modifier = Modifier.size(64.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = solicitud.parcela,
                color = Tinta,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${solicitud.comuna} · a ${solicitud.distanciaKm} km",
                color = TextoSecundario,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                ChipDato(
                    texto = solicitud.tipoProducto.etiqueta,
                    color = colorDeProducto(solicitud.tipoProducto)
                )
                ChipDato(
                    texto = solicitud.turno.etiqueta,
                    color = colorDeTurno(solicitud.turno)
                )
                ChipDato(texto = "${solicitud.hectareas} ha", color = Tinta)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Trigo,
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${solicitud.calificacionProductor} · ${solicitud.trabajosPublicados} trabajos",
                    color = TextoSecundario,
                    fontSize = 10.5.sp
                )
            }
        }
    }
}

// Chip pequeño con un punto de color: el color comunica el dato
@Composable
private fun ChipDato(texto: String, color: Color) {
    Row(
        modifier = Modifier
            .background(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .background(color = color, shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = texto,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Miniatura que simula una foto aérea del campo (surcos vistos desde arriba).
 * Cuando existan fotos reales de las solicitudes, se reemplaza por Image(...).
 */
@Composable
private fun MiniaturaAerea(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.clip(RoundedCornerShape(12.dp))) {
        drawRect(
            brush = Brush.linearGradient(
                listOf(Color(0xFF6E8558), Color(0xFF8C9A67), Color(0xFFA79C6E))
            )
        )

        // Surcos diagonales
        val separacion = size.width / 5f
        for (indice in -3..8) {
            val inicioX = indice * separacion
            drawLine(
                color = Color(0x552F4A2E),
                start = Offset(inicioX, 0f),
                end = Offset(inicioX + size.height, size.height),
                strokeWidth = 3.dp.toPx()
            )
        }
    }
}

private fun colorDeProducto(tipo: TipoProducto): Color = when (tipo) {
    TipoProducto.LIQUIDO -> AzulTexto
    TipoProducto.SOLIDO -> Tierra
}

private fun colorDeTurno(turno: Turno): Color = when (turno) {
    Turno.DIURNO -> TrigoOscuro
    Turno.NOCTURNO -> Noche
}
