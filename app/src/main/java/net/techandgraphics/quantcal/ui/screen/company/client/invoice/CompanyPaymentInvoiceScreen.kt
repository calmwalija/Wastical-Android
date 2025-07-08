package net.techandgraphics.quantcal.ui.screen.company.client.invoice

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
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.screen.account4Preview
import net.techandgraphics.quantcal.ui.screen.company.AccountInfoEvent
import net.techandgraphics.quantcal.ui.screen.company.AccountInfoView
import net.techandgraphics.quantcal.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.quantcal.ui.screen.paymentPlan4Preview
import net.techandgraphics.quantcal.ui.screen.paymentWithMonthsCovered4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@Composable
fun CompanyPaymentInvoiceScreen(
  state: CompanyPaymentInvoiceState,
  onEvent: (CompanyPaymentInvoiceEvent) -> Unit,
) {

  when (state) {
    CompanyPaymentInvoiceState.Loading -> LoadingIndicatorView()
    is CompanyPaymentInvoiceState.Success ->

      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyPaymentInvoiceEvent.Goto.BackHandler)
          }
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 8.dp)
        ) {
          item {
            Text(
              text = "Payment Invoice",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }

          item {
            AccountInfoView(state.account, state.demographic) { event ->
              when (event) {
                is AccountInfoEvent.Location ->
                  onEvent(CompanyPaymentInvoiceEvent.Goto.Location(event.id))

                is AccountInfoEvent.Phone ->
                  onEvent(CompanyPaymentInvoiceEvent.Button.Phone(event.contact))
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          items(state.payments) { payment ->
            if (payment.payment.status == PaymentStatus.Approved)
              CompanyPaymentInvoiceItem(payment, state.plan, onEvent)
          }
        }
      }
  }


}


@Preview
@Composable
private fun CompanyPaymentInvoiceScreenPreview() {
  QuantcalTheme {
    CompanyPaymentInvoiceScreen(
      state = CompanyPaymentInvoiceState.Success(
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
