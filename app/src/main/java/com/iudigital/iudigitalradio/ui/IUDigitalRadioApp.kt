package com.iudigital.iudigitalradio.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iudigital.iudigitalradio.R
import com.iudigital.iudigitalradio.camera.CameraResult
import com.iudigital.iudigitalradio.camera.rememberCameraLauncher
import com.iudigital.iudigitalradio.camera.rememberGalleryLauncher
import com.iudigital.iudigitalradio.data.repository.StationsUiState
import com.iudigital.iudigitalradio.permissions.PermissionHandler
import com.iudigital.iudigitalradio.ui.components.MiniPlayer
import com.iudigital.iudigitalradio.ui.components.PhotoOptionsSheet
import com.iudigital.iudigitalradio.ui.components.PlayerActions
import com.iudigital.iudigitalradio.ui.navigation.AppDestination
import com.iudigital.iudigitalradio.ui.navigation.AppNavigationBar
import com.iudigital.iudigitalradio.ui.navigation.LOADING_ROUTE
import com.iudigital.iudigitalradio.ui.screens.HomeScreen
import com.iudigital.iudigitalradio.ui.screens.LoadingScreen
import com.iudigital.iudigitalradio.ui.screens.PlayerScreen
import com.iudigital.iudigitalradio.ui.screens.ProfileScreen
import com.iudigital.iudigitalradio.ui.screens.SettingsScreen
import com.iudigital.iudigitalradio.ui.screens.StationsScreen
import com.iudigital.iudigitalradio.ui.theme.ThemeMode
import com.iudigital.iudigitalradio.utils.UserMessages
import kotlinx.coroutines.launch

const val DEFAULT_USER_NAME = "Usuario de prueba"

