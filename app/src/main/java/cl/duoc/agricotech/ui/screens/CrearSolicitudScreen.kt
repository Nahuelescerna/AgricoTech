package cl.duoc.agricotech.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agricotech.domain.model.TipoProducto
import cl.duoc.agricotech.domain.model.Turno
import cl.duoc.agricotech.ui.components.MiniaturaFoto
import cl.duoc.agricotech.ui.components.ModalConfirmacion
import cl.duoc.agricotech.ui.theme.AgricotechTheme
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.ChipFondo
import cl.duoc.agricotech.ui.theme.Crema
import cl.duoc.agricotech.ui.theme.ErrorRojo
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta
import cl.duoc.agricotech.ui.viewmodel.CrearSolicitudViewModel
import cl.duoc.agricotech.ui.viewmodel.OBSTACULOS_DISPONIBLES
import java.io.File

@Composable
fun CrearSolicitudScreen(
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CrearSolicitudViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    // Fotos tomadas con la cámara del teléfono (recurso nativo)
    val fotos = remember { mutableStateListOf<Uri>() }
    var uriPendiente by remember { mutableStateOf<Uri?>(null) }

    val camara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { guardada ->
        if (guardada) uriPendiente?.let { fotos.add(it) }
    }

    fun tomarFoto() {
        // Se guarda en la caché de la app y se comparte con la cámara vía FileProvider
        val archivo = File(contexto.cacheDir, "solicitud_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            contexto,
            "${contexto.packageName}.fileprovider",
            archivo
        )
        uriPendiente = uri
        camara.launch(uri)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Crema)
            .safeDrawingPadding()
            .imePadding()
    ) {
        BarraCrear(alVolver = alVolver)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            EtiquetaSeccion(texto = "Tipo de producto")
            SelectorTipoProducto(
                seleccionado = estado.tipoProducto,
                alElegir = viewModel::alCambiarTipoProducto
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = estado.cultivo,
                onValueChange = viewModel::alCambiarCultivo,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cultivo (ej: Paltos)", color = TextoSecundario, fontSize = 14.sp) },
                singleLine = true,
                isError = estado.errorCultivo != null,
                supportingText = estado.errorCultivo?.let { mensaje ->
                    { Text(text = mensaje, color = ErrorRojo, fontSize = 11.5.sp) }
                },
                shape = RoundedCornerShape(10.dp),
                colors = coloresCampo()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = estado.hectareas,
                onValueChange = viewModel::alCambiarHectareas,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Hectáreas", color = TextoSecundario, fontSize = 14.sp) },
                singleLine = true,
                isError = estado.errorHectareas != null,
                supportingText = estado.errorHectareas?.let { mensaje ->
                    { Text(text = mensaje, color = ErrorRojo, fontSize = 11.5.sp) }
                },
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = coloresCampo()
            )

            Spacer(modifier = Modifier.height(20.dp))

            EtiquetaSeccion(texto = "¿Tiene agua?")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OpcionChip(
                    texto = "Sí",
                    activo = estado.aguaDisponible == true,
                    alTocar = { viewModel.alElegirAgua(true) }
                )
                OpcionChip(
                    texto = "No",
                    activo = estado.aguaDisponible == false,
                    alTocar = { viewModel.alElegirAgua(false) }
                )
            }
            estado.errorAgua?.let { MensajeError(texto = it) }

            Spacer(modifier = Modifier.height(20.dp))

            EtiquetaSeccion(texto = "Aplicación")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OpcionChip(
                    texto = "Diurna",
                    activo = estado.turno == Turno.DIURNO,
                    alTocar = { viewModel.alElegirTurno(Turno.DIURNO) }
                )
                OpcionChip(
                    texto = "Nocturna",
                    activo = estado.turno == Turno.NOCTURNO,
                    alTocar = { viewModel.alElegirTurno(Turno.NOCTURNO) }
                )
            }
            estado.errorTurno?.let { MensajeError(texto = it) }

            Spacer(modifier = Modifier.height(20.dp))

            EtiquetaSeccion(texto = "Obstáculos")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OBSTACULOS_DISPONIBLES.forEach { obstaculo ->
                    OpcionChip(
                        texto = obstaculo,
                        activo = estado.obstaculos.contains(obstaculo),
                        alTocar = { viewModel.alAlternarObstaculo(obstaculo) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = estado.comentarios,
                onValueChange = viewModel::alCambiarComentarios,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                placeholder = {
                    Text("Comentarios adicionales", color = TextoSecundario, fontSize = 13.5.sp)
                },
                shape = RoundedCornerShape(10.dp),
                colors = coloresCampo()
            )

            Spacer(modifier = Modifier.height(18.dp))

            EtiquetaSeccion(texto = "Fotos de los obstáculos")

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (fotos.size < 3) {
                    BotonCamara(alTocar = ::tomarFoto)
                }

                fotos.forEach { uri ->
                    MiniaturaFoto(uri = uri, modifier = Modifier.size(64.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = { viewModel.publicar(fotos.map { it.toString() }) },
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
                Text(text = "Publicar solicitud", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (estado.publicada) {
        ModalConfirmacion(
            titulo = "Solicitud publicada",
            mensaje = "Los aplicadores de tu zona ya pueden verla y enviarte cotizaciones.",
            textoBoton = "Listo",
            alCerrar = {
                viewModel.cerrarConfirmacion()
                alVolver()
            }
        )
    }
}

// Selector de tipo de producto: un campo que abre un menú
@Composable
private fun SelectorTipoProducto(
    seleccionado: TipoProducto,
    alElegir: (TipoProducto) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Blanco, shape = RoundedCornerShape(10.dp))
                .border(width = 1.dp, color = Arena, shape = RoundedCornerShape(10.dp))
                .clickable { abierto = true }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = seleccionado.etiqueta, color = Tinta, fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = TextoSecundario
            )
        }

        DropdownMenu(
            expanded = abierto,
            onDismissRequest = { abierto = false }
        ) {
            TipoProducto.entries.forEach { tipo ->
                DropdownMenuItem(
                    text = { Text(text = tipo.etiqueta, fontSize = 14.sp) },
                    onClick = {
                        alElegir(tipo)
                        abierto = false
                    }
                )
            }
        }
    }
}

// Chip seleccionable, usado para agua, turno y obstáculos
@Composable
private fun OpcionChip(texto: String, activo: Boolean, alTocar: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (activo) AzulAccion else ChipFondo,
                shape = RoundedCornerShape(50)
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

// Botón redondo con + para abrir la cámara
@Composable
private fun BotonCamara(alTocar: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(color = Blanco, shape = RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = Arena, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = alTocar),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Tomar foto de un obstáculo",
            tint = AzulAccion
        )
    }
}

@Composable
private fun EtiquetaSeccion(texto: String) {
    Text(
        text = texto,
        color = TextoSecundario,
        fontSize = 12.5.sp,
        fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun MensajeError(texto: String) {
    Spacer(modifier = Modifier.height(6.dp))
    Text(text = texto, color = ErrorRojo, fontSize = 11.5.sp)
}

@Composable
private fun coloresCampo() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AzulAccion,
    unfocusedBorderColor = Arena,
    focusedContainerColor = Blanco,
    unfocusedContainerColor = Blanco,
    cursorColor = AzulAccion,
    errorBorderColor = ErrorRojo,
    errorContainerColor = Blanco
)

@Composable
private fun BarraCrear(alVolver: () -> Unit) {
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
                contentDescription = "Volver",
                tint = Tinta
            )
        }

        Text(
            text = "Nueva solicitud",
            color = Tinta,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }

    HorizontalDivider(thickness = 1.dp, color = Arena)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun CrearSolicitudPreview() {
    AgricotechTheme {
        CrearSolicitudScreen(alVolver = {})
    }
}
