package cl.duoc.agricotech.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Miniatura que simula una foto aérea del campo (surcos vistos desde arriba).
 * Cuando existan fotos reales de cada solicitud, se reemplaza por Image(...).
 */
@Composable
fun MiniaturaAerea(modifier: Modifier = Modifier) {
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
