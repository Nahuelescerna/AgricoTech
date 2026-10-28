package cl.duoc.agricotech.ui.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.agricotech.data.repository.CotizacionRepositorio
import cl.duoc.agricotech.data.repository.SolicitudRepositorio
import cl.duoc.agricotech.domain.model.Cotizacion
import cl.duoc.agricotech.domain.model.Solicitud
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Estado de la pantalla de detalle: la solicitud pedida, o null si no existe. */
data class DetalleSolicitudUiState(
    val solicitud: Solicitud? = null,
    val noEncontrada: Boolean = false,
    val cotizacion: Cotizacion? = null,
    val cantidadPostulaciones: Int = 0,
    val aceptada: Cotizacion? = null
)

class DetalleSolicitudViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleSolicitudUiState())
    val uiState: StateFlow<DetalleSolicitudUiState> = _uiState.asStateFlow()

    fun cargar(id: Int) {
        // Si ya está cargada la misma solicitud no se vuelve a consultar
        if (_uiState.value.solicitud?.id == id) return

        val solicitud = SolicitudRepositorio.obtenerSolicitud(id)
        _uiState.value = DetalleSolicitudUiState(
            solicitud = solicitud,
            noEncontrada = solicitud == null,
            cotizacion = CotizacionRepositorio.porSolicitud(id).firstOrNull { it.aplicador == "Tú" },
            cantidadPostulaciones = CotizacionRepositorio.porSolicitud(id).size,
            aceptada = CotizacionRepositorio.aceptadaDe(id)
        )
    }

    // Al volver de cotizar se refresca para mostrar la propuesta enviada
    fun refrescar() {
        val id = _uiState.value.solicitud?.id ?: return
        _uiState.update {
            it.copy(
                cotizacion = CotizacionRepositorio.porSolicitud(id).firstOrNull { c -> c.aplicador == "Tú" },
                cantidadPostulaciones = CotizacionRepositorio.porSolicitud(id).size,
                aceptada = CotizacionRepositorio.aceptadaDe(id)
            )
        }
    }
}
