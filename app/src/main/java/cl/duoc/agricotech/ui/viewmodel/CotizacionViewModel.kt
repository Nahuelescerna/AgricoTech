package cl.duoc.agricotech.ui.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.agricotech.data.repository.CotizacionRepositorio
import cl.duoc.agricotech.data.repository.SesionRepositorio
import cl.duoc.agricotech.data.repository.SolicitudRepositorio
import cl.duoc.agricotech.domain.model.Cotizacion
import cl.duoc.agricotech.domain.model.Solicitud
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Estado del formulario de cotización.
 * La solicitud se carga desde el repositorio para no pasarla por navegación.
 */
data class CotizacionUiState(
    val solicitud: Solicitud? = null,
    val tarifa: String = "",
    val observacion: String = "",
    val errorTarifa: String? = null,
    val cotizacionEnviada: Boolean = false
)

class CotizacionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CotizacionUiState())
    val uiState: StateFlow<CotizacionUiState> = _uiState.asStateFlow()

    fun cargarSolicitud(id: Int) {
        if (_uiState.value.solicitud?.id == id) return
        _uiState.update { it.copy(solicitud = SolicitudRepositorio.obtenerSolicitud(id)) }
    }

    // Solo se aceptan dígitos y un separador decimal
    fun alCambiarTarifa(valor: String) {
        val limpio = valor.filter { it.isDigit() || it == ',' || it == '.' }.take(6)
        _uiState.update { it.copy(tarifa = limpio, errorTarifa = null) }
    }

    fun alCambiarObservacion(valor: String) {
        _uiState.update { it.copy(observacion = valor) }
    }

    fun enviar(hectareas: Int): Boolean {
        val tarifa = _uiState.value.tarifa.replace(",", ".").toDoubleOrNull()

        val error = when {
            _uiState.value.tarifa.isBlank() -> "Ingresa una tarifa por hectárea."
            tarifa == null -> "La tarifa debe ser un número."
            tarifa <= 0 -> "La tarifa debe ser mayor que cero."
            else -> null
        }

        if (error != null) {
            _uiState.update { it.copy(errorTarifa = error, cotizacionEnviada = false) }
            return false
        }

        // Se registra la cotización para que el detalle la muestre al volver
        _uiState.value.solicitud?.let { solicitud ->
            CotizacionRepositorio.registrar(
                Cotizacion(
                    idSolicitud = solicitud.id,
                    aplicador = SesionRepositorio.nombreCompleto.ifBlank { "Tú" },
                    distanciaKm = 0,
                    calificacion = 5.0,
                    especialidad = solicitud.cultivo,
                    tarifaPorHectarea = tarifa ?: 0.0,
                    total = (tarifa ?: 0.0) * hectareas,
                    observacion = _uiState.value.observacion.trim()
                )
            )
        }

        _uiState.update { it.copy(errorTarifa = null, cotizacionEnviada = true) }
        return true
    }

    // Total estimado = tarifa por hectárea * superficie
    fun total(hectareas: Int): Double? =
        _uiState.value.tarifa.replace(",", ".").toDoubleOrNull()?.let { it * hectareas }

    fun cerrarConfirmacion() {
        _uiState.update { it.copy(cotizacionEnviada = false) }
    }
}
