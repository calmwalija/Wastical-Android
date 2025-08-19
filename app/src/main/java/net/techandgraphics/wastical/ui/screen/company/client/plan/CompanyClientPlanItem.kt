package net.techandgraphics.wastical.ui.screen.company.client.plan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable
fun CompanyClientPlanItem(
  plan: PaymentPlanUiModel,
  onClick: (PaymentPlanUiModel) -> Unit,
) {

  OutlinedCard(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    onClick = { onClick(plan) }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {

      Column(
        modifier = Modifier
          .padding(start = 12.dp)
          .weight(1f)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = plan.name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          if (plan.active) {
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedCard(shape = CircleShape) {
              Text(
                text = "Current",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = plan.fee.toAmount(),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary,
        )
        Text(
          text = plan.period.name.lowercase(),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

  }
}

@Preview
@Composable
private fun CompanyClientPlanItemPreview() {
  WasticalTheme {
    Column(modifier = Modifier.padding(16.dp)) {
      CompanyClientPlanItem(plan = paymentPlan4Preview.copy(active = true)) {}
    }
  }
}
