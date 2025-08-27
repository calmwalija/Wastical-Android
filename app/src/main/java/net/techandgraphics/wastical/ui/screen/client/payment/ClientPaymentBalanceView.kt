package net.techandgraphics.wastical.ui.screen.client.payment

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.toPluralMonth
import net.techandgraphics.wastical.toShortMonthName
import net.techandgraphics.wastical.ui.theme.WasticalTheme


@Composable fun ClientPaymentBalanceView(
  state: ClientPaymentState.Success,
  context: Context,
) {
  val remainingAfterPayment =
    (state.monthsOutstanding - state.monthsCovered).coerceAtLeast(0)
  val quantity = context.toPluralMonth(remainingAfterPayment)
  val due = context.toPluralMonth(state.monthsOutstanding)
  Card(
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.errorContainer,
      contentColor = MaterialTheme.colorScheme.onErrorContainer,
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Outstanding balance",
          style = MaterialTheme.typography.titleMedium,
        )
        Text(
          text = "$due due",
          style = MaterialTheme.typography.bodyMedium,
        )
        if (state.outstandingMonths.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            state.outstandingMonths.forEach { ym ->
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .background(MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.1f))
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = ym.month.toShortMonthName().plus(" ${ym.year}"),
                  style = MaterialTheme.typography.labelSmall,
                )
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        if (remainingAfterPayment > 0) {
          Text(
            text = "Remaining after this payment: $quantity",
            style = MaterialTheme.typography.labelLarge,
          )
        } else {
          Text(
            text = "Fully covered with this payment",
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }


}


@Preview(showBackground = true)
@Composable
private fun ClientPaymentBalancePreview() {
  WasticalTheme {
    ClientPaymentBalanceView(
      state = clientPaymentStateSuccess(),
      context = LocalContext.current,
    )
  }
}
