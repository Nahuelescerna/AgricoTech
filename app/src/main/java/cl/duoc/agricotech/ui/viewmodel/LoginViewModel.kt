package cl.duoc.agricotech.ui.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.agricotech.domain.validation.esContrasenaValida
import cl.duoc.agricotech.domain.validation.esEmailValido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Estado completo de la pantalla de login.
 * La UI solo lo pinta; toda la lógica vive en el ViewModel.
 */
data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val sesionIniciada: Boolean = false
)

class LoginViewModel : ViewModel() {

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

        val valido = errorCorreo == null && errorContrasena == null

        _uiState.update {
            it.copy(
                correo = correo,
                errorCorreo = errorCorreo,
                errorContrasena = errorContrasena,
                sesionIniciada = valido
            )
        }
    }

    // Se llama al cerrar el modal de confirmación
    fun cerrarConfirmacion() {
        _uiState.update { it.copy(sesionIniciada = false) }
    }
}
