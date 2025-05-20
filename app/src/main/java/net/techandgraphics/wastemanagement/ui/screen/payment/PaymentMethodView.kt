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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import net.techandgraphics.wastemanagement.AppUrl
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun PaymentMethodView(
  state: PaymentState,
  onEvent: (PaymentEvent) -> Unit
) {


  Column {

    Text(
      text = "Payment Method",
      modifier = Modifier.padding(8.dp)
    )

    state.paymentMethods.forEachIndexed { index, paymentMethod ->
      Card(
        colors = CardDefaults.elevatedCardColors(
          containerColor = if (index.mod(2) == 1) MaterialTheme.colorScheme.primary.copy(alpha = .15f) else {
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

          val asyncImagePainter = rememberAsyncImagePainter(
            model = AppUrl.FILE_URL.plus("gateway/").plus(paymentMethod.paymentGatewayId),
            imageLoader = state.imageLoader!!,
            placeholder = painterResource(R.drawable.ic_launcher_background)
          )

          Image(
            painter = asyncImagePainter,
            contentDescription = paymentMethod.name,
            modifier = Modifier
              .clip(CircleShape)
              .size(48.dp),
            contentScale = ContentScale.Crop
          )
          Column(
            modifier = Modifier
              .padding(horizontal = 16.dp)
              .weight(1f)
          ) {
            Text(
              text = paymentMethod.name,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Text(
              text = paymentMethod.account,
              color = MaterialTheme.colorScheme.primary
            )
          }
          IconButton(onClick = { onEvent(PaymentEvent.Button.TextToClipboard(paymentMethod.account)) }) {
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
      state = PaymentState(
        paymentMethods = listOf(paymentMethod, paymentMethod, paymentMethod),
        imageLoader = imageLoader(LocalContext.current)
      ),
      onEvent = {}
    )
  }
}
