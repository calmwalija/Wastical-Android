package net.techandgraphics.wastical.ui.screen.company.payment.verify

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.data.remote.payment.PaymentType
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.gatewayDrawableRes
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.Green
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable fun CompanyVerifyPaymentItem(
  modifier: Modifier = Modifier,
  model: PaymentWithAccountAndMethodWithGatewayUiModel,
  onEvent: (CompanyVerifyPaymentEvent) -> Unit,
) {

  val payment = model.payment
  val account = model.account

  Card(
    modifier = modifier.padding(vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    onClick = { onEvent(CompanyVerifyPaymentEvent.Goto.Profile(account.id)) }
  ) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

      Box(modifier = Modifier.size(48.dp)) {
        Image(
          painter = painterResource(gatewayDrawableRes[model.gateway.id.minus(1).toInt()]),
          contentDescription = null,
          modifier = Modifier
            .padding(4.dp)
            .clip(CircleShape)
            .fillMaxSize(),
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
            text = account.toFullName(),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
              text = payment.createdAt.toZonedDateTime().defaultDate(),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )

            Text(text = " • ")

            Text(
              text = model.plan.fee.times(model.coveredSize).toAmount(),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }


        if (model.gateway.type != PaymentType.Cash.name) {
          IconButton(onClick = { onEvent(CompanyVerifyPaymentEvent.Payment.Image(payment)) }) {
            Icon(
              painter = painterResource(R.drawable.ic_image),
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }

        Row(modifier = Modifier) {
          IconButton(
            onClick = { onEvent(CompanyVerifyPaymentEvent.Payment.Approve(payment)) },
            colors = IconButtonDefaults.iconButtonColors(
              containerColor = Green.copy(.2f)
            )
          ) {
            Icon(
              painterResource(R.drawable.ic_check),
              contentDescription = null,
              tint = Green
            )
          }
          IconButton(
            onClick = { onEvent(CompanyVerifyPaymentEvent.Payment.Deny(payment)) },
            colors = IconButtonDefaults.iconButtonColors(
              containerColor = MaterialTheme.colorScheme.error.copy(.2f)
            )
          ) {
            Icon(
              imageVector = Icons.Rounded.Clear,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.error
            )
          }
        }
      }
    }
  }
}


@Preview(showBackground = true)
@Composable fun CompanyVerifyPaymentItemPreview() {
  WasticalTheme {
    CompanyVerifyPaymentItem(
      model = paymentWithAccountAndMethodWithGateway4Preview,
      onEvent = {}
    )
  }
}
