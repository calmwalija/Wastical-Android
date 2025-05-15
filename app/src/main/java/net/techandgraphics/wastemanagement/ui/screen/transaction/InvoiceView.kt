package net.techandgraphics.wastemanagement.ui.screen.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

  Column(modifier = Modifier.clickable {}) {
    Row(
      modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Column(
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 8.dp)
          ) {
            Text(
              text = 10_000.times(transactionUiModel.numberOfMonths).toAmount(),
              style = MaterialTheme.typography.bodyMedium
            )
            Text(
              text = "#${transactionUiModel.id.toString().drop(4)}",
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = transactionUiModel.date.defaultDate(),
              style = MaterialTheme.typography.bodySmall
            )
          }
          Box(contentAlignment = Alignment.Center) {
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .background(
                  brush = Brush.horizontalGradient(
                    listOf(
                      MaterialTheme.colorScheme.primary.copy(.7f),
                      MaterialTheme.colorScheme.primary.copy(.8f),
                      MaterialTheme.colorScheme.primary
                    )
                  )
                )
            )
            Icon(
              painterResource(R.drawable.ic_file_open), null,
              modifier = Modifier.size(20.dp),
              tint = Color.White
            )
          }
        }
      }
    }
    HorizontalDivider()
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
