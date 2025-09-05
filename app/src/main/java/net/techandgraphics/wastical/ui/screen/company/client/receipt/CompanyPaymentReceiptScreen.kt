package net.techandgraphics.wastical.ui.screen.company.client.receipt

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.AccountInfoEvent
import net.techandgraphics.wastical.ui.screen.company.AccountInfoView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithMonthsCovered4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable
fun CompanyPaymentReceiptScreen(
  state: CompanyPaymentReceiptState,
  onEvent: (CompanyPaymentReceiptEvent) -> Unit,
) {

  when (state) {
    CompanyPaymentReceiptState.Loading -> LoadingIndicatorView()
    is CompanyPaymentReceiptState.Success ->

      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyPaymentReceiptEvent.Goto.BackHandler)
          }
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {
          item {
            Text(
              text = "Payment Receipts",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }

          item {
            AccountInfoView(state.account, state.demographic) { event ->
              when (event) {
                is AccountInfoEvent.Location ->
                  onEvent(CompanyPaymentReceiptEvent.Goto.Location(event.id))

                is AccountInfoEvent.Phone ->
                  onEvent(CompanyPaymentReceiptEvent.Button.Phone(event.contact))
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          items(state.payments) { payment ->
            if (payment.payment.status == PaymentStatus.Approved)
              CompanyPaymentReceiptItem(payment, state.plan, onEvent)
          }
        }
      }
  }


}


@Preview
@Composable
private fun CompanyPaymentReceiptScreenPreview() {
  WasticalTheme {
    CompanyPaymentReceiptScreen(
      state = CompanyPaymentReceiptState.Success(
        company = company4Preview,
        account = account4Preview,
        plan = paymentPlan4Preview,
        demographic = companyLocationWithDemographic4Preview,
        payments = (1..5).map { paymentWithMonthsCovered4Preview }
      ),
      onEvent = {}
    )
  }
}
