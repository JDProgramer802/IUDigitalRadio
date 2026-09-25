package com.iudigital.iudigitalradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.iudigital.iudigitalradio.ui.IUDigitalRadioApp
import com.iudigital.iudigitalradio.ui.RadioViewModel
import com.iudigital.iudigitalradio.ui.theme.IUDigitalRadioTheme
import com.iudigital.iudigitalradio.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {

    // Un solo ViewModel para toda la app, ligado a la Activity: sobrevive a las rotaciones.
    private val viewModel: RadioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
            IUDigitalRadioTheme(themeMode = themeMode) {
                IUDigitalRadioApp(
                    viewModel = viewModel,
                    themeMode = themeMode,
                    onThemeModeChange = { themeMode = it },
                )
            }
        }
    }
}
