package net.techandgraphics.quantcal.ui.screen.company.payment.timeline


import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.defaultTime
import net.techandgraphics.quantcal.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.toFullName
import net.techandgraphics.quantcal.toInvoice
import net.techandgraphics.quantcal.toZonedDateTime
import net.techandgraphics.quantcal.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme


@Composable
fun PaymentTimelineItem(
  p0: PaymentWithAccountAndMethodWithGatewayUiModel,
  onEvent: (PaymentTimelineEvent) -> Unit,
) {

  val account = p0.account
  val payment = p0.payment
  val plan = p0.plan

  var contentHeight by remember { mutableIntStateOf(0) }
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .padding(start = 24.dp)
        .height(with(LocalDensity.current) { contentHeight.toDp() })
        .width(24.dp),
      contentAlignment = Alignment.Center
    ) {

      Box(
        modifier = Modifier
          .width(2.dp)
          .fillMaxHeight()
          .background(Color.Gray)
      )
      Box(
        modifier = Modifier
          .size(16.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary)
          .border(2.dp, Color.White, CircleShape)
      )
    }

    Column(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .clickable { onEvent(PaymentTimelineEvent.Goto.Profile(account.id)) }
        .padding(start = 24.dp)
        .onGloballyPositioned { layoutCoordinates ->
          contentHeight = layoutCoordinates.size.height
        }
        .padding(vertical = 16.dp)
        .weight(1f),
      verticalArrangement = Arrangement.Center
    ) {

      Text(
        text = account.toFullName(),
        color = MaterialTheme.colorScheme.primary
      )
      Text(
        text = plan.fee.times(p0.coveredSize).toAmount(),
        style = MaterialTheme.typography.bodyLarge
      )
      Text(
        text = "Invoice #: ${account.toInvoice(payment)}",
        style = MaterialTheme.typography.labelLarge,
      )
      Text(
        text = payment.createdAt.toZonedDateTime().defaultTime(),
        style = MaterialTheme.typography.labelLarge,
      )
    }

  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PaymentTimelineItemPreview() {
  QuantcalTheme {
    PaymentTimelineItem(
      p0 = paymentWithAccountAndMethodWithGateway4Preview,
      onEvent = {}
    )
  }
}
