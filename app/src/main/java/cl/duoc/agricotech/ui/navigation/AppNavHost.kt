package cl.duoc.agricotech.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.agricotech.ui.screens.LoginScreen
import cl.duoc.agricotech.ui.screens.RegistroScreen

// Nombres de las rutas en un solo lugar para no repetir strings sueltos
object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.LOGIN) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                // navigate apila el registro: la flecha de volver nos devuelve al login
                alCrearCuenta = { navController.navigate(Rutas.REGISTRO) }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(alVolver = { navController.popBackStack() })
        }
    }
}
