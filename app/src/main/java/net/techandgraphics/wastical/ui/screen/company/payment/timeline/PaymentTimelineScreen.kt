package net.techandgraphics.wastical.ui.screen.company.payment.timeline

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.payment4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import java.time.LocalDate
import java.time.ZoneId
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
          modifier = Modifier.padding(16.dp)
        ) {
          item {
            Text(
              text = "Payments Timeline",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }
          item {
            LazyRow {
              items(state.payments.toList()) {
                PaymentTimelineDateItem(it, state.filters, onEvent)
              }
            }
          }
          state.filteredPayments.forEach { (dateTime, payments) ->
            stickyHeader {
              Text(
                text = dateTime.date.atStartOfDay(ZoneId.systemDefault()).defaultDate(),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
              )
            }
            items(payments, key = { key -> key.payment.id }) { item ->
              PaymentTimelineItem(
                modifier = Modifier.animateItem(),
                item = item,
                onEvent = onEvent
              )
            }

          }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun PaymentTimelineScreenPreview() {
  WasticalTheme {
    val zonedDateTime = ZonedDateTime.now()
    val payments = mapOf(
      PaymentDateTime(LocalDate.now(), zonedDateTime.toEpochSecond()) to
        (1L..3L).map { index ->
          paymentWithAccountAndMethodWithGateway4Preview
            .copy(payment = payment4Preview.copy(id = index))
        }
    )

    PaymentTimelineScreen(
      state = PaymentTimelineState.Success(
        company = company4Preview,
        payments = payments,
        filteredPayments = payments
      ),
      onEvent = {}
    )
  }
}
