package net.techandgraphics.quantcal.ui.screen.company.payment.pay

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.gatewayDrawableRes
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CompanyMakePaymentMethodView(
  state: CompanyMakePaymentState.Success,
  onEvent: (CompanyMakePaymentEvent) -> Unit,
) {


  Column {

    Text(
      text = "Payment Method",
      modifier = Modifier.padding(8.dp)
    )

    Card(
      colors = CardDefaults.elevatedCardColors(),
      modifier = Modifier.padding(vertical = 8.dp),
    ) {
      state.paymentMethods.forEachIndexed { index, ofType ->
        Row(
          modifier = Modifier
            .clickable { onEvent(CompanyMakePaymentEvent.Button.PaymentMethod(ofType.method)) }
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {

          Image(
            painterResource(gatewayDrawableRes[ofType.gateway.id.minus(1).toInt()]),
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
              text = ofType.gateway.name,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Text(
              text = ofType.method.account,
              color = MaterialTheme.colorScheme.primary
            )
          }

          if (ofType.method.isSelected)
            Icon(
              Icons.Outlined.CheckCircle,
              contentDescription = null,
              modifier = Modifier.size(24.dp),
              tint = MaterialTheme.colorScheme.primary
            )

          Spacer(modifier = Modifier.width(8.dp))

        }
      }
    }

  }

}


@Preview(showBackground = true)
@Composable
private fun CompanyMakePaymentMethodViewPreview() {
  QuantcalTheme {
    CompanyMakePaymentMethodView(
      state = companySuccessState(LocalContext.current),
      onEvent = {}
    )
  }
}
