package com.kalori.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kalori.app.BuildConfig
import com.kalori.app.ui.devmenu.DevMenuSheet
import com.kalori.app.ui.devmenu.DevMenuViewModel
import com.kalori.app.ui.theme.KaloriTheme

/**
 * Settings. Short labels per DESIGN_HANDOFF ("Haptics", not full sentences). feature-dev
 * fills in real settings; this stub wires the dev-menu affordance: long-press the version
 * string (debug only).
 */
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Innstillinger", style = MaterialTheme.typography.headlineSmall)
        // feature-dev: goals/targets, Health Connect status, notifications, haptics/sound, widget.
        VersionFooter()
    }
}

@Composable
private fun VersionFooter() {
    var showDevMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        VersionString(
            onLongPress = {
                // Dev menu reachable ONLY in debug. Identical behavior otherwise.
                if (BuildConfig.DEV_MENU_ENABLED) showDevMenu = true
            },
        )
    }

    if (showDevMenu && BuildConfig.DEV_MENU_ENABLED) {
        val viewModel: DevMenuViewModel = hiltViewModel()
        DevMenuSheet(
            viewModel = viewModel,
            onDismiss = { showDevMenu = false },
        )
    }
}

@Composable
private fun VersionString(onLongPress: () -> Unit) {
    Text(
        text = "Kalori ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onLongPress = { onLongPress() })
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    KaloriTheme { SettingsScreen(onBack = {}) }
}
