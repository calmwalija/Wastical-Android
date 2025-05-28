package net.techandgraphics.wastemanagement.ui.screen.client.payment

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import net.techandgraphics.wastemanagement.AppUrl
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.screen.appState
import net.techandgraphics.wastemanagement.ui.theme.Blue
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

    state.state.paymentMethods.forEachIndexed { index, paymentMethod ->
      Card(
        colors = CardDefaults.elevatedCardColors(),
        modifier = Modifier.padding(vertical = 8.dp),
        onClick = { onEvent(PaymentEvent.Button.PaymentMethod(paymentMethod)) },
        border = BorderStroke(2.dp, if (paymentMethod.isSelected) Blue else Color.Transparent)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {


          val imageUrl = AppUrl.FILE_URL.plus("gateway/").plus(paymentMethod.paymentGatewayId)
          val asyncImagePainter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
              .data(imageUrl)
              .diskCacheKey(imageUrl)
              .networkCachePolicy(CachePolicy.ENABLED)
              .crossfade(true)
              .build(),
            imageLoader = state.state.imageLoader!!,
            placeholder = painterResource(R.drawable.im_placeholder),
            error = painterResource(R.drawable.im_placeholder)
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
          if (paymentMethod.name.contains("Cash").not())
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
        state = appState(LocalContext.current)
      ),
      onEvent = {}
    )
  }
}
