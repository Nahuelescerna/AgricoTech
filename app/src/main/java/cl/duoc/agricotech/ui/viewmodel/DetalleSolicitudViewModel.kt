package cl.duoc.agricotech.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agricotech.data.repository.CalificacionRepositorio
import cl.duoc.agricotech.data.repository.CotizacionRepositorio
import cl.duoc.agricotech.data.repository.SesionRepositorio
import cl.duoc.agricotech.data.repository.SolicitudRepositorio
import cl.duoc.agricotech.domain.model.Calificacion
import cl.duoc.agricotech.domain.model.Cotizacion
import cl.duoc.agricotech.domain.model.Solicitud
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado de la pantalla de detalle: la solicitud pedida, o null si no existe. */
data class DetalleSolicitudUiState(
    val solicitud: Solicitud? = null,
    val cargando: Boolean = true,
    val noEncontrada: Boolean = false,
    val cotizacion: Cotizacion? = null,
    val cantidadPostulaciones: Int = 0,
    val aceptada: Cotizacion? = null,
    val calificacion: Calificacion? = null,
    val errorCalificacion: String? = null
)

class DetalleSolicitudViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleSolicitudUiState())
    val uiState: StateFlow<DetalleSolicitudUiState> = _uiState.asStateFlow()

    fun cargar(id: Int) {
        // Si ya está cargada la misma solicitud no se vuelve a consultar
        if (_uiState.value.solicitud?.id == id) return

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, noEncontrada = false) }

            SolicitudRepositorio.obtener(id)
                .onSuccess { solicitud ->
                    _uiState.value = estadoCon(solicitud).copy(cargando = false)
                }
                .onFailure {
                    // Sin red se muestra lo que ya estaba en la caché del home
                    val enCache = SolicitudRepositorio.obtenerSolicitud(id)
                    _uiState.value = estadoCon(enCache).copy(
                        cargando = false,
                        noEncontrada = enCache == null
                    )
                }
        }
    }

    // El productor califica al aplicador de la propuesta aceptada
    fun calificar(estrellas: Int, comentario: String) {
        val aceptada = _uiState.value.aceptada ?: return
        val calificacion = Calificacion(
            idSolicitud = aceptada.idSolicitud,
            aplicador = aceptada.aplicador,
            aplicadorCorreo = aceptada.aplicadorCorreo,
            estrellas = estrellas,
            comentario = comentario
        )

        viewModelScope.launch {
            CalificacionRepositorio.registrar(calificacion)
                .onSuccess {
                    _uiState.update {
                        it.copy(calificacion = calificacion, errorCalificacion = null)
                    }
                }
                .onFailure { fallo ->
                    _uiState.update { it.copy(errorCalificacion = mensajeDeError(fallo)) }
                }
        }
    }

    fun refrescar() {
        val solicitud = _uiState.value.solicitud ?: return
        _uiState.update { estadoCon(solicitud).copy(cargando = false) }
    }

    /** Arma el estado de la pantalla con las propuestas y la calificación de la solicitud. */
    private fun estadoCon(solicitud: Solicitud?): DetalleSolicitudUiState {
        val id = solicitud?.id ?: return DetalleSolicitudUiState(solicitud = null)
        val propuestas = CotizacionRepositorio.porSolicitud(id)
        return DetalleSolicitudUiState(
            solicitud = solicitud,
            cotizacion = propuestas.firstOrNull {
                it.aplicadorCorreo.equals(SesionRepositorio.usuarioEmail, ignoreCase = true)
            },
            cantidadPostulaciones = propuestas.size,
            aceptada = CotizacionRepositorio.aceptadaDe(id),
            calificacion = CalificacionRepositorio.porSolicitud(id)
        )
    }
}
