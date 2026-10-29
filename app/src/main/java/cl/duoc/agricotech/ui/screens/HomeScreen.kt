package cl.duoc.agricotech.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.agricotech.data.repository.SolicitudRepositorio
import cl.duoc.agricotech.domain.model.RolUsuario
import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.ui.components.TarjetaSolicitud
import cl.duoc.agricotech.ui.theme.AgricotechTheme
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.ChipFondo
import cl.duoc.agricotech.ui.theme.Crema
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta

/** Criterios de orden de la lista (RNF-04: cercanía, puntaje, especialidad). */
private enum class FiltroSolicitud(val etiqueta: String) {
    CERCANIA("Cercanas"),
    PUNTAJE("Mejor puntaje"),
    CULTIVO("Mi cultivo")
}

@Composable
fun HomeScreen(
    rol: RolUsuario,
    alCerrarSesion: () -> Unit = {},
    alVerDetalle: (Solicitud) -> Unit = {},
    alCrearSolicitud: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Pestaña activa de la barra inferior
    var pestana by remember { mutableStateOf(0) }

    Scaffold(
        modifier = modifier,
        containerColor = Crema,
        bottomBar = {
            BarraNavegacion(
                rol = rol,
                pestana = pestana,
                alCambiar = { pestana = it }
            )
        }
    ) { relleno ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
        ) {
            when (pestana) {
                0 -> if (rol == RolUsuario.APLICADOR) {
                    ListaSolicitudes(alVerDetalle = alVerDetalle)
                } else {
                    MisSolicitudes(
                        alVerDetalle = alVerDetalle,
                        alCrearSolicitud = alCrearSolicitud
                    )
                }

                1 -> EstadoVacio(
                    titulo = "Sin conversaciones",
                    mensaje = "El chat se activa cuando aceptas una cotización."
                )

                else -> PerfilScreen(alCerrarSesion = alCerrarSesion)
            }
        }
    }
}

// Pestaña del productor: sus publicaciones + acceso a crear una nueva
@Composable
private fun MisSolicitudes(
    alVerDetalle: (Solicitud) -> Unit,
    alCrearSolicitud: () -> Unit
) {
    val solicitudes by SolicitudRepositorio.solicitudes.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Text(
                text = "Mis solicitudes",
                color = Tinta,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${solicitudes.size} publicadas",
                color = TextoSecundario,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = alCrearSolicitud,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulAccion,
                    contentColor = Blanco
                )
            ) {
                Text(
                    text = "Publicar solicitud",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(solicitudes, key = { it.id }) { solicitud ->
                TarjetaSolicitud(
                    solicitud = solicitud,
                    alTocar = { alVerDetalle(solicitud) }
                )
            }
        }
    }
}

// Lista de solicitudes con los filtros de orden
@Composable
private fun ListaSolicitudes(alVerDetalle: (Solicitud) -> Unit) {
    var filtro by remember { mutableStateOf(FiltroSolicitud.CERCANIA) }

    // Flow del repositorio: si el productor publica, la lista se actualiza sola
    val solicitudes by SolicitudRepositorio.solicitudes.collectAsStateWithLifecycle()

    // El criterio elegido cambia el orden, no los datos
    val ordenadas = when (filtro) {
        FiltroSolicitud.CERCANIA -> solicitudes.sortedBy { it.distanciaKm }
        FiltroSolicitud.PUNTAJE -> solicitudes.sortedByDescending { it.calificacionProductor }
        FiltroSolicitud.CULTIVO -> solicitudes.sortedBy { it.cultivo }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Text(
                text = "Solicitudes",
                color = Tinta,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${ordenadas.size} parcelas cerca de ti",
                color = TextoSecundario,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FiltroSolicitud.entries.forEach { opcion ->
                    ChipFiltro(
                        texto = opcion.etiqueta,
                        activo = opcion == filtro,
                        alTocar = { filtro = opcion }
                    )
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ordenadas, key = { it.id }) { solicitud ->
                TarjetaSolicitud(
                    solicitud = solicitud,
                    alTocar = { alVerDetalle(solicitud) }
                )
            }
        }
    }
}

@Composable
private fun ChipFiltro(texto: String, activo: Boolean, alTocar: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (activo) AzulAccion else ChipFondo,
                shape = RoundedCornerShape(50)
            )
            .clickable(onClick = alTocar)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            text = texto,
            color = if (activo) Blanco else TextoSecundario,
            fontSize = 12.sp,
            fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

// Estado vacío: explica por qué no hay nada, en vez de dejar la pantalla en blanco
@Composable
private fun EstadoVacio(titulo: String, mensaje: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color = ChipFondo, shape = CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = titulo,
            color = Tinta,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = mensaje,
            color = TextoSecundario,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center
        )
    }
}

// Barra inferior: la primera pestaña cambia según el rol
@Composable
private fun BarraNavegacion(rol: RolUsuario, pestana: Int, alCambiar: (Int) -> Unit) {
    val esAplicador = rol == RolUsuario.APLICADOR

    Column(modifier = Modifier.background(Blanco)) {
        HorizontalDivider(thickness = 1.dp, color = Arena)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ItemBarra(
                seleccionado = pestana == 0,
                icono = if (esAplicador) Icons.Filled.Search else Icons.AutoMirrored.Filled.List,
                etiqueta = if (esAplicador) "Explorar" else "Mis solicitudes",
                alTocar = { alCambiar(0) }
            )
            ItemBarra(
                seleccionado = pestana == 1,
                icono = Icons.Filled.Email,
                etiqueta = "Mensajes",
                alTocar = { alCambiar(1) }
            )
            ItemBarra(
                seleccionado = pestana == 2,
                icono = Icons.Filled.Person,
                etiqueta = "Perfil",
                alTocar = { alCambiar(2) }
            )
        }
    }
}

@Composable
private fun ItemBarra(
    seleccionado: Boolean,
    icono: ImageVector,
    etiqueta: String,
    alTocar: () -> Unit
) {
    val color = if (seleccionado) AzulAccion else TextoSecundario

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = alTocar)
            .padding(horizontal = 18.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = etiqueta,
            color = color,
            fontSize = 10.sp,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
