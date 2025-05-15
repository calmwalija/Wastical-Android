package net.techandgraphics.wastemanagement.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.defaultDate
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.screen.home.model.TransactionUiModel
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeTransactionView(
  onEvent: (HomeEvent) -> Unit
) {

  OutlinedCard(modifier = Modifier.fillMaxWidth()) {
    transactionUiModels.forEach {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Image(
          painterResource(it.drawableRes), null,
          modifier = Modifier
            .clip(CircleShape)
            .size(32.dp)
            .alpha(.9f)
        )
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)
        ) {
          Text(text = 10_000.times(it.numberOfMonths).toAmount())
          Text(
            text = it.date.defaultDate(),
            style = MaterialTheme.typography.bodySmall
          )
        }

        IconButton(onClick = {}) {
          Icon(
            painterResource(R.drawable.ic_file_open), null,
            modifier = Modifier.size(20.dp),
          )
        }
        Spacer(modifier = Modifier.width(4.dp))
      }
      HorizontalDivider()
    }
  }


}

@Preview(showBackground = true)
@Composable
private fun HomeTransactionViewPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      HomeTransactionView(
        onEvent = {}
      )
    }
  }
}


private val transactionUiModels = listOf(
  TransactionUiModel(
    paymentMethod = "Airtel Money",
    numberOfMonths = 2,
    drawableRes = R.drawable.im_airtel_money_logo
  ),
  TransactionUiModel(
    paymentMethod = "National Bank",
    numberOfMonths = 3,
    drawableRes = R.drawable.im_national_bank_logo
  ),
  TransactionUiModel(
    paymentMethod = "TNM Mpamba",
    numberOfMonths = 4,
    drawableRes = R.drawable.im_tnm_mpamba
  ),
)
