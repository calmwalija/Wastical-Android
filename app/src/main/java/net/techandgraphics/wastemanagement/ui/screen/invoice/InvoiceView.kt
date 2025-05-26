package net.techandgraphics.wastemanagement.ui.screen.invoice

import androidx.compose.foundation.Image
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
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.calculateToTextAmount
import net.techandgraphics.wastemanagement.defaultDate
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.ui.screen.home.payment4Preview
import net.techandgraphics.wastemanagement.ui.screen.payment.paymentPlan4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class) @Composable fun InvoiceView(
  invoice: PaymentUiModel,
  paymentPlans: List<PaymentPlanUiModel>,
  onEvent: (InvoiceEvent) -> Unit
) {

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    onClick = { onEvent(InvoiceEvent.Button.Invoice(invoice)) }) {
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
          text = invoice.createdAt.toZonedDateTime().defaultDate(),
          style = MaterialTheme.typography.bodySmall
        )
        paymentPlans.forEach { paymentPlan ->
          Text(
            text = calculateToTextAmount(paymentPlan, invoice),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis,
            modifier = Modifier.padding(end = 8.dp)
          )
        }
      }

      IconButton(onClick = { onEvent(InvoiceEvent.Button.Share(invoice)) }) {
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
@Composable fun InvoiceViewPreview() {
  WasteManagementTheme {
    InvoiceView(
      invoice = payment4Preview,
      paymentPlans = listOf(paymentPlan4Preview)
    ) { }
  }
}
