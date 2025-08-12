package net.techandgraphics.wastical.ui.screen.company.payment.timeline


import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime


@Composable
fun PaymentTimelineDateItem(
  item: Pair<PaymentDateTime, List<PaymentWithAccountAndMethodWithGatewayUiModel>>,
  filters: Set<PaymentDateTime>,
  onEvent: (PaymentTimelineEvent) -> Unit,
) {
  OutlinedCard(
    modifier = Modifier.padding(8.dp),
    onClick = { onEvent(PaymentTimelineEvent.Button.Filter(item.first)) },
    border = BorderStroke(
      CardDefaults.outlinedCardBorder().width,
      if (filters.contains(item.first).not())
        CardDefaults.cardColors().containerColor else MaterialTheme.colorScheme.primary
    )
  ) {
    Column(
      modifier = Modifier.padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = item.second.size.toString(),
        style = MaterialTheme.typography.titleLarge
      )
      Text(
        text = item.first.date.atStartOfDay(ZoneId.systemDefault()).defaultDate(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PaymentTimelineDateItemPreview() {
  WasticalTheme {
    val zonedDateTime = ZonedDateTime.now()
    PaymentTimelineDateItem(
      item = Pair(
        PaymentDateTime(LocalDate.now(), zonedDateTime.toEpochSecond()),
        (1..3).map { paymentWithAccountAndMethodWithGateway4Preview }
      ),
      filters = setOf(),
      onEvent = {}
    )
  }
}
