package cl.duoc.agricotech.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.agricotech.data.repository.SesionRepositorio
import cl.duoc.agricotech.domain.model.RolUsuario
import cl.duoc.agricotech.ui.screens.CotizacionScreen
import cl.duoc.agricotech.ui.screens.CrearSolicitudScreen
import cl.duoc.agricotech.ui.screens.DetalleSolicitudScreen
import cl.duoc.agricotech.ui.screens.HomeScreen
import cl.duoc.agricotech.ui.screens.LoginScreen
import cl.duoc.agricotech.ui.screens.RegistroScreen

// Nombres de las rutas en un solo lugar para no repetir strings sueltos
object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val HOME = "home/{rol}"
    const val DETALLE = "solicitud/{id}/{rol}"

    fun home(rol: RolUsuario) = "home/${rol.name}"
    const val COTIZAR = "cotizar/{id}"
    const val CREAR_SOLICITUD = "crear-solicitud"

    fun detalle(id: Int, rol: RolUsuario) = "solicitud/$id/${rol.name}"
    fun cotizar(id: Int) = "cotizar/$id"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.LOGIN) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                // navigate apila el registro: la flecha de volver nos devuelve al login
                alCrearCuenta = { navController.navigate(Rutas.REGISTRO) },
                // Entra con el rol de la última sesión; con Room saldrá del usuario guardado
                alContinuar = {
                    navController.navigate(Rutas.home(SesionRepositorio.rolActual)) {
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
                alVerDetalle = { solicitud ->
                    navController.navigate(Rutas.detalle(solicitud.id, rol))
                },
                alCrearSolicitud = { navController.navigate(Rutas.CREAR_SOLICITUD) },
                alCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Rutas.DETALLE,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("rol") { type = NavType.StringType }
            )
        ) { entrada ->
            val id = entrada.arguments?.getInt("id") ?: 0
            val rol = entrada.arguments
                ?.getString("rol")
                ?.let { valor -> runCatching { RolUsuario.valueOf(valor) }.getOrNull() }
                ?: RolUsuario.APLICADOR

            DetalleSolicitudScreen(
                id = id,
                rol = rol,
                alVolver = { navController.popBackStack() },
                alCotizar = { solicitud -> navController.navigate(Rutas.cotizar(solicitud.id)) }
            )
        }
        composable(
            route = Rutas.COTIZAR,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entrada ->
            val id = entrada.arguments?.getInt("id") ?: 0

            CotizacionScreen(
                id = id,
                alVolver = { navController.popBackStack() }
            )
        }
        composable(route = Rutas.CREAR_SOLICITUD) {
            CrearSolicitudScreen(
                alVolver = { navController.popBackStack() }
            )
        }
    }
}
