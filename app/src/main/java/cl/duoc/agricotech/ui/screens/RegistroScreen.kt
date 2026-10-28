package cl.duoc.agricotech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agricotech.domain.model.RolUsuario
import cl.duoc.agricotech.ui.theme.AgricotechTheme
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.ChipFondo
import cl.duoc.agricotech.ui.theme.Crema
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta

@Composable
fun RegistroScreen(
    alVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Datos del formulario; parten vacíos y el rol sin elegir
    var rolSeleccionado by remember { mutableStateOf<RolUsuario?>(null) }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Crema)
            .safeDrawingPadding()
            .imePadding()
    ) {
        BarraSuperior(alVolver = alVolver)

        Column(
            modifier = Modifier
                .weight(1f)                             // ocupa el espacio libre y deja el botón abajo
                .verticalScroll(rememberScrollState())   // evita que los campos se corten con el teclado
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            FilaRoles(seleccionado = rolSeleccionado, alSeleccionar = { rolSeleccionado = it })

            Spacer(modifier = Modifier.height(24.dp))

            CampoRegistro(
                valor = nombre,
                alCambiar = { nombre = it },
                etiqueta = "Nombre",
                capitalizacion = KeyboardCapitalization.Words
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                valor = apellido,
                alCambiar = { apellido = it },
                etiqueta = "Apellido",
                capitalizacion = KeyboardCapitalization.Words
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(valor = rut, alCambiar = { rut = it }, etiqueta = "RUT")

            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                valor = email,
                alCambiar = { email = it },
                etiqueta = "Email",
                tipoTeclado = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                valor = telefono,
                alCambiar = { telefono = it },
                etiqueta = "Teléfono",
                tipoTeclado = KeyboardType.Phone
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = { /* Paso 3: validar y guardar */ },
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
                Text(text = "Registrarse", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// Barra superior con la flecha para volver y el título de la pantalla
@Composable
private fun BarraSuperior(alVolver: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = alVolver) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver al login",
                tint = Tinta
            )
        }

        Text(
            text = "Crea tu cuenta",
            color = Tinta,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }

    HorizontalDivider(thickness = 1.dp, color = Arena)
}

// Los dos chips de rol: solo uno puede quedar seleccionado
@Composable
private fun FilaRoles(seleccionado: RolUsuario?, alSeleccionar: (RolUsuario) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        RolUsuario.entries.forEach { rol ->
            ChipRol(
                texto = rol.etiqueta,
                activo = rol == seleccionado,
                alTocar = { alSeleccionar(rol) }
            )
        }
    }
}

@Composable
private fun ChipRol(texto: String, activo: Boolean, alTocar: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (activo) AzulAccion else ChipFondo,
                shape = RoundedCornerShape(50)   // 50 % = pastilla completamente redondeada
            )
            .clickable(onClick = alTocar)
            .padding(horizontal = 16.dp, vertical = 11.dp)
    ) {
        Text(
            text = texto,
            color = if (activo) Blanco else TextoSecundario,
            fontSize = 12.5.sp,
            fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

// Campo reutilizable: cambia teclado y capitalización según el dato que recibe
@Composable
private fun CampoRegistro(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    capitalizacion: KeyboardCapitalization = KeyboardCapitalization.None
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = etiqueta, fontSize = 14.sp, color = TextoSecundario) },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            capitalization = capitalizacion
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AzulAccion,
            unfocusedBorderColor = Arena,
            focusedContainerColor = Blanco,
            unfocusedContainerColor = Blanco,
            cursorColor = AzulAccion
        )
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun RegistroScreenPreview() {
    AgricotechTheme {
        RegistroScreen(alVolver = {})
    }
}
