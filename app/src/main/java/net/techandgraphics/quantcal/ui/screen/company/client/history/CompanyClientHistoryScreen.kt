package net.techandgraphics.quantcal.ui.screen.company.client.history

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import net.techandgraphics.quantcal.ui.screen.company.AccountInfoView
import net.techandgraphics.quantcal.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.quantcal.ui.screen.paymentPlan4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientHistoryScreen(
  state: CompanyClientHistoryState,
  onEvent: (CompanyClientHistoryEvent) -> Unit,
) {

  when (state) {
    CompanyClientHistoryState.Loading -> LoadingIndicatorView()
    is CompanyClientHistoryState.Success ->

      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {

          }
        },
        contentWindowInsets = WindowInsets.safeGestures
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp)
        ) {
          item {
            Text(
              text = "Payment History",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }

          item { AccountInfoView(state.account, state.demographic) }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          items(state.payments) { payment ->
            when (payment.payment.status) {
              PaymentStatus.Approved -> CompanyClientHistoryInvoiceView(
                payment,
                state.plan,
                onEvent
              )

              else -> CompanyClientHistoryView(
                entity = payment,
                onEvent = onEvent
              )
            }
          }

        }
      }
  }

}


@Preview
@Composable
private fun CompanyClientHistoryScreenPreview() {
  QuantcalTheme {
    CompanyClientHistoryScreen(
      state = CompanyClientHistoryState.Success(
        company = company4Preview,
        account = account4Preview,
        plan = paymentPlan4Preview,
        demographic = companyLocationWithDemographic4Preview
      ),
      onEvent = {}
    )
  }
}
