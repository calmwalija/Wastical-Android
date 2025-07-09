package net.techandgraphics.quantcal.ui.screen.company.client.invoice

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.defaultDate
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentWithMonthsCoveredUiModel
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.toZonedDateTime
import net.techandgraphics.quantcal.ui.screen.paymentPlan4Preview
import net.techandgraphics.quantcal.ui.screen.paymentWithMonthsCovered4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CompanyPaymentInvoiceItem(
  entity: PaymentWithMonthsCoveredUiModel,
  plan: PaymentPlanUiModel,
  onEvent: (CompanyPaymentInvoiceEvent) -> Unit,
) {


  val payment = entity.payment
  val covered = entity.covered

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    onClick = {
      onEvent(
        CompanyPaymentInvoiceEvent.Button.Invoice.Event(
          payment = payment,
          op = CompanyPaymentInvoiceEvent.Button.Invoice.Op.Preview
        )
      )
    }) {
    Row(
      modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {

      Image(
        painterResource(R.drawable.ic_invoice),
        contentDescription = null,
        modifier = Modifier
          .size(32.dp)
          .padding(2.dp)
      )

      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      ) {
        Text(
          text = payment.createdAt.toZonedDateTime().defaultDate(),
          style = MaterialTheme.typography.bodySmall
        )
        Text(
          text = plan.fee.times(covered.size).toAmount(),
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          modifier = Modifier.padding(end = 8.dp)
        )
      }
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
}


@Preview(showBackground = true)
@Composable
private fun CompanyPaymentInvoiceItemPreview() {
  QuantcalTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      CompanyPaymentInvoiceItem(
        entity = paymentWithMonthsCovered4Preview,
        plan = paymentPlan4Preview,
        onEvent = {}
      )
    }
  }
}
