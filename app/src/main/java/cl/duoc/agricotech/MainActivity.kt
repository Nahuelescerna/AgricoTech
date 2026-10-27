package cl.duoc.agricotech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.agricotech.ui.screens.LoginScreen
import cl.duoc.agricotech.ui.theme.AgricotechTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgricotechTheme {
                LoginScreen()
            }
        }
    }
}