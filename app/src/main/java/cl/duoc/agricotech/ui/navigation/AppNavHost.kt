package cl.duoc.agricotech.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.agricotech.domain.model.RolUsuario
import cl.duoc.agricotech.ui.screens.HomeScreen
import cl.duoc.agricotech.ui.screens.LoginScreen
import cl.duoc.agricotech.ui.screens.RegistroScreen

// Nombres de las rutas en un solo lugar para no repetir strings sueltos
object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val HOME = "home/{rol}"

    fun home(rol: RolUsuario) = "home/${rol.name}"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.LOGIN) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                // navigate apila el registro: la flecha de volver nos devuelve al login
                alCrearCuenta = { navController.navigate(Rutas.REGISTRO) },
                // Temporal: hasta que Room nos diga el rol del correo, entra como aplicador
                alContinuar = {
                    navController.navigate(Rutas.home(RolUsuario.APLICADOR)) {
                        popUpTo(Rutas.LOGIN) { inclusive = true } // el login no queda en la pila
                    }
                }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                alVolver = { navController.popBackStack() },
                alEntrar = { rol ->
                    navController.navigate(Rutas.home(rol)) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Rutas.HOME,
            arguments = listOf(navArgument("rol") { type = NavType.StringType })
        ) { entrada ->
            // Si el argumento no es un rol conocido, se cae en aplicador
            val rol = entrada.arguments
                ?.getString("rol")
                ?.let { valor -> runCatching { RolUsuario.valueOf(valor) }.getOrNull() }
                ?: RolUsuario.APLICADOR

            HomeScreen(
                rol = rol,
                alCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}
