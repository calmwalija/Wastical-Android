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
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import java.time.LocalDate
import java.time.ZoneId


@Composable
fun CompanyPaymentTimelineDateItem(
  item: PaymentDateTime,
  filters: Set<PaymentDateTime>,
  onEvent: (CompanyPaymentTimelineEvent) -> Unit,
) {
  OutlinedCard(
    modifier = Modifier.padding(8.dp),
    onClick = { onEvent(CompanyPaymentTimelineEvent.Button.DateTime(item)) },
    border = BorderStroke(
      CardDefaults.outlinedCardBorder().width,
      if (filters.contains(item).not())
        CardDefaults.cardColors().containerColor else MaterialTheme.colorScheme.primary
    )
  ) {
    Column(
      modifier = Modifier.padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = item.time.size.toString(),
        style = MaterialTheme.typography.titleLarge
      )
      Text(
        text = item.date.atStartOfDay(ZoneId.systemDefault()).defaultDate(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyPaymentTimelineDateItemPreview() {
  WasticalTheme {
    CompanyPaymentTimelineDateItem(
      item = PaymentDateTime(LocalDate.now(), listOf(2, 3, 4)),
      filters = setOf(),
      onEvent = {}
    )
  }
}
