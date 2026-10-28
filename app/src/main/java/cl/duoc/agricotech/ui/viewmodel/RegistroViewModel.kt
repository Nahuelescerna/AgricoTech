package cl.duoc.agricotech.ui.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.agricotech.data.repository.SesionRepositorio
import cl.duoc.agricotech.domain.model.RolUsuario
import cl.duoc.agricotech.domain.validation.esEmailValido
import cl.duoc.agricotech.domain.validation.esNombreValido
import cl.duoc.agricotech.domain.validation.esRutValido
import cl.duoc.agricotech.domain.validation.esTelefonoValido
import cl.duoc.agricotech.domain.validation.formatearRut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Estado completo de la pantalla de registro.
 * Un error en null significa que el campo está correcto.
 */
data class RegistroUiState(
    val rol: RolUsuario? = null,
    val nombre: String = "",
    val apellido: String = "",
    val rut: String = "",
    val email: String = "",
    val telefono: String = "",
    val aceptaTerminos: Boolean = false,
    val errorRol: String? = null,
    val errorNombre: String? = null,
    val errorApellido: String? = null,
    val errorRut: String? = null,
    val errorEmail: String? = null,
    val errorTelefono: String? = null,
    val errorTerminos: String? = null,
    val cuentaCreada: Boolean = false
)

class RegistroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun alSeleccionarRol(rol: RolUsuario) {
        _uiState.update { it.copy(rol = rol, errorRol = null) }
    }

    fun alCambiarNombre(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = null) }
    }

    fun alCambiarApellido(valor: String) {
        _uiState.update { it.copy(apellido = valor, errorApellido = null) }
    }

    // El RUT se va formateando mientras se escribe
    fun alCambiarRut(valor: String) {
        _uiState.update { it.copy(rut = formatearRut(valor), errorRut = null) }
    }

    fun alCambiarEmail(valor: String) {
        _uiState.update { it.copy(email = valor, errorEmail = null) }
    }

    fun alCambiarTelefono(valor: String) {
        _uiState.update { it.copy(telefono = valor, errorTelefono = null) }
    }

    fun alCambiarTerminos(acepta: Boolean) {
        _uiState.update { it.copy(aceptaTerminos = acepta, errorTerminos = null) }
    }

    fun registrar() {
        val estado = _uiState.value

        val errorRol = if (estado.rol == null) "Elige si eres productor o aplicador." else null

        val errorNombre = when {
            estado.nombre.isBlank() -> "Ingresa tu nombre."
            !esNombreValido(estado.nombre) -> "Usa solo letras, mínimo 2 caracteres."
            else -> null
        }

        val errorApellido = when {
            estado.apellido.isBlank() -> "Ingresa tu apellido."
            !esNombreValido(estado.apellido) -> "Usa solo letras, mínimo 2 caracteres."
            else -> null
        }

        val errorRut = when {
            estado.rut.isBlank() -> "Ingresa tu RUT."
            !esRutValido(estado.rut) -> "El RUT no es válido."
            else -> null
        }

        val errorEmail = when {
            estado.email.isBlank() -> "Ingresa tu correo."
            !esEmailValido(estado.email) -> "El correo no tiene un formato válido."
            else -> null
        }

        val errorTelefono = when {
            estado.telefono.isBlank() -> "Ingresa tu teléfono."
            !esTelefonoValido(estado.telefono) -> "Teléfono inválido. Ej: 912345678."
            else -> null
        }

        val errorTerminos = if (!estado.aceptaTerminos) {
            "Debes aceptar los términos para continuar."
        } else {
            null
        }

        val valido = listOf(
            errorRol, errorNombre, errorApellido, errorRut,
            errorEmail, errorTelefono, errorTerminos
        ).all { it == null }

        if (valido) {
            // Se recuerda el rol para que el próximo login abra su home
            estado.rol?.let { SesionRepositorio.iniciarSesion(it) }
        }

        _uiState.update {
            it.copy(
                nombre = estado.nombre.trim(),
                apellido = estado.apellido.trim(),
                email = estado.email.trim(),
                errorRol = errorRol,
                errorNombre = errorNombre,
                errorApellido = errorApellido,
                errorRut = errorRut,
                errorEmail = errorEmail,
                errorTelefono = errorTelefono,
                errorTerminos = errorTerminos,
                cuentaCreada = valido
            )
        }
    }

    fun cerrarConfirmacion() {
        _uiState.update { it.copy(cuentaCreada = false) }
    }
}
