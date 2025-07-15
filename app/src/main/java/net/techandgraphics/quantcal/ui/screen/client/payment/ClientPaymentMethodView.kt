package net.techandgraphics.quantcal.ui.screen.client.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.quantcal.gatewayDrawableRes
import net.techandgraphics.quantcal.ui.screen.paymentMethodWithGatewayAndPlan4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme


@Composable fun ClientPaymentMethodView(
  item: PaymentMethodWithGatewayAndPlanUiModel,
  onEvent: (ClientPaymentEvent) -> Unit,
) {
  Card(
    colors = CardDefaults.elevatedCardColors(),
    modifier = Modifier.padding(vertical = 8.dp),
    onClick = { onEvent(ClientPaymentEvent.Button.PaymentMethod(item)) },
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        painter = painterResource(
          id = gatewayDrawableRes[item.gateway.id.minus(1).toInt()]
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
          text = item.gateway.name,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = item.method.account,
          color = MaterialTheme.colorScheme.primary
        )
      }
      if (item.method.isSelected)
        Icon(
          Icons.Outlined.CheckCircle,
          contentDescription = null,
          modifier = Modifier
            .padding(horizontal = 8.dp)
            .size(32.dp),
          tint = MaterialTheme.colorScheme.primary
        )

    }
  }

}


@Preview(showBackground = true)
@Composable
private fun ClientPaymentMethodViewPreview() {
  QuantcalTheme {
    ClientPaymentMethodView(
      item = paymentMethodWithGatewayAndPlan4Preview,
      onEvent = {}
    )
  }
}
