package net.techandgraphics.wastical.ui.screen.company.client.pending

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.model.relations.PaymentRequestWithAccountUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.paymentRequestWithAccount4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme


@Composable fun CompanyClientPendingPaymentView(
  entity: PaymentRequestWithAccountUiModel,
  onEvent: (CompanyClientPendingPaymentEvent) -> Unit,
) {

  val payment = entity.payment
  val account = entity.account

  Card(
    modifier = Modifier.padding(vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
  ) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

      Icon(
        painterResource(R.drawable.ic_database_upload),
        contentDescription = null,
        modifier = Modifier
          .padding(4.dp)
          .size(42.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.error.copy(.1f))
          .padding(12.dp)
      )

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
  WasticalTheme {
    CompanyClientPendingPaymentView(
      entity = paymentRequestWithAccount4Preview,
      onEvent = {}
    )
  }
}
