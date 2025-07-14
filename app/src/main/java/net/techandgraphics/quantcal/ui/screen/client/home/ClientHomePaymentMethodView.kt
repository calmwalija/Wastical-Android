package net.techandgraphics.quantcal.ui.screen.client.home

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.data.remote.payment.PaymentType
import net.techandgraphics.quantcal.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.quantcal.gatewayDrawableRes
import net.techandgraphics.quantcal.ui.screen.paymentMethodWithGatewayAndPlan4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ClientPaymentMethodView(
  model: PaymentMethodWithGatewayAndPlanUiModel,
  onEvent: (ClientHomeEvent) -> Unit,
) {

  Card(
    colors = CardDefaults.elevatedCardColors(),
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {

      Image(
        painter = painterResource(
          id = gatewayDrawableRes[model.gateway.id.minus(1).toInt()]
        ),
        contentDescription = null,
        modifier = Modifier
          .clip(CircleShape)
          .size(48.dp),
        contentScale = ContentScale.Crop,
      )
      Column(
        modifier = Modifier
          .padding(horizontal = 16.dp)
          .weight(1f)
      ) {
        Text(
          text = model.gateway.name,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = model.method.account,
          color = MaterialTheme.colorScheme.primary
        )
      }
      if (PaymentType.valueOf(model.gateway.type) != PaymentType.Cash)
        IconButton(
          onClick = { onEvent(ClientHomeEvent.Button.Payment.TextToClipboard(model.method.account)) }) {
          Icon(
            painter = painterResource(R.drawable.ic_content_copy),
            contentDescription = null
          )
        }
    }
  }
}


@Preview(showBackground = true)
@Composable
private fun ClientPaymentMethodViewPreview() {
  QuantcalTheme {
    ClientPaymentMethodView(
      model = paymentMethodWithGatewayAndPlan4Preview,
      onEvent = {}
    )
  }
}
