package net.techandgraphics.wastemanagement.ui.screen.company.client.pending

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.defaultDateTime
import net.techandgraphics.wastemanagement.domain.model.relations.PaymentRequestWithAccountUiModel
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.paymentRequestWithAccount4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@Composable fun CompanyClientPendingPaymentView(
  entity: PaymentRequestWithAccountUiModel,
  onEvent: (CompanyClientPendingPaymentEvent) -> Unit,
) {

  val payment = entity.payment
  val account = entity.account

  Card(
    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    onClick = { onEvent(CompanyClientPendingPaymentEvent.Goto.BackHandler) }
  ) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {


      Box(modifier = Modifier.size(48.dp)) {
        Icon(
          painterResource(R.drawable.ic_upload_ready),
          contentDescription = null,
          modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(),
          tint = MaterialTheme.colorScheme.secondary
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

        Icon(
          painterResource(R.drawable.ic_upload_ready),
          contentDescription = null,
          modifier = Modifier.padding(horizontal = 16.dp),
          tint = MaterialTheme.colorScheme.primary
        )
        Text(
          text = entity.fee.times(payment.months).toAmount(),
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          modifier = Modifier.padding(end = 16.dp)
        )

      }
    }
  }
}


@Preview
@Composable
private fun CompanyClientPendingPaymentPreview() {
  WasteManagementTheme {
    CompanyClientPendingPaymentView(
      entity = paymentRequestWithAccount4Preview,
      onEvent = {}
    )
  }
}
