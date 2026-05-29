package com.kalori.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kalori.app.ui.theme.KaloriTheme
import com.kalori.app.ui.theme.LocalRadii
import com.kalori.app.ui.theme.LocalSpacing
import com.kalori.app.ui.theme.TabularNumeric

/**
 * Shared component seam. Establishes the [com.kalori.app.ui.components] boundary; the design
 * system's real components (calorie ring, food row, etc.) land here so designer changes via
 * LiveEdit propagate from one place. These two stubs read tokens from theme CompositionLocals.
 */

/** A surface card using the shared radius token. */
@Composable
fun KaloriCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val radii = LocalRadii.current
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(radii.md),
    ) {
        Box(Modifier.padding(LocalSpacing.current.md)) { content() }
    }
}

/**
 * Placeholder for the calorie ring (the Today screen's primary element). Real implementation
 * with spring physics + idle breathing is feature-dev's job (see DESIGN_HANDOFF motion spec).
 */
@Composable
fun NumericStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    KaloriCard(modifier = modifier.size(140.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, style = TabularNumeric)
            Text(text = label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NumericStatPreview() {
    KaloriTheme {
        Box(Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
            NumericStat(value = "1 842", label = "kcal")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KaloriCardPreview() {
    KaloriTheme {
        Box(Modifier.padding(16.dp).clip(RoundedCornerShape(12.dp))) {
            KaloriCard { Text("Card") }
        }
    }
}
