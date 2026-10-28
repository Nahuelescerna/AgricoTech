package cl.duoc.agricotech.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Roles de Material 3 mapeados a la paleta de Tecnomundo Connect
private val EsquemaClaro = lightColorScheme(
    primary = AzulAccion,
    onPrimary = Blanco,
    secondary = VerdeCampo,
    onSecondary = Blanco,
    tertiary = Tierra,
    onTertiary = Blanco,
    background = Crema,
    onBackground = Tinta,
    surface = Blanco,
    onSurface = Tinta,
    surfaceVariant = ChipFondo,
    onSurfaceVariant = TextoSecundario,
    outline = Arena,
    error = ErrorRojo
)

@Composable
fun AgricotechTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaClaro, typography = Typography, content = content)
}
