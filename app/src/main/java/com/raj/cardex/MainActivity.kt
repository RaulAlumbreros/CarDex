package com.raj.cardex
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.raj.cardex.ui.theme.CarDexTheme
import com.raj.cardex.data.CocheAvistado
import com.raj.cardex.ui.PantallaCardex
import com.raj.cardex.viewmodel.PantallaLogin
import com.raj.cardex.viewmodel.PantallaRegistro
import com.raj.cardex.viewmodel.PantallaCamara
import com.raj.cardex.viewmodel.PantallaMapa
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarDexTheme {
                val navController = rememberNavController()
                val listaCoches = remember { mutableStateListOf<CocheAvistado>() }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = currentRoute in listOf("mapa", "camara", "cardex")
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Place, contentDescription = "Mapa") },
                                    label = { Text("Mapa") },
                                    selected = currentRoute == "mapa",
                                    onClick = {
                                        navController.navigate("mapa") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Cámara") },
                                    label = { Text("Cazar") },
                                    selected = currentRoute == "camara",
                                    onClick = {
                                        navController.navigate("camara") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.List, contentDescription = "Cardex") },
                                    label = { Text("Cardex") },
                                    selected = currentRoute == "cardex",
                                    onClick = {
                                        navController.navigate("cardex") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "iniciarSesion",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("iniciarSesion") {
                            PantallaLogin(
                                alIniciarSesionExito = {
                                    navController.navigate("cardex") {
                                        popUpTo("iniciarSesion") { inclusive = true }
                                    }
                                },
                                alNavegarARegistro = {
                                    navController.navigate("registro")
                                }
                            )
                        }
                        composable("registro") {
                            PantallaRegistro(
                                alRegistroExito = { navController.popBackStack() },
                                alVolverALogin = { navController.popBackStack() }
                            )
                        }
                        composable("mapa") { PantallaMapa() }
                        composable("camara") {
                            PantallaCamara(alCapturarCoche = { nuevoCoche ->
                                listaCoches.add(nuevoCoche)
                                navController.navigate("cardex")
                            })
                        }
                        composable("cardex") {
                            PantallaCardex(alHacerClicCoche = { coche -> println("Click en: ${coche.nombre}") })
                        }
                    }
                }
            }
        }
    }
}
