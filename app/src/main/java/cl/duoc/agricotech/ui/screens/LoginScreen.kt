package cl.duoc.agricotech.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
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
import cl.duoc.agricotech.ui.components.TecnomundoLogo
import cl.duoc.agricotech.ui.theme.AgricotechTheme
import cl.duoc.agricotech.ui.theme.AzulTecnomundo
import cl.duoc.agricotech.ui.theme.BordeCampo
import cl.duoc.agricotech.ui.theme.LineaDivisor
import cl.duoc.agricotech.ui.theme.NegroBoton
import cl.duoc.agricotech.ui.theme.TextoPrincipal
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.VioletaTerminos

@Composable
fun LoginScreen(
    alContinuar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Estado del formulario: remember guarda el valor entre recomposiciones
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var aceptaTerminos by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()     // evita que el contenido quede bajo la barra de estado
            .imePadding()             // sube todo cuando aparece el teclado
            .verticalScroll(rememberScrollState()) // permite desplazar en pantallas pequeñas
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TecnomundoLogo()

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Crea una cuenta",
            color = TextoPrincipal,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa tu correo electrónico\npara registrarte en esta aplicación",
            color = TextoSecundario,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        CampoCorreo(correo = correo, alCambiar = { correo = it })

        Spacer(modifier = Modifier.height(12.dp))

        CampoContrasena(contrasena = contrasena, alCambiar = { contrasena = it })

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = alContinuar,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NegroBoton,
                contentColor = Color.White
            )
        ) {
            Text(text = "Continuar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DivisorConCirculo()

        Spacer(modifier = Modifier.height(16.dp))

        FilaTerminos(acepta = aceptaTerminos, alCambiar = { aceptaTerminos = it })
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
        shape = RoundedCornerShape(8.dp),
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
        shape = RoundedCornerShape(8.dp),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = coloresDeCampo()
    )
}

// Colores comunes a los dos campos, para no repetirlos
@Composable
private fun coloresDeCampo() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AzulTecnomundo,
    unfocusedBorderColor = BordeCampo,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)

// Línea gris con un círculo hueco al medio, como en el mock
@Composable
private fun DivisorConCirculo() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = LineaDivisor)

        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(8.dp)
                .border(width = 1.dp, color = BordeCampo, shape = CircleShape)
        )

        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = LineaDivisor)
    }
}

// Checkbox + texto legal con las dos partes en negrita
@Composable
private fun FilaTerminos(acepta: Boolean, alCambiar: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = acepta,
            onCheckedChange = alCambiar,
            colors = CheckboxDefaults.colors(
                checkedColor = VioletaTerminos,
                uncheckedColor = BordeCampo
            )
        )

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = TextoSecundario)) {
                    append("Al hacer clic en continuar, aceptas nuestros ")
                }
                withStyle(SpanStyle(color = TextoPrincipal, fontWeight = FontWeight.SemiBold)) {
                    append("Términos de servicio")
                }
                withStyle(SpanStyle(color = TextoSecundario)) {
                    append(" y ")
                }
                withStyle(SpanStyle(color = TextoPrincipal, fontWeight = FontWeight.SemiBold)) {
                    append("Política de privacidad")
                }
            },
            fontSize = 12.sp,
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