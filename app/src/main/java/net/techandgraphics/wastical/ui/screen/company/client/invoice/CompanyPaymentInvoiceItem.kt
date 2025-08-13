package net.techandgraphics.wastical.ui.screen.company.client.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithMonthsCoveredUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithMonthsCovered4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import java.time.Month

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
    Column(modifier = Modifier.padding(16.dp)) {
      // Accent bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp)
          .clip(MaterialTheme.shapes.small)
          .size(height = 4.dp, width = 0.dp)
          .background(
            Brush.horizontalGradient(
              listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = .6f),
                MaterialTheme.colorScheme.primary
              )
            )
          )
      )

      // Header
      Row(verticalAlignment = Alignment.CenterVertically) {
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Box(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = plan.fee.times(covered.size).toAmount(),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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

      // Summary line
      val preview = 3
      val monthHead = covered.take(preview)
      val monthSummary = monthHead.joinToString(", ") { cm ->
        Month.of(cm.month).name.take(3).lowercase().replaceFirstChar { it.titlecase() }
      }
      val remaining = covered.size - monthHead.size
      val summaryText = if (remaining > 0) "Months: $monthSummary +$remaining more" else "Months: $monthSummary"
      if (covered.isNotEmpty()) {
        Text(
          text = summaryText,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 12.dp)
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
