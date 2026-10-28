package cl.duoc.agricotech.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta
import cl.duoc.agricotech.ui.theme.Trigo

/**
 * Tarjeta de una solicitud en la lista del aplicador.
 * El color de cada chip depende del dato (producto, turno), no del gusto.
 */
@Composable
fun TarjetaSolicitud(
    solicitud: Solicitud,
    modifier: Modifier = Modifier,
    alTocar: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Blanco, shape = RoundedCornerShape(14.dp))
            .border(width = 1.dp, color = Arena, shape = RoundedCornerShape(14.dp))
            .clickable(onClick = alTocar)
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