/** Composable raíz: navegación, barras, mensajes y conexión entre el ViewModel y las pantallas. */
@Composable
fun IUDigitalRadioApp(
    viewModel: RadioViewModel,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Datos simples del perfil: rememberSaveable los conserva al rotar y si Android recrea el proceso.
    var userName by rememberSaveable { mutableStateOf(DEFAULT_USER_NAME) }
    var showPhotoOptions by rememberSaveable { mutableStateOf(false) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = AppDestination.fromRoute(backStackEntry?.destination?.route)

    val playerActions = remember(viewModel) {
        PlayerActions(
            onPlay = viewModel::play,
            onPause = viewModel::pause,
            onToggleMute = viewModel::toggleMute,
        )
    }

    AppLifecycleEffect(
        onForeground = viewModel::onAppForegrounded,
        onBackground = viewModel::onAppBackgrounded,
    )

    fun showMessage(message: String, actionLabel: String? = null, onAction: () -> Unit = {}) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = if (actionLabel != null) SnackbarDuration.Long else SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) onAction()
        }
    }

    val launchCamera = rememberCameraLauncher { result ->
        when (result) {
            is CameraResult.PhotoTaken -> {
                viewModel.updateProfilePhoto(result.bitmap)
                showMessage(UserMessages.PHOTO_UPDATED)
            }
            CameraResult.Cancelled -> showMessage(UserMessages.CAMERA_CANCELLED)
            is CameraResult.PermissionDenied -> if (result.permanently) {
                showMessage(UserMessages.CAMERA_PERMISSION, actionLabel = "Configuración") {
                    PermissionHandler.openAppSettings(context)
                }
            } else {
                showMessage(UserMessages.CAMERA_PERMISSION)
            }
            CameraResult.Unavailable -> showMessage(UserMessages.CAMERA_UNAVAILABLE)
        }
    }

    val launchGallery = rememberGalleryLauncher { uri ->
        if (uri == null) {
            showMessage(UserMessages.GALLERY_CANCELLED)
        } else {
            viewModel.updateProfilePhoto(uri) { success ->
                showMessage(if (success) UserMessages.PHOTO_UPDATED else UserMessages.GALLERY_ERROR)
            }
        }
    }

    val openPhotoOptions = { showPhotoOptions = true }

    Scaffold(
        topBar = { currentDestination?.let { AppTopBar(it) } },
        bottomBar = {
            if (currentDestination != null) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(start = 12.dp, end = 12.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (currentDestination.showsMiniPlayer) {
                        MiniPlayer(
                            station = viewModel.selectedStation,
                            playerState = viewModel.playerState,
                            actions = playerActions,
                            onClick = { navController.navigateToTab(AppDestination.Player) },
                        )
                    }
                    AppNavigationBar(
                        current = currentDestination,
                        onNavigate = { navController.navigateToTab(it) },
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        val screenModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        NavHost(
            navController = navController,
            startDestination = LOADING_ROUTE,
            modifier = Modifier.fillMaxSize(),
        ) {
            // La pantalla de carga ocupa toda la pantalla; las demás respetan las barras.
            composable(LOADING_ROUTE) {
                val stations = viewModel.externalStations
                LoadingScreen(
                    isContentReady = stations !is StationsUiState.Loading,
                    statusText = when (stations) {
                        StationsUiState.Loading -> "Cargando emisoras…"
                        is StationsUiState.Error -> stations.message
                        else -> "¡Todo listo!"
                    },
                    onFinished = {
                        navController.navigate(AppDestination.Home.route) {
                            popUpTo(LOADING_ROUTE) { inclusive = true }
                        }
                    },
                )
            }
            composable(AppDestination.Home.route) {
                Box(screenModifier) {
                    HomeScreen(
                        userName = userName,
                        profileImage = viewModel.profileImage,
                        officialStation = viewModel.officialStation,
                        selectedStation = viewModel.selectedStation,
                        playerState = viewModel.playerState,
                        playerActions = playerActions,
                        selectedCategory = viewModel.stationCategory,
                        externalStations = viewModel.externalStations,
                        onStationSelected = viewModel::selectStation,
                        onCategorySelected = viewModel::selectCategory,
                        onRetryStations = viewModel::loadExternalStations,
                        onChangePhoto = openPhotoOptions,
                        onOpenProfile = { navController.navigateToTab(AppDestination.Profile) },
                        onOpenStations = { navController.navigateToTab(AppDestination.Stations) },
                        onOpenPlayer = { navController.navigateToTab(AppDestination.Player) },
                    )
                }
            }
            composable(AppDestination.Stations.route) {
                Box(screenModifier) {
                    StationsScreen(
                        officialStation = viewModel.officialStation,
                        selectedStation = viewModel.selectedStation,
                        isPlaying = viewModel.playerState.isPlaying,
                        stationsState = viewModel.externalStations,
                        selectedCategory = viewModel.stationCategory,
                        onCategorySelected = viewModel::selectCategory,
                        onStationSelected = viewModel::selectStation,
                        onRetry = viewModel::loadExternalStations,
                    )
                }
            }
            composable(AppDestination.Player.route) {
                Box(screenModifier) {
                    PlayerScreen(
                        station = viewModel.selectedStation,
                        playerState = viewModel.playerState,
                        playerActions = playerActions,
                        onOpenStations = { navController.navigateToTab(AppDestination.Stations) },
                    )
                }
            }
            composable(AppDestination.Profile.route) {
                Box(screenModifier) {
                    ProfileScreen(
                        userName = userName,
                        onUserNameChange = { userName = it },
                        profileImage = viewModel.profileImage,
                        onChangePhoto = openPhotoOptions,
                        selectedStation = viewModel.selectedStation,
                        isPlaying = viewModel.playerState.isPlaying,
                        recentStations = viewModel.recentStations,
                        onStationSelected = viewModel::selectStation,
                    )
                }
            }
            composable(AppDestination.Settings.route) {
                Box(screenModifier) {
                    SettingsScreen(
                        themeMode = themeMode,
                        onThemeModeChange = onThemeModeChange,
                        selectedStation = viewModel.selectedStation,
                    )
                }
            }
        }
    }

    if (showPhotoOptions) {
        PhotoOptionsSheet(
            hasPhoto = viewModel.profileImage != null,
            onTakePhoto = launchCamera,
            onPickFromGallery = launchGallery,
            onRemovePhoto = {
                viewModel.removeProfilePhoto()
                showMessage(UserMessages.PHOTO_REMOVED)
            },
            onDismiss = { showPhotoOptions = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(destination: AppDestination) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (destination == AppDestination.Home) {
                    Image(
                        painter = painterResource(R.drawable.logo_mark),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                }
                Column {
                    Text(text = destination.title, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = destination.subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

/** Navega entre pestañas sin apilar pantallas repetidas y conservando el estado de cada una. */
private fun NavHostController.navigateToTab(destination: AppDestination) {
    navigate(destination.route) {
        popUpTo(AppDestination.Home.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Pausa el audio cuando la app pasa a segundo plano (no hay servicio de reproducción) y lo
 * reanuda al volver. Una rotación no cuenta como salir de la app.
 */
@Composable
private fun AppLifecycleEffect(onForeground: () -> Unit, onBackground: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalActivity.current
    val currentOnForeground by rememberUpdatedState(onForeground)
    val currentOnBackground by rememberUpdatedState(onBackground)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> currentOnForeground()
                Lifecycle.Event.ON_STOP -> if (activity?.isChangingConfigurations != true) currentOnBackground()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
