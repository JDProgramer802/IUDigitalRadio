package com.iudigital.iudigitalradio.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.ui.components.AvatarWithCameraButton
import com.iudigital.iudigitalradio.ui.components.EmptyView
import com.iudigital.iudigitalradio.ui.components.SectionHeader
import com.iudigital.iudigitalradio.ui.components.StationItem
import com.iudigital.iudigitalradio.ui.components.auroraBackground
import com.iudigital.iudigitalradio.ui.theme.BrandBlue
import com.iudigital.iudigitalradio.ui.theme.Navy800
import com.iudigital.iudigitalradio.ui.theme.Navy950

private const val MAX_NAME_LENGTH = 30
private val CompactButtonPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)

@Composable
fun ProfileScreen(
    userName: String,
    onUserNameChange: (String) -> Unit,
    profileImage: Bitmap?,
    onChangePhoto: () -> Unit,
    selectedStation: RadioStation,
    isPlaying: Boolean,
    recentStations: List<RadioStation>,
    onStationSelected: (RadioStation) -> Unit,
) {
    var showNameDialog by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(key = "profile-card") {
            ProfileHeroCard(
                userName = userName,
                profileImage = profileImage,
                onChangePhoto = onChangePhoto,
                onEditName = { showNameDialog = true },
            )
        }
        item(key = "stats") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    icon = Icons.Rounded.History,
                    value = recentStations.size.toString(),
                    label = "Escuchadas en esta sesión",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    icon = Icons.Rounded.GraphicEq,
                    value = selectedStation.name,
                    label = if (isPlaying) "Sonando ahora" else "Emisora actual",
                    modifier = Modifier.weight(1.4f),
                )
            }
        }
        item(key = "recent-header") {
            SectionHeader(
                title = "Escuchadas recientemente",
                subtitle = "Historial de esta sesión",
                modifier = Modifier.padding(top = 6.dp),
            )
        }
        if (recentStations.isEmpty()) {
            item(key = "recent-empty") {
                EmptyView(message = "Aún no has escuchado ninguna emisora en esta sesión.")
            }
        } else {
            items(items = recentStations, key = { "recent-${it.id}" }) { station ->
                StationItem(
                    station = station,
                    isSelected = station.id == selectedStation.id,
                    isPlaying = isPlaying,
                    onClick = { onStationSelected(station) },
                )
            }
        }
        item(key = "privacy") { PrivacyNote() }
    }

    if (showNameDialog) {
        EditNameDialog(
            currentName = userName,
            onConfirm = { newName ->
                onUserNameChange(newName)
                showNameDialog = false
            },
            onDismiss = { showNameDialog = false },
        )
    }
}

@Composable
private fun ProfileHeroCard(
    userName: String,
    profileImage: Bitmap?,
    onChangePhoto: () -> Unit,
    onEditName: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Banner con degradado y el avatar montado sobre su borde inferior.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(176.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp)
                        .auroraBackground(listOf(Navy950, Navy800, BrandBlue)),
                )
                AvatarWithCameraButton(
                    image = profileImage,
                    size = 124.dp,
                    onChangePhoto = onChangePhoto,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("profile_name"),
                )
                Text(
                    text = "Cuenta de demostración",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                ) {
                    Button(
                        onClick = onChangePhoto,
                        contentPadding = CompactButtonPadding,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Cambiar foto",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 6.dp),
                        )
                    }
                    OutlinedButton(
                        onClick = onEditName,
                        contentPadding = CompactButtonPadding,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Editar nombre",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EditNameDialog(currentName: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var name by rememberSaveable { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar nombre") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= MAX_NAME_LENGTH) name = it },
                label = { Text("Nombre para mostrar") },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name.trim()) }, enabled = name.isNotBlank()) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}

@Composable
private fun PrivacyNote() {
    Row(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "La foto se guarda solo en este dispositivo. La app no crea cuentas ni envía datos personales.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
