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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import cl.duoc.agricotech.ui.viewmodel.HomeUiState
import cl.duoc.agricotech.ui.viewmodel.HomeViewModel

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
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    // Pestaña activa de la barra inferior
    var pestana by remember { mutableStateOf(0) }

    // Lista y estado de la petición: se cargan una vez al entrar al home
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

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
                    ListaSolicitudes(
                        estado = estado,
                        alReintentar = viewModel::cargar,
                        alVerDetalle = alVerDetalle
                    )
                } else {
                    MisSolicitudes(
                        estado = estado,
                        alReintentar = viewModel::cargar,
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
    estado: HomeUiState,
    alReintentar: () -> Unit,
    alVerDetalle: (Solicitud) -> Unit,
    alCrearSolicitud: () -> Unit
) {
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
                text = "${estado.solicitudes.size} publicadas",
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

        // El listado ocupa el resto de la pantalla y resuelve carga o error
        Box(modifier = Modifier.weight(1f)) {
            ListadoSolicitudes(
                cargando = estado.cargando,
                error = estado.error,
                solicitudes = estado.solicitudes,
                alReintentar = alReintentar,
                alVerDetalle = alVerDetalle,
                vacioTitulo = "Aún no publicas nada",
                vacioMensaje = "Publica tu primera solicitud para recibir cotizaciones."
            )
        }
    }
}

// Lista de solicitudes con los filtros de orden
@Composable
private fun ListaSolicitudes(
    estado: HomeUiState,
    alReintentar: () -> Unit,
    alVerDetalle: (Solicitud) -> Unit
) {
    var filtro by remember { mutableStateOf(FiltroSolicitud.CERCANIA) }

    // El criterio elegido cambia el orden, no los datos
    val ordenadas = when (filtro) {
        FiltroSolicitud.CERCANIA -> estado.solicitudes.sortedBy { it.distanciaKm }
        FiltroSolicitud.PUNTAJE -> estado.solicitudes.sortedByDescending { it.calificacionProductor }
        FiltroSolicitud.CULTIVO -> estado.solicitudes.sortedBy { it.cultivo }
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

        Box(modifier = Modifier.weight(1f)) {
            ListadoSolicitudes(
                cargando = estado.cargando,
                error = estado.error,
                solicitudes = ordenadas,
                alReintentar = alReintentar,
                alVerDetalle = alVerDetalle,
                vacioTitulo = "No hay solicitudes",
                vacioMensaje = "Cuando un productor publique una, aparecerá acá."
            )
        }
    }
}

/**
 * Lista reutilizable para las dos pestañas: mientras la API responde muestra
 * el indicador, si falla ofrece reintentar y si no hay datos explica el vacío.
 */
@Composable
private fun ListadoSolicitudes(
    cargando: Boolean,
    error: String?,
    solicitudes: List<Solicitud>,
    alReintentar: () -> Unit,
    alVerDetalle: (Solicitud) -> Unit,
    vacioTitulo: String,
    vacioMensaje: String
) {
    when {
        cargando -> IndicadorCarga()
        error != null -> ErrorConexion(error, alReintentar)
        solicitudes.isEmpty() -> EstadoVacio(vacioTitulo, vacioMensaje)
        else -> LazyColumn(
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

@Composable
private fun IndicadorCarga() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AzulAccion)
    }
}

@Composable
private fun ErrorConexion(mensaje: String, alReintentar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = mensaje,
            color = TextoSecundario,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = alReintentar,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulAccion,
                contentColor = Blanco
            )
        ) {
            Text(text = "Reintentar", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
                .padding(vertical = 6.dp)
        ) {
            ItemBarra(
                seleccionado = pestana == 0,
                icono = if (esAplicador) Icons.Filled.Search else Icons.AutoMirrored.Filled.List,
                etiqueta = if (esAplicador) "Explorar" else "Mis solicitudes",
                alTocar = { alCambiar(0) },
                modifier = Modifier.weight(1f)
            )
            ItemBarra(
                seleccionado = pestana == 1,
                icono = Icons.Filled.Email,
                etiqueta = "Mensajes",
                alTocar = { alCambiar(1) },
                modifier = Modifier.weight(1f)
            )
            ItemBarra(
                seleccionado = pestana == 2,
                icono = Icons.Filled.Person,
                etiqueta = "Perfil",
                alTocar = { alCambiar(2) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ItemBarra(
    seleccionado: Boolean,
    icono: ImageVector,
    etiqueta: String,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (seleccionado) AzulAccion else TextoSecundario

    // El ancho lo entrega el Row (weight), así los tres centros quedan parejos
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = alTocar)
            .padding(vertical = 6.dp),
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
            fontSize = 9.5.sp,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
