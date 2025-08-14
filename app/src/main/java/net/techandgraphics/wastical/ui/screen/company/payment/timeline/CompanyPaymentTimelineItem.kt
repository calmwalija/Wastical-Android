package net.techandgraphics.wastical.ui.screen.company.payment.timeline


import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.asGatewayIcon
import net.techandgraphics.wastical.defaultTime
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme


@Composable
fun CompanyPaymentTimelineItem(
  modifier: Modifier = Modifier,
  item: PaymentWithAccountAndMethodWithGatewayUiModel,
  onEvent: (CompanyPaymentTimelineEvent) -> Unit,
) {

  val account = item.account
  val payment = item.payment
  val plan = item.plan

  var contentHeight by remember { mutableIntStateOf(0) }
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .padding(start = 24.dp)
        .height(with(LocalDensity.current) { contentHeight.toDp() })
        .size(32.dp),
      contentAlignment = Alignment.Center
    ) {

      Box(
        modifier = Modifier
          .width(2.dp)
          .fillMaxHeight()
          .background(Color.Gray)
      )
      Image(
        painter = painterResource(item.gateway.id.asGatewayIcon()),
        contentDescription = null,
        modifier = Modifier
          .clip(CircleShape)
          .size(32.dp)
          .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
      )
    }

    Row(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .clickable { onEvent(CompanyPaymentTimelineEvent.GotoInvoice(payment.id)) }
        .padding(start = 16.dp)
        .onGloballyPositioned { layoutCoordinates ->
          contentHeight = layoutCoordinates.size.height
        }
        .padding(vertical = 12.dp)
        .padding(end = 8.dp)
        .weight(1f),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = account.toFullName(),
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
            text = payment.createdAt.toZonedDateTime().defaultTime(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      Text(
        text = plan.fee.toAmount(),
        style = MaterialTheme.typography.bodyMedium,
      )

      Spacer(modifier = Modifier.width(8.dp))

    }

  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyPaymentTimelineItemPreview() {
  WasticalTheme {
    CompanyPaymentTimelineItem(
      item = paymentWithAccountAndMethodWithGateway4Preview,
      onEvent = {}
    )
  }
}
