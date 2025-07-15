package net.techandgraphics.quantcal.ui.screen.client.payment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ClientPaymentReferenceView(
  state: ClientPaymentState.Success,
  onEvent: (ClientPaymentEvent) -> Unit,
) {

  Card(
    colors = CardDefaults.elevatedCardColors(),
    onClick = { onEvent(ClientPaymentEvent.Button.AttachScreenshot) },
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
    ) {
      if (state.screenshotAttached)
        Icon(
          Icons.Outlined.CheckCircle,
          contentDescription = null,
          modifier = Modifier.size(32.dp),
          tint = MaterialTheme.colorScheme.primary
        ) else {
        Icon(
          painterResource(R.drawable.ic_add_photo), null,
          modifier = Modifier.size(32.dp)
        )
      }
      Text(
        modifier = Modifier.padding(4.dp),
        text = if (state.screenshotAttached) "Payment Screenshot Attached" else {
          "Attach Payment Screenshot"
        }
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun ClientPaymentReferenceViewPreview() {
  QuantcalTheme {
    Box(modifier = Modifier.padding(32.dp)) {
      ClientPaymentReferenceView(
        state = clientPaymentStateSuccess(),
        onEvent = {}
      )
    }
  }
}
