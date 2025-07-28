package net.techandgraphics.wastical.ui.screen.client.invoice

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.data.remote.payment.PaymentType
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.gatewayDrawableRes
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable fun ClientInvoiceView(
  model: PaymentWithAccountAndMethodWithGatewayUiModel,
  paymentPlan: PaymentPlanUiModel,
  onEvent: (ClientInvoiceEvent) -> Unit,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    onClick = { onEvent(ClientInvoiceEvent.Button.Invoice(model.payment)) }) {
    Row(
      modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {

      Image(
        painterResource(R.drawable.ic_invoice),
        contentDescription = null,
        modifier = Modifier
          .size(32.dp)
          .padding(2.dp)
      )

      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      ) {
        Text(
          text = model.gateway.name,
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          modifier = Modifier.padding(end = 8.dp)
        )
        Text(
          text = model.payment.createdAt.toZonedDateTime().defaultDateTime(),
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Image(
        painter = painterResource(
          id = gatewayDrawableRes[model.gateway.id.minus(1).toInt()]
        ),
        contentDescription = null,
        modifier = Modifier
          .alpha(if (PaymentType.Cash == PaymentType.valueOf(model.gateway.type)) 0f else 1f)
          .padding(horizontal = 4.dp)
          .clip(CircleShape)
          .size(28.dp),
        contentScale = ContentScale.Crop,
      )

      Box(
        modifier = Modifier.wrapContentWidth(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = model.coveredSize.times(paymentPlan.fee).toAmount(),
          style = MaterialTheme.typography.labelMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          textAlign = TextAlign.End
        )
        Text(
          text = 1_000_000.toAmount(),
          style = MaterialTheme.typography.labelMedium,
          modifier = Modifier.alpha(0f),
        )
      }

      IconButton(onClick = { onEvent(ClientInvoiceEvent.Button.Share(model.payment)) }) {
        Icon(
          Icons.Default.Share,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable fun ClientInvoiceViewPreview() {
  WasticalTheme {
    ClientInvoiceView(
      model = paymentWithAccountAndMethodWithGateway4Preview,
      paymentPlan = paymentPlan4Preview,
      onEvent = {}
    )
  }
}
