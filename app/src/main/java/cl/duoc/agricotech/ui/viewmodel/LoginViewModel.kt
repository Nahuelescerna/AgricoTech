package cl.duoc.agricotech.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agricotech.data.local.AgricoTechDatabase
import cl.duoc.agricotech.data.repository.SesionRepositorio
import cl.duoc.agricotech.domain.model.RolUsuario
import cl.duoc.agricotech.domain.security.hashClave
import cl.duoc.agricotech.domain.validation.esContrasenaValida
import cl.duoc.agricotech.domain.validation.esEmailValido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado completo de la pantalla de login.
 * La UI solo lo pinta; la lógica y el acceso a datos viven aquí.
 */
data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val sesionIniciada: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AgricoTechDatabase.obtener(application).usuarioDao()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Al escribir se limpia el error del campo: el usuario está corrigiendo
    fun alCambiarCorreo(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = null) }
    }

    fun alCambiarContrasena(valor: String) {
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null) }
    }

    fun iniciarSesion() {
        val estado = _uiState.value
        val correo = estado.correo.trim()

        val errorCorreo = when {
            correo.isEmpty() -> "Ingresa tu correo."
            !esEmailValido(correo) -> "El correo no tiene un formato válido."
            else -> null
        }

        val errorContrasena = when {
            estado.contrasena.isEmpty() -> "Ingresa tu contraseña."
            !esContrasenaValida(estado.contrasena) -> "La contraseña debe tener al menos 6 caracteres."
            else -> null
        }

        if (errorCorreo != null || errorContrasena != null) {
            _uiState.update {
                it.copy(
                    correo = correo,
                    errorCorreo = errorCorreo,
                    errorContrasena = errorContrasena
                )
            }
            return
        }

        // Se consulta la base de datos fuera del hilo principal
        viewModelScope.launch {
            val usuario = usuarioDao.buscarPorEmail(correo)
            val claveCorrecta = usuario != null && usuario.claveHash == hashClave(estado.contrasena)

            if (!claveCorrecta) {
                _uiState.update { it.copy(errorContrasena = "Correo o contraseña incorrectos.") }
                return@launch
            }

            // Se recuerda el rol guardado en la base para abrir el home correcto
            usuario?.let { SesionRepositorio.iniciarSesion(RolUsuario.valueOf(it.rol)) }
            _uiState.update { it.copy(sesionIniciada = true) }
        }
    }

    // Se llama al cerrar el modal de confirmación
    fun cerrarConfirmacion() {
        _uiState.update { it.copy(sesionIniciada = false) }
    }
}
