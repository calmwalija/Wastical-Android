package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomeClientPaidStreetView(
  location: Payment4CurrentLocationMonth,
  onEvent: (CompanyHomeEvent) -> Unit,

  ) {

  var targetValue by remember { mutableFloatStateOf(0f) }
  val animateAsFloat by animateFloatAsState(
    targetValue = targetValue,
    animationSpec = tween(durationMillis = Random.nextInt(7_000, 10_000))
  )
  var rowHeightPx by remember { mutableIntStateOf(0) }
  val progressText = String.format(locale = Locale.getDefault(), "%.1f", animateAsFloat * 100)

  LaunchedEffect(location) {
    targetValue =
      (location.paidAccounts.toFloat() / location.totalAccounts.toFloat()).coerceIn(0f, 1f)
  }

  OutlinedCard(
    shape = CircleShape, modifier = Modifier.padding(8.dp),
    onClick = { onEvent(CompanyHomeEvent.Goto.LocationOverview(location.streetId)) }) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Box(
        modifier = Modifier
          .height(with(LocalDensity.current) { rowHeightPx.toDp() })
          .fillMaxWidth(fraction = animateAsFloat)
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
      )

      Row(
        modifier = Modifier
          .onGloballyPositioned { coordinates ->
            rowHeightPx = coordinates.size.height
          }
          .padding(horizontal = 24.dp, vertical = 16.dp)
          .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier
            .padding(start = 8.dp)
            .weight(1f)
        ) {
          Text(
            text = location.areaName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
          )
          Text(
            text = location.streetName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
          )
        }

        Text(
          text = "${progressText}%",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(horizontal = 8.dp)
        )

        Text(
          text = "${location.paidAccounts} of ${location.totalAccounts}",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.bodySmall,
        )

      }
    }
  }


}


@Preview(showBackground = true)
@Composable
private fun CompanyHomeClientPaidStreetViewPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      CompanyHomeClientPaidStreetView(
        location = companyHomeStateSuccess().payment4CurrentLocationMonth.first(),
        onEvent = {  },
      )
    }
  }
}
