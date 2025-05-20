package net.techandgraphics.wastemanagement.ui.screen.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun PaymentPlanView(
  state: PaymentState,
  onEvent: (PaymentEvent) -> Unit
) {

  Column {
    Text(
      text = "Payment Plan",
      modifier = Modifier.padding(8.dp)
    )
    Card(colors = CardDefaults.elevatedCardColors()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {

        Column(modifier = Modifier.weight(1f)) {

          Text(
            text = "Standard",
          )

          Text(
            text = "K10,000",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
          )

          Text(
            text = "Monthly",
            style = MaterialTheme.typography.bodySmall
          )

        }

        Card {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onEvent(PaymentEvent.Button.NumberOfMonths(false)) }) {
              Icon(Icons.AutoMirrored.TwoTone.KeyboardArrowLeft, null)
            }

            Text(
              text = "${state.numberOfMonths}",
              modifier = Modifier.padding(horizontal = 4.dp)
            )

            IconButton(onClick = { onEvent(PaymentEvent.Button.NumberOfMonths(true)) }) {
              Icon(Icons.AutoMirrored.TwoTone.KeyboardArrowRight, null)
            }
          }
        }

      }
    }
  }

}


@Preview(showBackground = true)
@Composable
private fun PaymentPlanViewPreview() {
  WasteManagementTheme {
    PaymentPlanView(
      state = PaymentState(),
      onEvent = {}
    )
  }
}
