package net.techandgraphics.wastical.ui.screen.company.client.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithMonthsCoveredUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toMonthName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithMonthsCovered4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CompanyPaymentInvoiceItem(
  entity: PaymentWithMonthsCoveredUiModel,
  plan: PaymentPlanUiModel,
  onEvent: (CompanyPaymentInvoiceEvent) -> Unit,
) {


  val payment = entity.payment
  val covered = entity.covered

  ElevatedCard(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.elevatedCardColors(),
    onClick = {
      onEvent(
        CompanyPaymentInvoiceEvent.Button.Invoice.Event(
          payment = payment,
          op = CompanyPaymentInvoiceEvent.Button.Invoice.Op.Preview
        )
      )
    }) {
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .padding(horizontal = 16.dp)
          .padding(top = 8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = .12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            painter = painterResource(R.drawable.ic_invoice),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
          Text(text = "Invoice", style = MaterialTheme.typography.bodyMedium)
          Text(
            text = payment.createdAt.toZonedDateTime().defaultDate(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
          )
        }

        Box(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = plan.fee.times(covered.size).toAmount(),
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = MaterialTheme.colorScheme.primary
          )
          IconButton(onClick = {
            onEvent(
              CompanyPaymentInvoiceEvent.Button.Invoice.Event(
                payment = payment,
                op = CompanyPaymentInvoiceEvent.Button.Invoice.Op.Share
              )
            )
          }) {
            Icon(
              Icons.Default.Share,
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }


      HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

      val preview = 3
      val monthHead = covered.take(preview)
      val monthSummary = monthHead.joinToString(", ") { it.month.toMonthName() }
      val remaining = covered.size - monthHead.size
      val summaryText =
        if (remaining > 0) "Months: $monthSummary +$remaining more" else "Months: $monthSummary"
      if (covered.isNotEmpty()) {
        Text(
          text = summaryText,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(16.dp),
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
        )
      }
    }
  }
}


@Preview(showBackground = true)
@Composable
private fun CompanyPaymentInvoiceItemPreview() {
  WasticalTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      CompanyPaymentInvoiceItem(
        entity = paymentWithMonthsCovered4Preview,
        plan = paymentPlan4Preview,
        onEvent = {}
      )
    }
  }
}
