package com.kalori.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kalori.app.data.fake.SampleData
import com.kalori.app.domain.model.LogEntry
import com.kalori.app.ui.components.NumericStat
import com.kalori.app.ui.theme.KaloriTheme

/**
 * Phase-1 placeholder screens. These render from [SampleData] (fakes) so navigation and the
 * theme/component seams are exercisable end to end. feature-dev REPLACES each of these with a
 * real ViewModel + immutable UI state + empty/loaded/error/loading previews per the
 * definition of done. No business logic belongs in these composables.
 */

@Composable
fun TodayScreen(
    onAddFood: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val entries = SampleData.logHistory().filter { it.date == SampleData.logHistory().first().date }
    val totalKcal = entries.sumOf { it.nutrition.kcal }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("I dag", style = MaterialTheme.typography.headlineSmall)
        NumericStat(value = formatKcal(totalKcal), label = "kcal")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(entries) { entry -> LogRow(entry) }
        }
    }
}

@Composable
private fun LogRow(entry: LogEntry) {
    val food = SampleData.foods.firstOrNull { it.id == entry.foodId }
    Text(
        text = "${food?.name ?: "Hurtigregistrering"} — ${formatKcal(entry.nutrition.kcal)} kcal",
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun AddFoodScreen(onBack: () -> Unit) {
    PlaceholderBody(title = "Legg til mat")
}

@Composable
fun StatsScreen() {
    PlaceholderBody(title = "Statistikk")
}

@Composable
fun TrendScreen() {
    PlaceholderBody(title = "Trend")
}

@Composable
private fun PlaceholderBody(title: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
    }
}

private fun formatKcal(value: Double): String = value.toInt().toString()

@Preview(showBackground = true)
@Composable
private fun TodayPreview() {
    KaloriTheme { TodayScreen(onAddFood = {}, onOpenSettings = {}) }
}
