@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.report

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import net.techandgraphics.wastical.asGatewayIcon
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable
fun CompanyReportRecentPaymentItem(
  item: PaymentWithAccountAndMethodWithGatewayUiModel,
  onEvent: (CompanyReportEvent) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onEvent(CompanyReportEvent.Goto.Profile(item.account.id)) }
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Image(
      painter = painterResource(item.gateway.id.asGatewayIcon()),
      contentDescription = null,
      modifier = Modifier
        .clip(CircleShape)
        .size(32.dp),
    )
    Column(
      modifier = Modifier
        .padding(horizontal = 8.dp)
        .padding(end = 8.dp)
        .weight(1f),
    ) {
      Text(
        text = item.account.toFullName(),
        style = MaterialTheme.typography.bodyMedium,
      )
      Row {
        Text(
          text = item.gateway.name,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = " • ",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
          text = item.payment.createdAt.toZonedDateTime().defaultDateTime(),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
    Text(
      text = item.plan.fee.toAmount(),
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@Preview(showBackground = true)
@Composable
fun CompanyReportRecentPaymentItemPreview() {
  WasticalTheme {
    CompanyReportRecentPaymentItem(
      item = paymentWithAccountAndMethodWithGateway4Preview,
      onEvent = {},
    )
  }
}
