package cl.duoc.agricotech.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agricotech.data.repository.CotizacionRepositorio
import cl.duoc.agricotech.data.repository.SolicitudRepositorio
import cl.duoc.agricotech.domain.model.Cotizacion
import cl.duoc.agricotech.domain.model.Solicitud
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CompararUiState(
    val solicitud: Solicitud? = null,
    val propuestas: List<Cotizacion> = emptyList(),
    val aceptada: Cotizacion? = null,
    val aceptando: Boolean = false,
    val error: String? = null,
    val mostrarConfirmacion: Boolean = false
)

class CompararPostulacionesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CompararUiState())
    val uiState: StateFlow<CompararUiState> = _uiState.asStateFlow()

    private var solicitudId: Int = 0

    fun cargar(id: Int) {
        solicitudId = id
        val solicitud = SolicitudRepositorio.obtenerSolicitud(id)
        val propuestas = CotizacionRepositorio.porSolicitud(id)

        _uiState.value = CompararUiState(
            solicitud = solicitud,
            propuestas = ordenar(propuestas, solicitud?.cultivo.orEmpty()),
            aceptada = CotizacionRepositorio.aceptadaDe(id)
        )
    }

    // Aceptar guarda el estado en la API, así no se pierde al reiniciar la app
    fun aceptar(cotizacion: Cotizacion) {
        if (_uiState.value.aceptando) return

        viewModelScope.launch {
            _uiState.update { it.copy(aceptando = true, error = null) }

            CotizacionRepositorio.aceptar(cotizacion)
                .onSuccess { actualizada ->
                    _uiState.update {
                        it.copy(
                            aceptada = actualizada,
                            aceptando = false,
                            mostrarConfirmacion = true
                        )
                    }
                }
                .onFailure { fallo ->
                    _uiState.update { it.copy(aceptando = false, error = mensajeDeError(fallo)) }
                }
        }
    }

    fun cerrarConfirmacion() {
        _uiState.update { it.copy(mostrarConfirmacion = false) }
    }

    fun refrescar() {
        if (solicitudId == 0) return
        cargar(solicitudId)
    }

    /**
     * Criterio del documento (RNF-04): primero cercanía, después puntaje
     * y por último la especialidad en el cultivo. Sin descartes rígidos:
     * todas las propuestas se muestran, solo se ordenan.
     */
    private fun ordenar(propuestas: List<Cotizacion>, cultivo: String): List<Cotizacion> =
        propuestas.sortedWith(
            compareBy<Cotizacion> { it.distanciaKm }
                .thenByDescending { it.calificacion }
                .thenByDescending { it.especialidad.equals(cultivo, ignoreCase = true) }
        )
}
