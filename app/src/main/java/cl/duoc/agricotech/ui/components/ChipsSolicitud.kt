package cl.duoc.agricotech.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agricotech.domain.model.TipoProducto
import cl.duoc.agricotech.domain.model.Turno
import cl.duoc.agricotech.ui.theme.AzulTexto
import cl.duoc.agricotech.ui.theme.Noche
import cl.duoc.agricotech.ui.theme.Tierra
import cl.duoc.agricotech.ui.theme.TrigoOscuro

// Chip pequeño con un punto de color: el color comunica el dato
@Composable
fun ChipDato(texto: String, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
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

fun colorDeProducto(tipo: TipoProducto): Color = when (tipo) {
    TipoProducto.LIQUIDO -> AzulTexto
    TipoProducto.SOLIDO -> Tierra
}

fun colorDeTurno(turno: Turno): Color = when (turno) {
    Turno.DIURNO -> TrigoOscuro
    Turno.NOCTURNO -> Noche
}
