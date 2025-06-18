package net.techandgraphics.wastemanagement.ui.screen.company.payment.verify

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.calculate
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.defaultDateTime
import net.techandgraphics.wastemanagement.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastemanagement.gatewayDrawableRes
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@Composable fun CompanyVerifyPaymentView(
  entity: PaymentWithAccountAndMethodWithGatewayUiModel,
  onEvent: (CompanyVerifyPaymentEvent) -> Unit,
) {

  val payment = entity.payment
  val account = entity.account


  Card(
    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    onClick = { onEvent(CompanyVerifyPaymentEvent.Goto.Profile(account.id)) }
  ) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

      Box(modifier = Modifier.size(48.dp)) {
        Image(
          painter = painterResource(gatewayDrawableRes[entity.gateway.id.minus(1).toInt()]),
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
          Text(
            text = payment.createdAt.toZonedDateTime().defaultDateTime(),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis
          )

        }

        when (payment.status) {
          PaymentStatus.Verifying -> R.drawable.ic_help
          PaymentStatus.Approved -> R.drawable.ic_check_circle
          else -> R.drawable.ic_close
        }.also {
          Icon(
            painterResource(it),
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 16.dp),
            tint = MaterialTheme.colorScheme.primary
          )
        }

        Text(
          text = payment.calculate(),
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          modifier = Modifier.padding(end = 16.dp)
        )

      }
    }
  }
}


@Preview(showBackground = true)
@Composable fun CompanyVerifyPaymentViewPreview() {
  WasteManagementTheme {
    CompanyVerifyPaymentView(
      entity = paymentWithAccountAndMethodWithGateway4Preview,
      onEvent = {}
    )
  }
}
