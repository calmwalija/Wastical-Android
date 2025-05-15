package net.techandgraphics.wastemanagement.ui.screen.transaction

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.defaultDate
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.screen.home.model.TransactionUiModel
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@Composable fun TransactionView(
  transactionUiModel: TransactionUiModel,
  onEvent: (TransactionEvent) -> Unit
) {

  Column (modifier = Modifier.clickable{}){
    Row(
      modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {

      Image(
        painterResource(transactionUiModel.drawableRes), null,
        modifier = Modifier
          .clip(CircleShape)
          .size(42.dp)
          .alpha(.9f)
      )
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Column(
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 8.dp)
          ) {
            Text(
              text = transactionUiModel.paymentMethod,
              style = MaterialTheme.typography.bodySmall
            )
            Text(text = transactionUiModel.date.defaultDate())
            Text(
              text = "${transactionUiModel.numberOfMonths} months",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.secondary
            )

          }

          Text(
            text = 10_000.times(transactionUiModel.numberOfMonths).toAmount(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
          )


          Icon(
            Icons.AutoMirrored.Default.KeyboardArrowRight, null,
            modifier = Modifier.size(20.dp),
          )
        }
      }
    }
    HorizontalDivider()
  }
}


@Preview(showBackground = true)
@Composable fun TransactionViewPreview() {
  WasteManagementTheme {
    TransactionView(transactionUiModel = transactionUiModel) { }
  }
}

private val transactionUiModel = TransactionUiModel(
  paymentMethod = "Airtel Money",
  numberOfMonths = 2,
  drawableRes = R.drawable.im_airtel_money_logo
)
