package com.idat.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.idat.presentation.login.LoginScreen
import com.idat.presentation.registro.RegistroScreen
import com.idat.presentation.catalogo.CatalogoScreen
import com.idat.presentation.carrito.CarritoScreen
import com.idat.presentation.detalle.DetalleScreen
import com.idat.presentation.favoritos.FavoritosScreen
import com.idat.presentation.personalizacion.PersonalizacionScreen
import com.idat.presentation.configuracion.ConfiguracionScreen
import com.idat.presentation.gestion.GestionProductosScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("registro") { RegistroScreen(navController) }
        composable("catalogo") { CatalogoScreen(navController) }
        composable("carrito") { CarritoScreen(navController) }
        composable("favoritos") { FavoritosScreen(navController) }
        composable("personalizacion") { PersonalizacionScreen(navController) }
        composable("configuracion") { ConfiguracionScreen(navController) }
        composable("gestion") { GestionProductosScreen(navController) }
        composable(
            route = "detalle/{productoId}",
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0
            DetalleScreen(navController, productoId)
        }
    }

}
