package cl.duoc.agricotech.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agricotech.data.local.AgricoTechDatabase
import cl.duoc.agricotech.data.local.UsuarioEntity
import cl.duoc.agricotech.data.repository.SesionRepositorio
import cl.duoc.agricotech.domain.model.RolUsuario
import cl.duoc.agricotech.domain.security.hashClave
import cl.duoc.agricotech.domain.validation.esEmailValido
import cl.duoc.agricotech.domain.validation.esNombreValido
import cl.duoc.agricotech.domain.validation.esRutValido
import cl.duoc.agricotech.domain.validation.esTelefonoValido
import cl.duoc.agricotech.domain.validation.formatearRut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado completo de la pantalla de registro.
 * Un error en null significa que el campo está correcto.
 * La contraseña no forma parte de este formulario: se pide en el login.
 */
data class RegistroUiState(
    val rol: RolUsuario? = null,
    val nombre: String = "",
    val apellido: String = "",
    val rut: String = "",
    val email: String = "",
    val telefono: String = "",
    val clave: String = "",
    val aceptaTerminos: Boolean = false,
    val errorRol: String? = null,
    val errorNombre: String? = null,
    val errorApellido: String? = null,
    val errorRut: String? = null,
    val errorEmail: String? = null,
    val errorTelefono: String? = null,
    val errorClave: String? = null,
    val errorTerminos: String? = null,
    val guardando: Boolean = false,
    val cuentaCreada: Boolean = false
)

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AgricoTechDatabase.obtener(application).usuarioDao()

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

    fun alCambiarClave(valor: String) {
        _uiState.update { it.copy(clave = valor, errorClave = null) }
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

        val errorClave = when {
            estado.clave.isEmpty() -> "Crea una contraseña."
            estado.clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
            else -> null
        }

        val errorTerminos = if (!estado.aceptaTerminos) {
            "Debes aceptar los términos para continuar."
        } else {
            null
        }

        val valido = listOf(
            errorRol, errorNombre, errorApellido, errorRut,
            errorEmail, errorTelefono, errorClave, errorTerminos
        ).all { it == null }

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
                errorClave = errorClave,
                errorTerminos = errorTerminos
            )
        }

        if (!valido) return

        // Se guarda el usuario en Room
        _uiState.update { it.copy(guardando = true) }

        viewModelScope.launch {
            val correo = estado.email.trim()
            val yaExiste = usuarioDao.contarPorEmail(correo) > 0

            if (yaExiste) {
                _uiState.update {
                    it.copy(
                        guardando = false,
                        errorEmail = "Ese correo ya tiene una cuenta registrada."
                    )
                }
                return@launch
            }

            usuarioDao.insertar(
                UsuarioEntity(
                    nombre = estado.nombre.trim(),
                    apellido = estado.apellido.trim(),
                    rut = estado.rut,
                    email = correo,
                    telefono = estado.telefono,
                    rol = estado.rol?.name.orEmpty(),
                    claveHash = hashClave(estado.clave)
                )
            )

            // La sesión recuerda el rol para que el próximo login abra su home
            estado.rol?.let { SesionRepositorio.iniciarSesion(it) }

            _uiState.update { it.copy(guardando = false, cuentaCreada = true) }
        }
    }

    fun cerrarConfirmacion() {
        _uiState.update { it.copy(cuentaCreada = false) }
    }
}
