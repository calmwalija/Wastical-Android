package net.techandgraphics.wastical.ui.screen.client.payment

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.ui.theme.WasticalTheme

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
        text = if (state.screenshotAttached) "Proof Of Payment Attached" else {
          "Attach Proof Of Payment"
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun ClientPaymentReferenceViewPreview() {
  WasticalTheme {
    Box(modifier = Modifier.padding(32.dp)) {
      ClientPaymentReferenceView(
        state = clientPaymentStateSuccess(),
        onEvent = {}
      )
    }
  }
}
