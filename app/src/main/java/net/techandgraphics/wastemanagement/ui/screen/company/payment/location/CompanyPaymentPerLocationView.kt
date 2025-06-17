package net.techandgraphics.wastemanagement.ui.screen.company.payment.location

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPaymentPerLocationView(
  location: Payment4CurrentLocationMonth,
  onEvent: (CompanyPaymentPerLocationEvent) -> Unit,
) {

  val targetValue =
    (location.paidAccounts.toFloat() / location.totalAccounts.toFloat()).coerceIn(0f, 1f)
  val progressText = String.format(locale = Locale.getDefault(), "%.1f", targetValue * 100)

  OutlinedCard(
    shape = CircleShape,
    modifier = Modifier.padding(8.dp),
    onClick = { onEvent(CompanyPaymentPerLocationEvent.Goto.LocationOverview(location.streetId)) }
  ) {
    Row(
      modifier = Modifier
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

      Badge(
        modifier = Modifier.padding(horizontal = 8.dp),
        containerColor = MaterialTheme.colorScheme.onSecondary
      ) {
        Text(
          text = "${progressText}%",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(2.dp)
        )
      }

      Text(
        text = "${location.paidAccounts} of ${location.totalAccounts}",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodySmall,
      )

    }
  }


}


@Preview(showBackground = true)
@Composable
private fun CompanyPaymentPerLocationPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      CompanyPaymentPerLocationView(
        location = companyPaymentPerLocationStateSuccess().payment4CurrentLocationMonth.first(),
        onEvent = {},
      )
    }
  }
}
