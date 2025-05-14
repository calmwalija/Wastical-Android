package net.techandgraphics.wastemanagement.ui.screen.payment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.HorizontalRuleView
import net.techandgraphics.wastemanagement.ui.InputField
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun PaymentReferenceView(
  state: PaymentState,
  onEvent: (PaymentEvent) -> Unit
) {

  Column {
    Text(
      text = "Payment Reference",
      modifier = Modifier.padding(8.dp)
    )
    Card(
      colors = CardDefaults.elevatedCardColors(),
      onClick = {}
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(painterResource(R.drawable.ic_add_photo), null)
          Text(text = "Attach a screenshot")
        }

      }
    }


    HorizontalRuleView(content = {
      Text(
        text = "Or",
        modifier = Modifier.padding(16.dp)
      )
    })

    Card(colors = CardDefaults.elevatedCardColors()) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
      ) {
        InputField(
          painterResource = R.drawable.ic_password,
          value = "",
          prompt = "type transaction id",
          onValueChange = { },
        )
      }
    }

  }

}


@Preview(showBackground = true)
@Composable
private fun PaymentReferenceViewPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(32.dp)) {
      PaymentReferenceView(
        state = PaymentState(),
        onEvent = {}
      )
    }
  }
}
