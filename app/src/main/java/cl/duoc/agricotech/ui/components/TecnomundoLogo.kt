package cl.duoc.agricotech.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agricotech.R
import cl.duoc.agricotech.ui.theme.Tinta

@Composable
fun TecnomundoLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        // Isotipo oficial de Tecnomundo (PNG con transparencia en drawable-nodpi)
        Image(
            painter = painterResource(id = R.drawable.logo_tecnomundo),
            contentDescription = "Isotipo de Tecnomundo",
            modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "TECMUNDO",
            color = Tinta,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    }
}
