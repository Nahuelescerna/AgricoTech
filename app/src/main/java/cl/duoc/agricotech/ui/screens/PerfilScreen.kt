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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agricotech.data.repository.SesionRepositorio
import cl.duoc.agricotech.domain.model.RolUsuario
import cl.duoc.agricotech.ui.theme.Arena
import cl.duoc.agricotech.ui.theme.AzulAccion
import cl.duoc.agricotech.ui.theme.AzulTexto
import cl.duoc.agricotech.ui.theme.Blanco
import cl.duoc.agricotech.ui.theme.ChipFondo
import cl.duoc.agricotech.ui.theme.Crema
import cl.duoc.agricotech.ui.theme.TextoSecundario
import cl.duoc.agricotech.ui.theme.Tinta
import cl.duoc.agricotech.ui.theme.Trigo
import cl.duoc.agricotech.ui.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(
    alCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PerfilViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val usuario = estado.usuario

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Crema)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Text(
            text = "Perfil",
            color = Tinta,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Círculo con las iniciales
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color = AzulAccion.copy(alpha = 0.12f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales(usuario?.nombre.orEmpty(), usuario?.apellido.orEmpty()),
                    color = AzulTexto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = listOfNotNull(usuario?.nombre, usuario?.apellido)
                        .joinToString(" ")
                        .ifBlank { "Usuario" },
                    color = Tinta,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .background(color = ChipFondo, shape = RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rolDeLaSesion().etiqueta,
                        color = TextoSecundario,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Datos personales",
            color = Tinta,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Blanco, shape = RoundedCornerShape(14.dp))
                .border(width = 1.dp, color = Arena, shape = RoundedCornerShape(14.dp))
        ) {
            FilaPerfil("Nombre", usuario?.nombre.orEmpty())
            HorizontalDivider(thickness = 1.dp, color = Arena)
            FilaPerfil("Apellido", usuario?.apellido.orEmpty())
            HorizontalDivider(thickness = 1.dp, color = Arena)
            FilaPerfil("RUT", usuario?.rut.orEmpty())
            HorizontalDivider(thickness = 1.dp, color = Arena)
            FilaPerfil("Correo", usuario?.email.orEmpty())
            HorizontalDivider(thickness = 1.dp, color = Arena)
            FilaPerfil("Teléfono", usuario?.telefono.orEmpty())
        }

        if (SesionRepositorio.rolActual == RolUsuario.APLICADOR) {
            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Calificación",
                color = Tinta,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Blanco, shape = RoundedCornerShape(14.dp))
                    .border(width = 1.dp, color = Arena, shape = RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                if (estado.promedio == null) {
                    Text(
                        text = "Sin calificaciones todavía",
                        color = Tinta,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cuando completes tu primer trabajo, el productor podrá calificarte de 1 a 5 estrellas.",
                        color = TextoSecundario,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { indice ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (indice < (estado.promedio ?: 0.0)) Trigo else Arena,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${"%.1f".format(estado.promedio)} · ${estado.cantidadCalificaciones} calificaciones",
                            color = Tinta,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = {
                SesionRepositorio.cerrarSesion()
                alCerrarSesion()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blanco,
                contentColor = Tinta
            )
        ) {
            Text(text = "Cerrar sesión", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun FilaPerfil(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            color = TextoSecundario,
            fontSize = 12.sp,
            modifier = Modifier.width(90.dp)
        )

        Text(
            text = valor.ifBlank { "—" },
            color = Tinta,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

// El rol se lee de la sesión activa
private fun rolDeLaSesion(): RolUsuario = SesionRepositorio.rolActual

private fun iniciales(nombre: String, apellido: String): String =
    listOf(nombre, apellido)
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .joinToString("")
        .ifBlank { "?" }
