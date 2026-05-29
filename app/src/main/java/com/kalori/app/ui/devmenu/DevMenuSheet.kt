package com.kalori.app.ui.devmenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kalori.app.core.config.FeatureFlags
import java.time.LocalDate

/**
 * The debug dev menu. Reachable by long-pressing the Settings version string (debug only).
 * Provides: jump to date, seed fake logs, reset goal/calibration, force recalibration prompt,
 * toggle every feature flag. Calls into [DevMenuViewModel]; some actions are Phase-1 seams.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevMenuSheet(
    viewModel: DevMenuViewModel,
    onDismiss: () -> Unit,
) {
    val flags by viewModel.flags.collectAsState()
    val overrideDate by viewModel.overrideDate.collectAsState()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text("Dev menu", fontWeight = FontWeight.Bold)
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
            }

            item {
                Text("Date override: ${overrideDate ?: "live"}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        viewModel.jumpToDate(LocalDate.now().minusDays(7))
                    }) { Text("Jump -7d") }
                    TextButton(onClick = { viewModel.clearDateOverride() }) { Text("Clear") }
                }
            }

            item {
                TextButton(onClick = { viewModel.seedFakeLogs() }) { Text("Seed fake logs") }
                TextButton(onClick = { viewModel.resetGoalAndCalibration() }) {
                    Text("Reset goal / calibration")
                }
                TextButton(onClick = { viewModel.forceRecalibrationPrompt() }) {
                    Text("Force recalibration prompt")
                }
            }

            item {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Feature flags", fontWeight = FontWeight.Bold)
                    TextButton(onClick = { viewModel.resetFlags() }) { Text("Reset") }
                }
            }

            items(FeatureFlags.Flag.entries) { flag ->
                val enabled = flags[flag] ?: flag.default
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(flag.key)
                        if (flag.v11) Text("v1.1", fontWeight = FontWeight.Light)
                    }
                    Switch(
                        checked = enabled,
                        onCheckedChange = { viewModel.toggleFlag(flag, it) },
                    )
                }
            }
        }
    }
}
