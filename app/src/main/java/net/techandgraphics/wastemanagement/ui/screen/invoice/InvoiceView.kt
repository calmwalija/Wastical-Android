package net.techandgraphics.wastemanagement.ui.screen.invoice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import coil.ImageLoader
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.calculateAmount
import net.techandgraphics.wastemanagement.defaultDate
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.imageGatewayUrl
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.ui.screen.home.payment
import net.techandgraphics.wastemanagement.ui.screen.imageGatewayPainter
import net.techandgraphics.wastemanagement.ui.screen.payment.imageLoader
import net.techandgraphics.wastemanagement.ui.screen.payment.paymentPlan
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@Composable fun InvoiceView(
  payment: PaymentUiModel,
  paymentPlan: PaymentPlanUiModel,
  imageLoader: ImageLoader?,
  onEvent: (InvoiceEvent) -> Unit
) {
  Card(
    modifier = Modifier.padding(vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.outlinedCardColors(),
    onClick = { onEvent(InvoiceEvent.Button.Tap(payment)) }
  ) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

      Box(modifier = Modifier.size(42.dp)) {

        val asyncImagePainter =
          imageGatewayPainter(imageGatewayUrl(payment.paymentMethodId), imageLoader!!)

        Image(
          painterResource(R.drawable.ic_invoice), null,
          modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSecondary)
            .fillMaxSize()
            .padding(10.dp),
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
          text = calculateAmount(paymentPlan, payment),
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          modifier = Modifier.padding(end = 8.dp)
        )
      }

      IconButton(onClick = { onEvent(InvoiceEvent.Button.Share(payment)) }) {
        Icon(
          Icons.Default.Share,
          contentDescription = null,
          modifier = Modifier
            .size(24.dp),
        )
      }

      Spacer(modifier = Modifier.width(16.dp))
    }

  }
}

@Preview(showBackground = true)
@Composable fun InvoiceViewPreview() {
  WasteManagementTheme {
    InvoiceView(
      payment = payment,
      paymentPlan = paymentPlan,
      imageLoader = imageLoader(LocalContext.current),
    ) { }
  }
}
