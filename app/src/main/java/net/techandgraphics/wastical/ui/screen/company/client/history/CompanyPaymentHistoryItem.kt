package net.techandgraphics.wastical.ui.screen.company.client.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
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
  entity: PaymentWithMonthsCoveredUiModel,
  plan: PaymentPlanUiModel,
  onEvent: (CompanyPaymentHistoryEvent) -> Unit,
) {

  val payment = entity.payment
  var contentHeight by remember { mutableIntStateOf(0) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
  ) {
    Text(
      text = payment.createdAt.toZonedDateTime().defaultDateTime(),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      fontWeight = FontWeight.Bold,
      style = MaterialTheme.typography.titleMedium
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
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

      Column(modifier = Modifier.padding(16.dp)) {

        Row(
          modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
              contentHeight = layoutCoordinates.size.height
            }
            .fillMaxWidth()
        ) {

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = plan.fee.times(entity.covered.size).toAmount(),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.primary
            )
            Column {
              entity.covered.forEachIndexed { index, monthData ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = index.plus(1).toString().plus(". "),
                    style = MaterialTheme.typography.bodyMedium,
                  )
                  Text(
                    text = Month.of(monthData.month).name.capitalize().plus(" ${monthData.year}"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
            }
          }

          val statusIcon = when (payment.status) {
            PaymentStatus.Verifying -> R.drawable.ic_help
            PaymentStatus.Approved -> R.drawable.ic_check_circle
            else -> R.drawable.ic_close
          }

          if (statusIcon == 1234)
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                enabled = false,
                onClick = { onEvent(CompanyPaymentHistoryEvent.Button.Delete(payment.id)) }) {
                Icon(
                  imageVector = Icons.Outlined.Delete,
                  contentDescription = null
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
            }

        }
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
