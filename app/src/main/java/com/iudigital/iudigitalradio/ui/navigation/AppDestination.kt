package com.iudigital.iudigitalradio.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

const val LOADING_ROUTE = "loading"

/** Pantallas de la barra de navegación inferior. */
enum class AppDestination(
    val route: String,
    val label: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val showsMiniPlayer: Boolean,
) {
    Home("home", "Inicio", "IU Digital Radio", "Tu música, tu momento", Icons.Rounded.Home, showsMiniPlayer = false),
    Stations("stations", "Emisoras", "Emisoras", "La oficial y otras del mundo", Icons.Rounded.Radio, showsMiniPlayer = true),
    Player("player", "Reproductor", "Reproductor", "Transmisión en vivo", Icons.Rounded.PlayCircle, showsMiniPlayer = false),
    Profile("profile", "Perfil", "Mi perfil", "Tu espacio en la app", Icons.Rounded.Person, showsMiniPlayer = true),
    Settings("settings", "Ajustes", "Configuración", "Personaliza la app", Icons.Rounded.Settings, showsMiniPlayer = true);

    companion object {
        fun fromRoute(route: String?): AppDestination? = entries.firstOrNull { it.route == route }
    }
}
