package cl.duoc.agricotech.ui.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.agricotech.data.repository.SolicitudRepositorio
import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.domain.model.TipoProducto
import cl.duoc.agricotech.domain.model.Turno
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Obstáculos que el productor puede marcar (el caso pide detalle de obstáculos). */
val OBSTACULOS_DISPONIBLES = listOf("Antenas", "Cables", "Torres de alta tensión", "Otros")

data class CrearSolicitudUiState(
    val tipoProducto: TipoProducto = TipoProducto.LIQUIDO,
    val cultivo: String = "",
    val hectareas: String = "",
    val aguaDisponible: Boolean? = null,
    val turno: Turno? = null,
    val obstaculos: Set<String> = emptySet(),
    val comentarios: String = "",
    val errorCultivo: String? = null,
    val errorHectareas: String? = null,
    val errorAgua: String? = null,
    val errorTurno: String? = null,
    val publicada: Boolean = false
)

class CrearSolicitudViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CrearSolicitudUiState())
    val uiState: StateFlow<CrearSolicitudUiState> = _uiState.asStateFlow()

    fun alCambiarTipoProducto(tipo: TipoProducto) {
        _uiState.update { it.copy(tipoProducto = tipo) }
    }

    fun alCambiarCultivo(valor: String) {
        _uiState.update { it.copy(cultivo = valor, errorCultivo = null) }
    }

    fun alCambiarHectareas(valor: String) {
        _uiState.update { it.copy(hectareas = valor.filter { c -> c.isDigit() }.take(4), errorHectareas = null) }
    }

    fun alElegirAgua(disponible: Boolean) {
        _uiState.update { it.copy(aguaDisponible = disponible, errorAgua = null) }
    }

    fun alElegirTurno(turno: Turno) {
        _uiState.update { it.copy(turno = turno, errorTurno = null) }
    }

    // Los obstáculos son opcionales y acumulables: se marca y desmarca
    fun alAlternarObstaculo(obstaculo: String) {
        _uiState.update { estado ->
            val nuevos = if (estado.obstaculos.contains(obstaculo)) {
                estado.obstaculos - obstaculo
            } else {
                estado.obstaculos + obstaculo
            }
            estado.copy(obstaculos = nuevos)
        }
    }

    fun alCambiarComentarios(valor: String) {
        _uiState.update { it.copy(comentarios = valor) }
    }

    fun publicar(fotos: List<String> = emptyList()): Boolean {
        val estado = _uiState.value
        val hectareas = estado.hectareas.toIntOrNull()
        val cultivo = estado.cultivo.trim()

        val errorCultivo = when {
            cultivo.isEmpty() -> "Indica el cultivo."
            cultivo.length < 3 -> "El cultivo es demasiado corto."
            else -> null
        }

        val errorHectareas = when {
            estado.hectareas.isBlank() -> "Indica las hectáreas."
            hectareas == null || hectareas <= 0 -> "Las hectáreas deben ser mayores que cero."
            else -> null
        }

        val errorAgua = if (estado.aguaDisponible == null) "Indica si tienes agua en el terreno." else null
        val errorTurno = if (estado.turno == null) "Elige el turno de aplicación." else null

        val valido = listOf(errorCultivo, errorHectareas, errorAgua, errorTurno).all { it == null }

        if (!valido) {
            _uiState.update {
                it.copy(
                    errorCultivo = errorCultivo,
                    errorHectareas = errorHectareas,
                    errorAgua = errorAgua,
                    errorTurno = errorTurno,
                    publicada = false
                )
            }
            return false
        }

        val terreno = hectareas ?: 0

        SolicitudRepositorio.agregar(
            Solicitud(
                id = SolicitudRepositorio.siguienteId(),
                parcela = "Parcela $terreno ha · $cultivo",
                cultivo = cultivo,
                comuna = "Rancagua",     // ubicación ficticia del MVP
                distanciaKm = 5,
                hectareas = terreno,
                tipoProducto = estado.tipoProducto,
                turno = estado.turno ?: Turno.DIURNO,
                aguaDisponible = estado.aguaDisponible ?: false,
                obstaculos = if (estado.obstaculos.isEmpty()) {
                    "Sin obstáculos declarados."
                } else {
                    estado.obstaculos.joinToString(", ")
                },
                fotos = fotos,
                calificacionProductor = 0.0,
                trabajosPublicados = 0
            )
        )

        _uiState.update { it.copy(publicada = true) }
        return true
    }

    fun cerrarConfirmacion() {
        _uiState.update { it.copy(publicada = false) }
    }
}
