package com.iudigital.iudigitalradio.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iudigital.iudigitalradio.audio.PlayerState
import com.iudigital.iudigitalradio.data.OfficialStation
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.data.model.StationCategory
import com.iudigital.iudigitalradio.data.repository.StationsUiState
import com.iudigital.iudigitalradio.ui.components.PlayerActions
import com.iudigital.iudigitalradio.ui.components.PlayerControls
import com.iudigital.iudigitalradio.ui.screens.StationsScreen
import com.iudigital.iudigitalradio.ui.theme.IUDigitalRadioTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StationSelectionTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val jazz = RadioStation(id = "jazz", name = "Jazz Radio", streamUrl = "https://example.com/jazz", tags = listOf("jazz"))
    private val rock = RadioStation(id = "rock", name = "Rock FM", streamUrl = "https://example.com/rock", tags = listOf("rock"))

    @Test
    fun seleccionarUnaEmisoraActualizaLaEmisoraActiva() {
        var selectedStation by mutableStateOf<RadioStation>(OfficialStation)
        composeRule.setContent {
            IUDigitalRadioTheme {
                StationsScreen(
                    officialStation = OfficialStation,
                    selectedStation = selectedStation,
                    isPlaying = false,
                    stationsState = StationsUiState.Success(listOf(jazz, rock)),
                    selectedCategory = StationCategory.COLOMBIA,
                    onCategorySelected = {},
                    onStationSelected = { selectedStation = it },
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithTag("station_${OfficialStation.id}").assertIsSelected()
        composeRule.onNodeWithTag("station_rock").performClick()

        assertEquals(rock, selectedStation)
        composeRule.onNodeWithTag("station_rock").assertIsSelected()
        composeRule.onNodeWithTag("station_${OfficialStation.id}").assertIsNotSelected()
    }

    @Test
    fun elBuscadorFiltraLaLista() {
        composeRule.setContent {
            IUDigitalRadioTheme {
                StationsScreen(
                    officialStation = OfficialStation,
                    selectedStation = OfficialStation,
                    isPlaying = false,
                    stationsState = StationsUiState.Success(listOf(jazz, rock)),
                    selectedCategory = StationCategory.COLOMBIA,
                    onCategorySelected = {},
                    onStationSelected = {},
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithTag("search_field").performTextInput("jazz")

        composeRule.onNodeWithText("Jazz Radio").assertExists()
        composeRule.onNodeWithText("Rock FM").assertDoesNotExist()
    }

    @Test
    fun elErrorDeLaApiMantieneDisponibleLaEmisoraOficial() {
        composeRule.setContent {
            IUDigitalRadioTheme {
                StationsScreen(
                    officialStation = OfficialStation,
                    selectedStation = OfficialStation,
                    isPlaying = false,
                    stationsState = StationsUiState.Error("No hay conexión a Internet."),
                    selectedCategory = StationCategory.COLOMBIA,
                    onCategorySelected = {},
                    onStationSelected = {},
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithText("No hay conexión a Internet.").assertExists()
        composeRule.onNodeWithTag("station_${OfficialStation.id}").assertExists()
    }

    @Test
    fun losControlesLlamanAPlayPauseYMute() {
        val calls = mutableListOf<String>()
        var state by mutableStateOf(PlayerState())
        composeRule.setContent {
            IUDigitalRadioTheme {
                PlayerControls(
                    playerState = state,
                    actions = PlayerActions(
                        onPlay = { calls += "play"; state = state.copy(isPlaying = true) },
                        onPause = { calls += "pause"; state = state.copy(isPlaying = false) },
                        onToggleMute = { calls += "mute"; state = state.copy(isMuted = !state.isMuted) },
                    ),
                )
            }
        }

        composeRule.onNodeWithTag("pause_button").assertIsNotEnabled()
        composeRule.onNodeWithTag("play_button").performClick()
        composeRule.onNodeWithTag("pause_button").assertIsEnabled().performClick()
        composeRule.onNodeWithTag("mute_button").performClick()

        assertEquals(listOf("play", "pause", "mute"), calls)
        assertEquals(true, state.isMuted)
    }
}
