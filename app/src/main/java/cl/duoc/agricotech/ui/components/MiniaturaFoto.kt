package cl.duoc.agricotech.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import cl.duoc.agricotech.ui.theme.ChipFondo

/**
 * Miniatura de una foto tomada con la cámara.
 * La imagen se decodifica fuera del hilo principal para no trabar la UI.
 */
@Composable
fun MiniaturaFoto(uri: Uri, modifier: Modifier = Modifier) {
    val contexto = LocalContext.current

    val imagen by produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                contexto.contentResolver.openInputStream(uri)?.use { entrada ->
                    BitmapFactory.decodeStream(entrada)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color = ChipFondo)
    ) {
        imagen?.let { mapa ->
            Image(
                bitmap = mapa,
                contentDescription = "Foto del obstáculo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
