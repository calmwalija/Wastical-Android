package net.techandgraphics.wastemanagement.ui.screen.transaction

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

@Composable fun InvoiceView(
  transactionUiModel: TransactionUiModel,
  onEvent: (TransactionEvent) -> Unit
) {
  Card(
    modifier = Modifier.padding(vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    onClick = {}
  ) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
      Icon(
        painterResource(R.drawable.ic_invoice), null,
        modifier = Modifier
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.secondary)
          .size(42.dp)
          .padding(8.dp),
        tint = MaterialTheme.colorScheme.onSecondary
      )
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      ) {
        Text(
          text = transactionUiModel.date.defaultDate(),
          style = MaterialTheme.typography.bodySmall
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
            painterResource(transactionUiModel.drawableRes), null,
            modifier = Modifier
              .padding(end = 4.dp)
              .clip(CircleShape)
              .size(16.dp)
              .alpha(.9f)
          )
          Text(text = 10_000.times(transactionUiModel.numberOfMonths).toAmount())
        }
      }
      Icon(
        painterResource(R.drawable.ic_file_open), null,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(16.dp))
    }

  }
}

@Preview(showBackground = true)
@Composable fun InvoiceViewPreview() {
  WasteManagementTheme {
    InvoiceView(transactionUiModel) { }
  }
}


private val transactionUiModel = TransactionUiModel(
  paymentMethod = "Airtel Money",
  numberOfMonths = 2,
  drawableRes = R.drawable.im_airtel_money_logo
)
