package net.techandgraphics.wastical.ui.screen.company.client.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.capitalize
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithMonthsCoveredUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithMonthsCovered4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import java.time.Month

@Composable fun CompanyPaymentHistoryItem(
  modifier: Modifier = Modifier,
  entity: PaymentWithMonthsCoveredUiModel,
  plan: PaymentPlanUiModel,
  onEvent: (CompanyPaymentHistoryEvent) -> Unit,
) {

  val payment = entity.payment
  var showMonths by remember { mutableIntStateOf(0) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
  ) {
    ElevatedCard(
      shape = MaterialTheme.shapes.large,
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = plan.fee.times(entity.covered.size).toAmount(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
          )
          StatusPill(text = payment.status.name)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = payment.createdAt.toZonedDateTime().defaultDateTime(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .size(4.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.onSurfaceVariant)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = entity.covered.size.toString().plus(" months"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      HorizontalDivider()

      // Months covered section
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        val previewCount = 3
        val totalMonths = entity.covered.size
        val shouldCollapse = totalMonths > previewCount
        val visibleItems =
          if (shouldCollapse && showMonths == 0) entity.covered.take(previewCount) else entity.covered

        visibleItems.forEach { monthData ->
          MonthBullet(text = Month.of(monthData.month).name.capitalize().plus(" ${monthData.year}"))
          Spacer(modifier = Modifier.height(6.dp))
        }

        if (shouldCollapse) {
          Text(
            text = if (showMonths == 0) "Show all (" + (totalMonths - previewCount) + ")" else "Hide",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
              .padding(top = 4.dp)
              .clip(MaterialTheme.shapes.small)
              .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = .4f),
                MaterialTheme.shapes.small
              )
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .clickable { showMonths = if (showMonths == 0) 1 else 0 }
          )
        }
      }

      // Status info strip (no actions)
      when (payment.status) {
        PaymentStatus.Verifying -> {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Awaiting review",
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        else -> Unit
      }
    }
  }
}


@Preview(showBackground = true)
@Composable fun CompanyPaymentHistoryItemPreview() {
  WasticalTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      CompanyPaymentHistoryItem(
        entity = paymentWithMonthsCovered4Preview,
        plan = paymentPlan4Preview,
        onEvent = {}
      )
    }
  }
}

@Composable private fun StatusPill(text: String) {
  OutlinedCard(shape = CircleShape) {
    Text(
      text = text,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.primary
    )
  }
}

@Composable private fun MonthBullet(text: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
