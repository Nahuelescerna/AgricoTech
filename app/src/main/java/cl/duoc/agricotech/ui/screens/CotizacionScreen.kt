package cl.duoc.agricotech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.ui.components.ModalConfirmacion
import cl.duoc.agricotech.ui.theme.AgricotechTheme
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.Crema
import cl.duoc.agricotech.ui.theme.ErrorRojo
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta
import cl.duoc.agricotech.ui.viewmodel.CotizacionViewModel

@Composable
fun CotizacionScreen(
    id: Int,
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CotizacionViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(id) { viewModel.cargarSolicitud(id) }

    val solicitud = estado.solicitud

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Crema)
            .safeDrawingPadding()
            .imePadding()
    ) {
        BarraCotizacion(alVolver = alVolver)

        if (solicitud == null) {
            Box(modifier = Modifier.fillMaxSize())
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                ResumenSolicitud(solicitud = solicitud)

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Tu propuesta",
                    color = Tinta,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = estado.tarifa,
                    onValueChange = viewModel::alCambiarTarifa,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Tarifa por hectárea (UF)",
                            color = TextoSecundario,
                            fontSize = 14.sp
                        )
                    },
                    singleLine = true,
                    isError = estado.errorTarifa != null,
                    supportingText = estado.errorTarifa?.let { mensaje ->
                        { Text(text = mensaje, color = ErrorRojo, fontSize = 11.5.sp) }
                    },
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = estado.observacion,
                    onValueChange = viewModel::alCambiarObservacion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    placeholder = {
                        Text(
                            text = "Observaciones (opcional). Ej: incluyo traslado del dron.",
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

                Spacer(modifier = Modifier.height(18.dp))

                val total = viewModel.total(solicitud.hectareas)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Blanco, shape = RoundedCornerShape(12.dp))
                        .border(width = 1.dp, color = Arena, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total estimado",
                            color = TextoSecundario,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${solicitud.hectareas} ha × ${estado.tarifa.ifBlank { "0" }} UF",
                            color = TextoSecundario,
                            fontSize = 10.5.sp
                        )
                    }

                    Text(
                        text = if (total == null) "—" else "UF ${"%.1f".format(total)}",
                        color = Tinta,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = { viewModel.enviar(solicitud.hectareas) },
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
                        text = "Enviar cotización",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (estado.cotizacionEnviada) {
        val total = estado.solicitud?.let { viewModel.total(it.hectareas) }

        ModalConfirmacion(
            titulo = "Cotización enviada",
            mensaje = if (total == null) {
                "Tu propuesta quedó registrada y el productor ya puede verla."
            } else {
                "Tu propuesta de UF ${"%.1f".format(total)} quedó registrada y el productor ya puede verla."
            },
            textoBoton = "Listo",
            alCerrar = {
                viewModel.cerrarConfirmacion()
                alVolver()
            }
        )
    }
}

// Resumen compacto de la solicitud que se está cotizando
@Composable
private fun ResumenSolicitud(solicitud: Solicitud) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Blanco, shape = RoundedCornerShape(14.dp))
            .border(width = 1.dp, color = Arena, shape = RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = solicitud.parcela,
            color = Tinta,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${solicitud.comuna} · ${solicitud.hectareas} ha · ${solicitud.tipoProducto.etiqueta}",
            color = TextoSecundario,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun BarraCotizacion(alVolver: () -> Unit) {
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
            text = "Enviar cotización",
            color = Tinta,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }

    HorizontalDivider(thickness = 1.dp, color = Arena)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun CotizacionPreview() {
    AgricotechTheme {
        CotizacionScreen(id = 1, alVolver = {})
    }
}
