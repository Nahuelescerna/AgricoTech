package cl.duoc.agricotech.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agricotech.ui.theme.AzulTecnomundo
import cl.duoc.agricotech.ui.theme.TextoPrincipal

@Composable
fun TecnomundoLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        // Isotipo provisorio: círculo azul con la inicial.
        // Con el logo oficial se reemplaza este Box por Image(painterResource(R.drawable.logo_tecnomundo)).
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color = AzulTecnomundo, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "M", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "TECMUNDO",
            color = TextoPrincipal,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    }
}