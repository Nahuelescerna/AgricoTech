package cl.duoc.agricotech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agricotech.domain.model.RolUsuario
import cl.duoc.agricotech.ui.components.ModalConfirmacion
import cl.duoc.agricotech.ui.theme.AgricotechTheme
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.AzulTexto
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.ChipFondo
import cl.duoc.agricotech.ui.theme.Crema
import cl.duoc.agricotech.ui.theme.ErrorRojo
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta
import cl.duoc.agricotech.ui.theme.VerdeCampo
import cl.duoc.agricotech.ui.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    alVolver: () -> Unit,
    alEntrar: (RolUsuario) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: RegistroViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

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
            FilaRoles(
                seleccionado = estado.rol,
                alSeleccionar = viewModel::alSeleccionarRol
            )

            if (estado.errorRol != null) {
                Spacer(modifier = Modifier.height(8.dp))
                MensajeError(texto = estado.errorRol!!)
            }

            Spacer(modifier = Modifier.height(24.dp))

            CampoRegistro(
                valor = estado.nombre,
                error = estado.errorNombre,
                alCambiar = viewModel::alCambiarNombre,
                etiqueta = "Nombre",
                capitalizacion = KeyboardCapitalization.Words
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                valor = estado.apellido,
                error = estado.errorApellido,
                alCambiar = viewModel::alCambiarApellido,
                etiqueta = "Apellido",
                capitalizacion = KeyboardCapitalization.Words
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                valor = estado.rut,
                error = estado.errorRut,
                alCambiar = viewModel::alCambiarRut,
                etiqueta = "RUT"
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                valor = estado.email,
                error = estado.errorEmail,
                alCambiar = viewModel::alCambiarEmail,
                etiqueta = "Email",
                tipoTeclado = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                valor = estado.telefono,
                error = estado.errorTelefono,
                alCambiar = viewModel::alCambiarTelefono,
                etiqueta = "Teléfono",
                tipoTeclado = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoRegistro(
                valor = estado.clave,
                error = estado.errorClave,
                alCambiar = viewModel::alCambiarClave,
                etiqueta = "Contraseña",
                esPassword = true
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            FilaTerminos(
                acepta = estado.aceptaTerminos,
                alCambiar = viewModel::alCambiarTerminos
            )

            if (estado.errorTerminos != null) {
                Spacer(modifier = Modifier.height(6.dp))
                MensajeError(texto = estado.errorTerminos!!)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = viewModel::registrar,
                enabled = !estado.guardando,
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
                Text(
                    text = if (estado.guardando) "Guardando…" else "Registrarse",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (estado.cuentaCreada) {
        ModalConfirmacion(
            titulo = "Cuenta creada",
            mensaje = "Tu cuenta quedó registrada. Ya puedes iniciar sesión con tu correo.",
            textoBoton = "Entendido",
            alCerrar = {
                viewModel.cerrarConfirmacion()
                estado.rol?.let(alEntrar)
            }
        )
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

// Dos opciones grandes con forma de círculo, como tarjetas de selección
@Composable
private fun FilaRoles(seleccionado: RolUsuario?, alSeleccionar: (RolUsuario) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RolUsuario.entries.forEach { rol ->
            OpcionRol(
                texto = rol.etiqueta,
                activo = rol == seleccionado,
                alTocar = { alSeleccionar(rol) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OpcionRol(
    texto: String,
    activo: Boolean,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = alTocar)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Círculo de la opción. Cuando tengamos las fotos, se reemplaza este Box por:
        // Image(painterResource(R.drawable.foto_productor), contentScale = ContentScale.Crop,
        //       modifier = Modifier.size(104.dp).clip(CircleShape))
        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(CircleShape)
                .background(if (activo) AzulAccion.copy(alpha = 0.14f) else ChipFondo)
                .border(
                    width = if (activo) 2.dp else 1.dp,
                    color = if (activo) AzulAccion else Arena,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = texto,
            color = if (activo) AzulTexto else TextoSecundario,
            fontSize = 13.sp,
            fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

// Campo reutilizable: cambia teclado, capitalización y mensaje de error según el dato
@Composable
private fun CampoRegistro(
    valor: String,
    error: String?,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    capitalizacion: KeyboardCapitalization = KeyboardCapitalization.None,
    esPassword: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = etiqueta, fontSize = 14.sp, color = TextoSecundario) },
        singleLine = true,
        visualTransformation = if (esPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (esPassword) KeyboardType.Password else tipoTeclado,
            capitalization = capitalizacion
        ),
        isError = error != null,
        supportingText = error?.let { { Text(text = it, color = ErrorRojo, fontSize = 11.5.sp) } },
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AzulAccion,
            unfocusedBorderColor = Arena,
            focusedContainerColor = Blanco,
            unfocusedContainerColor = Blanco,
            cursorColor = AzulAccion,
            errorBorderColor = ErrorRojo,
            errorContainerColor = Blanco
        )
    )
}

@Composable
private fun MensajeError(texto: String) {
    Text(
        text = texto,
        color = ErrorRojo,
        fontSize = 11.5.sp
    )
}

// Checkbox de términos: es aquí donde el usuario crea la cuenta, no en el login
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
private fun RegistroScreenPreview() {
    AgricotechTheme {
        RegistroScreen(alVolver = {})
    }
}
