package net.techandgraphics.wastemanagement.ui.screen.client.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import coil.ImageLoader
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.calculate
import net.techandgraphics.wastemanagement.defaultDate
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.imageGatewayUrl
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.ui.screen.imageGatewayPainter
import net.techandgraphics.wastemanagement.ui.screen.imageLoader
import net.techandgraphics.wastemanagement.ui.screen.payment4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@Composable fun ClientHomePaymentView(
  payment: PaymentUiModel,
  imageLoader: ImageLoader?,
) {

  Card(
    modifier = Modifier.padding(vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
  ) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

      Box(modifier = Modifier.size(42.dp)) {

        val asyncImagePainter =
          imageGatewayPainter(imageGatewayUrl(1), imageLoader!!)
//          imageGatewayPainter(imageGatewayUrl(payment.paymentGatewayId), imageLoader!!)

        Icon(
          painterResource(R.drawable.ic_compare_arrows), null,
          modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSecondary)
            .fillMaxSize()
            .padding(8.dp),
          tint = MaterialTheme.colorScheme.secondary
        )
        Image(
          asyncImagePainter, null,
          modifier = Modifier
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.onSecondary, CircleShape)
            .size(18.dp)
            .align(Alignment.BottomEnd),
          contentScale = ContentScale.Crop
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)
        ) {
          Text(
            text = payment.createdAt.toZonedDateTime().defaultDate(),
            style = MaterialTheme.typography.bodySmall
          )
          Text(
            text = payment.calculate(),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis,
            modifier = Modifier.padding(end = 8.dp)
          )
        }

        Text(
          text = payment.status.name,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(end = 24.dp)
        )
      }
    }
  }
}


@Preview(showBackground = true)
@Composable fun ClientHomePaymentViewPreview() {
  WasteManagementTheme {
    ClientHomePaymentView(
      payment = payment4Preview,
      imageLoader = imageLoader(LocalContext.current),
    )
  }
}
