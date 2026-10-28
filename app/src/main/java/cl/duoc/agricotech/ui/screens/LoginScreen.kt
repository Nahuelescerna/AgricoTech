package cl.duoc.agricotech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agricotech.ui.components.ModalRecuperarContrasena
import cl.duoc.agricotech.ui.components.TecnomundoLogo
import cl.duoc.agricotech.ui.theme.AgricotechTheme
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.AzulTexto
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.Crema
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta
import cl.duoc.agricotech.ui.theme.VerdeCampo

@Composable
fun LoginScreen(
    alContinuar: () -> Unit = {},
    alCrearCuenta: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Datos que el usuario escribe; remember los guarda entre recomposiciones
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var aceptaTerminos by remember { mutableStateOf(false) }
    var mostrarRecuperacion by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Crema)
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState()) // permite desplazar en pantallas pequeñas
            .padding(horizontal = 24.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TecnomundoLogo()

        Spacer(modifier = Modifier.height(44.dp))

        Text(
            text = "Iniciar sesión",
            color = Tinta,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa con tu correo y contraseña",
            color = TextoSecundario,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        CampoCorreo(correo = correo, alCambiar = { correo = it })

        Spacer(modifier = Modifier.height(12.dp))

        CampoContrasena(contrasena = contrasena, alCambiar = { contrasena = it })

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = alContinuar,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(10.dp),
                    ambientColor = AzulAccion,
                    spotColor = AzulAccion
                ),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulAccion,
                contentColor = Blanco
            )
        ) {
            Text(text = "Iniciar sesión", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        FilaTerminos(acepta = aceptaTerminos, alCambiar = { aceptaTerminos = it })

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            modifier = Modifier.clickable { mostrarRecuperacion = true },
            color = AzulTexto,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "¿No tienes cuenta?",
                color = TextoSecundario,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Crear una",
                modifier = Modifier.clickable(onClick = alCrearCuenta),
                color = AzulTexto,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    // El modal solo existe en la composición mientras está visible
    if (mostrarRecuperacion) {
        ModalRecuperarContrasena(alCerrar = { mostrarRecuperacion = false })
    }
}

// Campo de correo: teclado de email y una sola línea
@Composable
private fun CampoCorreo(correo: String, alCambiar: (String) -> Unit) {
    OutlinedTextField(
        value = correo,
        onValueChange = alCambiar,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "correo electrónico@dominio.com", fontSize = 14.sp, color = TextoSecundario) },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        colors = coloresDeCampo()
    )
}

// Campo de contraseña: oculta lo que se escribe
@Composable
private fun CampoContrasena(contrasena: String, alCambiar: (String) -> Unit) {
    OutlinedTextField(
        value = contrasena,
        onValueChange = alCambiar,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "contraseña", fontSize = 14.sp, color = TextoSecundario) },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = coloresDeCampo()
    )
}

// Colores comunes a los dos campos, para no repetirlos
@Composable
private fun coloresDeCampo() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AzulAccion,
    unfocusedBorderColor = Arena,
    focusedContainerColor = Blanco,
    unfocusedContainerColor = Blanco,
    cursorColor = AzulAccion
)

// Checkbox + texto legal con las dos partes en negrita
@Composable
private fun FilaTerminos(acepta: Boolean, alCambiar: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = acepta,
            onCheckedChange = alCambiar,
            colors = CheckboxDefaults.colors(
                checkedColor = VerdeCampo,
                uncheckedColor = Arena
            )
        )

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = TextoSecundario)) {
                    append("Acepto los ")
                }
                withStyle(SpanStyle(color = Tinta, fontWeight = FontWeight.SemiBold)) {
                    append("Términos de servicio")
                }
                withStyle(SpanStyle(color = TextoSecundario)) {
                    append(" y la ")
                }
                withStyle(SpanStyle(color = Tinta, fontWeight = FontWeight.SemiBold)) {
                    append("Política de privacidad")
                }
            },
            fontSize = 11.5.sp,
            lineHeight = 16.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun LoginScreenPreview() {
    AgricotechTheme {
        LoginScreen()
    }
}
