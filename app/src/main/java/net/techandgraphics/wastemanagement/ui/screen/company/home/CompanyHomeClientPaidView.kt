package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.data.local.database.dashboard.account.PaidThisMonthIndicator
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomeClientPaidView(
  paidThisMonth: PaidThisMonthIndicator,
) {

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .padding(16.dp)
        .size(200.dp),
      contentAlignment = Alignment.Center
    ) {

      CircularProgressIndicator(
        progress = { paidThisMonth.percentPaid },
        strokeCap = StrokeCap.Round,
        strokeWidth = 20.dp,
        modifier = Modifier.fillMaxSize(),
      )

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
      ) {

        Text(
          text = "${paidThisMonth.accountsPaidThisMonth} of ${paidThisMonth.totalAccounts}",
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.primary,
        )
        Text(
          text = "Paid This Month",
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.bodyMedium,
        )
        Text(
          text = paidThisMonth.totalPaid.toAmount(),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold
        )
      }

    }
  }


}


@Preview(showBackground = true)
@Composable
private fun CompanyHomeClientPaidViewPreview() {
  WasteManagementTheme {
    CompanyHomeClientPaidView(
      paidThisMonth = companyHomeStateSuccess(LocalContext.current).paidThisMonth,
    )
  }
}
