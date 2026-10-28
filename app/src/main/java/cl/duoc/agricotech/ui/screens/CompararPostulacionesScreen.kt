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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agricotech.domain.model.Cotizacion
import cl.duoc.agricotech.ui.components.ModalConfirmacion
import cl.duoc.agricotech.ui.theme.AgricotechTheme
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.ChipFondo
import cl.duoc.agricotech.ui.theme.Crema
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta
import cl.duoc.agricotech.ui.theme.Trigo
import cl.duoc.agricotech.ui.theme.VerdeCampo
import cl.duoc.agricotech.ui.viewmodel.CompararPostulacionesViewModel

@Composable
fun CompararPostulacionesScreen(
    id: Int,
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CompararPostulacionesViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(id) { viewModel.cargar(id) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Crema)
            .safeDrawingPadding()
    ) {
        BarraComparar(alVolver = alVolver)

        if (estado.solicitud == null) {
            Box(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 16.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = estado.solicitud?.parcela.orEmpty(),
                        color = Tinta,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${estado.propuestas.size} propuestas · ordenadas por cercanía, puntaje y especialidad",
                        color = TextoSecundario,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(estado.propuestas, key = { it.aplicador }) { propuesta ->
                    val esMejor = propuesta == estado.propuestas.firstOrNull()
                    val estaAceptada = estado.aceptada?.aplicador == propuesta.aplicador

                    TarjetaPropuesta(
                        propuesta = propuesta,
                        esMejorOpcion = esMejor && estado.aceptada == null,
                        estaAceptada = estaAceptada,
                        alAceptar = { viewModel.aceptar(propuesta) }
                    )
                }
            }
        }
    }

    if (estado.mostrarConfirmacion) {
        ModalConfirmacion(
            titulo = "Propuesta aceptada",
            mensaje = "Aceptaste la propuesta de ${estado.aceptada?.aplicador}. " +
                "El chat entre ambos se habilitará cuando esté implementado.",
            textoBoton = "Entendido",
            alCerrar = { viewModel.cerrarConfirmacion() }
        )
    }
}

@Composable
private fun TarjetaPropuesta(
    propuesta: Cotizacion,
    esMejorOpcion: Boolean,
    estaAceptada: Boolean,
    alAceptar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Blanco, shape = RoundedCornerShape(14.dp))
            .border(
                width = if (estaAceptada) 2.dp else 1.dp,
                color = if (estaAceptada) VerdeCampo else Arena,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        if (esMejorOpcion || estaAceptada) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (estaAceptada) "ACEPTADA" else "MEJOR OPCIÓN",
                    color = if (estaAceptada) VerdeCampo else AzulAccion,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Círculo con las iniciales del aplicador
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = ChipFondo, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales(propuesta.aplicador),
                    color = TextoSecundario,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = propuesta.aplicador,
                    color = Tinta,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Trigo,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${propuesta.calificacion} · ${propuesta.especialidad}",
                        color = TextoSecundario,
                        fontSize = 10.5.sp
                    )
                }
            }

            Text(
                text = "a ${propuesta.distanciaKm} km",
                color = TextoSecundario,
                fontSize = 10.5.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "UF ${"%.1f".format(propuesta.total)}",
                    color = Tinta,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${"%.1f".format(propuesta.tarifaPorHectarea)} UF por hectárea",
                    color = TextoSecundario,
                    fontSize = 10.5.sp
                )
            }

            if (!estaAceptada) {
                Button(
                    onClick = alAceptar,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (esMejorOpcion) AzulAccion else Blanco,
                        contentColor = if (esMejorOpcion) Blanco else Tinta
                    ),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(text = "Aceptar", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (propuesta.observacion.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = propuesta.observacion,
                color = TextoSecundario,
                fontSize = 11.5.sp,
                lineHeight = 16.sp
            )
        }
    }
}

private fun iniciales(nombre: String): String =
    nombre.split(" ").filter { it.isNotBlank() }.take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")

@Composable
private fun BarraComparar(alVolver: () -> Unit) {
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
            text = "Comparar propuestas",
            color = Tinta,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }

    HorizontalDivider(thickness = 1.dp, color = Arena)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun CompararPreview() {
    AgricotechTheme {
        CompararPostulacionesScreen(id = 1, alVolver = {})
    }
}
