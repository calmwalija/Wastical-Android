package net.techandgraphics.wastical.ui.screen.company.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import kotlin.math.roundToInt


@Composable
fun CompanyHomeLocationPieChart(
  data: List<Payment4CurrentLocationMonth>,
  modifier: Modifier = Modifier,
) {
  val total = data.sumOf { it.totalAccounts }.coerceAtLeast(1)
  val slices = remember(data) { data.map { it.streetName to it.totalAccounts } }
  val basePalette = listOf(
    MaterialTheme.colorScheme.primary,
    MaterialTheme.colorScheme.secondary,
    MaterialTheme.colorScheme.tertiary,
    MaterialTheme.colorScheme.error,
    MaterialTheme.colorScheme.primary.copy(alpha = .8f),
    MaterialTheme.colorScheme.secondary.copy(alpha = .8f),
    MaterialTheme.colorScheme.tertiary.copy(alpha = .8f),
  )
  val palette = remember(data.size, basePalette) { generatePaletteFromBase(data.size, basePalette) }
  val surfaceColor = MaterialTheme.colorScheme.surface

  Card(colors = CardDefaults.elevatedCardColors(), modifier = modifier) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Location distribution",
        style = MaterialTheme.typography.titleMedium,
      )
      Text(
        text = "Share of accounts by location",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(180.dp)) {
          var start = -90f
          slices.forEachIndexed { index, pair ->
            val value = pair.second
            val sweep = 360f * (value.toFloat() / total.toFloat())
            if (sweep > 0f) {
              drawArc(
                color = palette[index],
                startAngle = start,
                sweepAngle = sweep,
                useCenter = true
              )
            }
            start += sweep
          }
          drawCircle(
            color = surfaceColor,
            radius = size.minDimension * .28f,
            style = Fill
          )
        }

        Column(
          modifier = Modifier
            .padding(start = 16.dp)
            .height(180.dp)
            .weight(1f)
        ) {
          LazyColumn {
            itemsIndexed(slices) { index, (label, value) ->
              val percent = (value * 100f / total).roundToInt()
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
              ) {
                Spacer(
                  modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(palette[index])
                )
                Text(
                  text = "$label • $percent%",
                  style = MaterialTheme.typography.bodySmall,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }
          }
        }
      }
    }
  }
}


private fun generatePaletteFromBase(count: Int, base: List<Color>): List<Color> {
  if (count <= 0 || base.isEmpty()) return emptyList()
  return List(count) { i -> base[i % base.size] }
}


@Preview(showBackground = true)
@Composable
private fun CompanyHomeLocationPieChartPreview() {
  WasticalTheme {
    val sample = listOf(
      Payment4CurrentLocationMonth(1, "Street A", "Area 1", 40, 24, "Region", "District", 0f, 0f),
      Payment4CurrentLocationMonth(2, "Street B", "Area 1", 25, 18, "Region", "District", 0f, 0f),
      Payment4CurrentLocationMonth(3, "Street C", "Area 2", 15, 12, "Region", "District", 0f, 0f),
      Payment4CurrentLocationMonth(4, "Street D", "Area 3", 20, 9, "Region", "District", 0f, 0f),
    )
    CompanyHomeLocationPieChart(sample)
  }
}
