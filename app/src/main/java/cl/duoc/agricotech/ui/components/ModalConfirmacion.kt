package cl.duoc.agricotech.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta

/**
 * Modal simple para confirmar una acción terminada
 * (cuenta creada, sesión iniciada). Un solo botón para cerrar.
 */
@Composable
fun ModalConfirmacion(
    titulo: String,
    mensaje: String,
    textoBoton: String,
    alCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCerrar,
        containerColor = Blanco,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text(
                text = titulo,
                color = Tinta,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = mensaje,
                color = TextoSecundario,
                fontSize = 13.5.sp,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = alCerrar,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulAccion,
                    contentColor = Blanco
                )
            ) {
                Text(text = textoBoton, fontSize = 13.5.sp)
            }
        }
    )
}
