package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.data.local.database.dashboard.street.StreetPaidThisMonthIndicator
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomeClientPaidStreetView(
  street: StreetPaidThisMonthIndicator,
) {

  val percentage =
    if (street.totalAccounts > 0) street.paidAccounts.toFloat() / street.totalAccounts else 0f

  OutlinedCard(shape = CircleShape, modifier = Modifier.padding(8.dp)) {
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
          text = street.areaName,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.bodySmall,
        )

        Text(
          text = street.streetName,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary
        )
        Text(
          text = "${street.paidAccounts} of ${street.totalAccounts}",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.bodySmall,
        )

      }

      CircularProgressIndicator(
        progress = { percentage },
        modifier = Modifier
          .padding(horizontal = 16.dp)
          .size(42.dp),
        strokeWidth = 8.dp
      )

    }
  }


}


@Preview(showBackground = true)
@Composable
private fun CompanyHomeClientPaidStreetViewPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      CompanyHomeClientPaidStreetView(
        street = companyHomeStateSuccess(LocalContext.current).streetPaidThisMonth.first(),
      )
    }
  }
}
