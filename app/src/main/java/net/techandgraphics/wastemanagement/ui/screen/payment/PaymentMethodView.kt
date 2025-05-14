package net.techandgraphics.wastemanagement.ui.screen.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


data class PaymentMethod(
  val logo: Int,
  val name: String,
  val account: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun PaymentMethodView(
  state: PaymentState,
  onEvent: (PaymentEvent) -> Unit
) {

  val paymentMethods = listOf(
    PaymentMethod(
      logo = R.drawable.im_airtel_money_logo,
      name = "Airtel Money",
      account = "+265-999-00-11-22"
    ),
    PaymentMethod(
      logo = R.drawable.im_national_bank_logo,
      name = "National Bank",
      account = "10011223344"
    )
  )

  Column {

    Text(
      text = "Payment Method",
      modifier = Modifier.padding(8.dp)
    )

    paymentMethods.forEachIndexed { index, payment ->

      Card(
        colors = CardDefaults.elevatedCardColors(
          containerColor = if (index == 0) MaterialTheme.colorScheme.primary.copy(alpha = .15f) else {
            CardDefaults.elevatedCardColors().containerColor
          }

        ),
        modifier = Modifier.padding(vertical = 8.dp),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Image(
            painterResource(payment.logo), null,
            modifier = Modifier
              .clip(CircleShape)
              .size(44.dp),
            contentScale = ContentScale.Crop
          )
          Column(
            modifier = Modifier
              .padding(horizontal = 16.dp)
              .weight(1f)
          ) {
            Text(text = payment.name)
            Text(
              text = payment.account,
              color = MaterialTheme.colorScheme.primary
            )
          }


          IconButton(onClick = {}) {
            Icon(painterResource(R.drawable.ic_content_copy), null)
          }
        }
      }
    }

  }

}


@Preview(showBackground = true)
@Composable
private fun PaymentMethodViewPreview() {
  WasteManagementTheme {
    PaymentMethodView(
      state = PaymentState(),
      onEvent = {}
    )
  }
}
