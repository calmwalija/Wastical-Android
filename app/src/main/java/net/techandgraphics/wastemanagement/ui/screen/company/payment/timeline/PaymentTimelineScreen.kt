package net.techandgraphics.wastemanagement.ui.screen.company.payment.timeline

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.defaultDateTime
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.time.ZonedDateTime

@Composable
fun PaymentTimelineScreen(
  state: PaymentTimelineState,
  onEvent: (PaymentTimelineEvent) -> Unit,
) {
  when (state) {
    PaymentTimelineState.Loading -> LoadingIndicatorView()
    is PaymentTimelineState.Success -> {
      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(PaymentTimelineEvent.Goto.BackHandler)
          }
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 4.dp)
        ) {
          item {
            Text(
              text = "Payments Timeline",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }
          state.payments.forEach { (createdAt, payments) ->
            stickyHeader {
              Text(
                text = createdAt,
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
              )
            }
            items(payments) { PaymentTimelineItem(it, onEvent) }
          }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun PaymentTimelineScreenPreview() {
  WasteManagementTheme {
    PaymentTimelineScreen(
      state = PaymentTimelineState.Success(
        company = company4Preview,
        payments = mapOf(
          ZonedDateTime.now().defaultDateTime() to
            (1..3).map { paymentWithAccountAndMethodWithGateway4Preview }
        )
      ),
      onEvent = {}
    )
  }
}
