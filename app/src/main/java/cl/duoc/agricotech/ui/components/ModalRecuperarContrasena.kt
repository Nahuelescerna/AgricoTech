package cl.duoc.agricotech.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agricotech.domain.validation.esEmailValido
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.ErrorRojo
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta

/**
 * Modal para pedir el correo cuando el usuario olvidó su contraseña.
 * Tiene dos pasos: pedir el correo y confirmar el envío.
 */
@Composable
fun ModalRecuperarContrasena(alCerrar: () -> Unit) {
    var correo by remember { mutableStateOf("") }
    var correoInvalido by remember { mutableStateOf(false) }
    var enviado by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = alCerrar,
        containerColor = Blanco,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text(
                text = if (enviado) "Revisa tu correo" else "Recuperar contraseña",
                color = Tinta,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (enviado) {
                Text(
                    text = "Si el correo está registrado, te enviamos un enlace para restablecer tu contraseña.",
                    color = TextoSecundario,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                )
            } else {
                Column {
                    Text(
                        text = "Escribe el correo con el que te registraste y te enviaremos las instrucciones.",
                        color = TextoSecundario,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                            correoInvalido = false   // el error desaparece al volver a escribir
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "correo electrónico@dominio.com", fontSize = 13.sp, color = TextoSecundario) },
                        singleLine = true,
                        isError = correoInvalido,
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulAccion,
                            unfocusedBorderColor = Arena,
                            focusedContainerColor = Blanco,
                            unfocusedContainerColor = Blanco,
                            cursorColor = AzulAccion,
                            errorBorderColor = ErrorRojo
                        )
                    )

                    if (correoInvalido) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Escribe un correo válido.",
                            color = ErrorRojo,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (enviado) {
                        alCerrar()
                    } else if (esEmailValido(correo)) {
                        enviado = true
                    } else {
                        correoInvalido = true
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulAccion,
                    contentColor = Blanco
                )
            ) {
                Text(text = if (enviado) "Listo" else "Enviar", fontSize = 13.5.sp)
            }
        },
        dismissButton = {
            if (!enviado) {
                TextButton(onClick = alCerrar) {
                    Text(text = "Cancelar", color = TextoSecundario, fontSize = 13.5.sp)
                }
            }
        }
    )
}
