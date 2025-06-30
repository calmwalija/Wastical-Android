package net.techandgraphics.wastemanagement.ui.screen.company.payment.location

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPaymentPerLocationView(
  location: Payment4CurrentLocationMonth,
  onEvent: (CompanyPaymentPerLocationEvent) -> Unit,
) {

  OutlinedCard(
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
          text = location.districtName,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.bodyMedium,
        )

        Text(
          text = location.areaName,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        Text(
          text = location.streetName,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.primary
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
