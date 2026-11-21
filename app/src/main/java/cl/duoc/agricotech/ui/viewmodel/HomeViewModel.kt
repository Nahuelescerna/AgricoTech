package cl.duoc.agricotech.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agricotech.data.repository.CalificacionRepositorio
import cl.duoc.agricotech.data.repository.CotizacionRepositorio
import cl.duoc.agricotech.data.repository.SolicitudRepositorio
import cl.duoc.agricotech.domain.model.Solicitud
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

/** Estado de la pantalla de inicio: la lista, si está cargando y si hubo error. */
data class HomeUiState(
    val solicitudes: List<Solicitud> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val _cargando = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    /**
     * La lista vive en el repositorio; acá solo se combina con el estado de la
     * petición para que la pantalla muestre carga, error o contenido.
     */
    val uiState: StateFlow<HomeUiState> = combine(
        SolicitudRepositorio.solicitudes,
        _cargando,
        _error
    ) { solicitudes, cargando, error ->
        HomeUiState(solicitudes = solicitudes, cargando = cargando, error = error)
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

    private fun mensajeDeError(error: Throwable): String =
        if (error is IOException) {
            "Sin conexión. Revisa tu internet e intenta de nuevo."
        } else {
            "No pudimos cargar las solicitudes. Inténtalo otra vez."
        }
}
