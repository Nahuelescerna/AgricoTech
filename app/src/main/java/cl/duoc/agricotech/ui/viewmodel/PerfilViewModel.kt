package cl.duoc.agricotech.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agricotech.data.local.AgricoTechDatabase
import cl.duoc.agricotech.data.local.UsuarioEntity
import cl.duoc.agricotech.data.repository.CalificacionRepositorio
import cl.duoc.agricotech.data.repository.SesionRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PerfilUiState(
    val usuario: UsuarioEntity? = null,
    val promedio: Double? = null,
    val cantidadCalificaciones: Int = 0,
    val cargando: Boolean = true
)

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AgricoTechDatabase.obtener(application).usuarioDao()

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    // Se lee el usuario de Room usando el correo de la sesión
    fun cargar() {
        val correo = SesionRepositorio.usuarioEmail
        if (correo.isBlank()) {
            _uiState.value = PerfilUiState(usuario = null, cargando = false)
            return
        }

        viewModelScope.launch {
            val usuario = usuarioDao.buscarPorEmail(correo)
            val nombre = SesionRepositorio.nombreCompleto

            _uiState.value = PerfilUiState(
                usuario = usuario,
                promedio = CalificacionRepositorio.promedioDe(nombre),
                cantidadCalificaciones = CalificacionRepositorio.cantidadDe(nombre),
                cargando = false
            )
        }
    }
}
