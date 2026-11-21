package cl.duoc.agricotech.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agricotech.data.repository.CalificacionRepositorio
import cl.duoc.agricotech.data.repository.CotizacionRepositorio
import cl.duoc.agricotech.data.repository.SolicitudRepositorio
import cl.duoc.agricotech.domain.geo.conDistanciaDesde
import cl.duoc.agricotech.domain.model.Solicitud
import cl.duoc.agricotech.domain.model.Ubicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estado de la pantalla de inicio: la lista, si está cargando y si hubo error. */
data class HomeUiState(
    val solicitudes: List<Solicitud> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val _cargando = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    // Si el usuario da permiso de ubicación, las distancias se recalculan con el GPS
    private val _ubicacion = MutableStateFlow<Ubicacion?>(null)

    /**
     * La lista vive en el repositorio; acá se combina con el estado de la
     * petición y con la ubicación para que la pantalla muestre carga, error
     * o las solicitudes ya ordenadas por cercanía real.
     */
    val uiState: StateFlow<HomeUiState> = combine(
        SolicitudRepositorio.solicitudes,
        _cargando,
        _error,
        _ubicacion
    ) { solicitudes, cargando, error, ubicacion ->
        val conDistancia = if (ubicacion == null) {
            solicitudes
        } else {
            solicitudes.map { it.conDistanciaDesde(ubicacion) }
        }
        HomeUiState(solicitudes = conDistancia, cargando = cargando, error = error)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            // Las cotizaciones y calificaciones se necesitan en el detalle y el perfil
            val resultado = SolicitudRepositorio.sincronizar()
            CotizacionRepositorio.sincronizar()
            CalificacionRepositorio.sincronizar()

            resultado.onFailure { _error.value = mensajeDeError(it) }
            _cargando.value = false
        }
    }

    fun actualizarUbicacion(ubicacion: Ubicacion) {
        _ubicacion.value = ubicacion
    }
}
