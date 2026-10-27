package cl.duoc.agricotech.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Solo definimos los roles que la app realmente usa; el resto toma el default de Material 3
private val EsquemaClaro = lightColorScheme(
    primary = AzulTecnomundo,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    outline = BordeCampo
)

@Composable
fun AgricotechTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaClaro, typography = Typography, content = content)
}