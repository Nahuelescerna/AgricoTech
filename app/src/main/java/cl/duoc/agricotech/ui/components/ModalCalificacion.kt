package cl.duoc.agricotech.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta
import cl.duoc.agricotech.ui.theme.Trigo

/**
 * Modal para calificar al aplicador de 1 a 5 estrellas (RF-05).
 * El botón "Enviar" se habilita recién cuando se elige al menos una estrella.
 */
@Composable
fun ModalCalificacion(
    nombreAplicador: String,
    alCancelar: () -> Unit,
    alEnviar: (estrellas: Int, comentario: String) -> Unit
) {
    var estrellas by remember { mutableStateOf(0) }
    var comentario by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = alCancelar,
        containerColor = Blanco,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text(
                text = "Calificar servicio",
                color = Tinta,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "¿Cómo fue el trabajo de $nombreAplicador?",
                    color = TextoSecundario,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cinco estrellas seleccionables
                Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()) {
                    for (indice in 1..5) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(36.dp)
                                .clickable { estrellas = indice },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "$indice estrellas",
                                tint = if (indice <= estrellas) Trigo else Arena,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (estrellas) {
                        0 -> "Toca una estrella para calificar"
                        1 -> "Muy malo"
                        2 -> "Malo"
                        3 -> "Regular"
                        4 -> "Bueno"
                        else -> "Excelente"
                    },
                    color = if (estrellas == 0) TextoSecundario else Tinta,
                    fontSize = 11.5.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp),
                    placeholder = {
                        Text(
                            text = "Comentario (opcional)",
                            color = TextoSecundario,
                            fontSize = 13.sp
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulAccion,
                        unfocusedBorderColor = Arena,
                        focusedContainerColor = Blanco,
                        unfocusedContainerColor = Blanco,
                        cursorColor = AzulAccion
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { alEnviar(estrellas, comentario.trim()) },
                enabled = estrellas > 0,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulAccion,
                    contentColor = Blanco
                )
            ) {
                Text(text = "Enviar", fontSize = 13.5.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) {
                Text(text = "Cancelar", color = TextoSecundario, fontSize = 13.5.sp)
            }
        }
    )
}
