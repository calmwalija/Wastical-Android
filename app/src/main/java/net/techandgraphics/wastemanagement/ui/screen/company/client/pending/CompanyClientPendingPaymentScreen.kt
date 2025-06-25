package net.techandgraphics.wastemanagement.ui.screen.company.client.pending

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company.AccountInfoView
import net.techandgraphics.wastemanagement.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentRequestWithAccount4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientPendingPaymentScreen(
  state: CompanyClientPendingPaymentState,
  onEvent: (CompanyClientPendingPaymentEvent) -> Unit,
) {
  when (state) {
    CompanyClientPendingPaymentState.Loading -> LoadingIndicatorView()
    is CompanyClientPendingPaymentState.Success -> {

      LaunchedEffect(state.pending) {
        if (state.pending.isEmpty()) onEvent(CompanyClientPendingPaymentEvent.Goto.BackHandler)
      }

      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyClientPendingPaymentEvent.Goto.BackHandler)
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
              text = "Pending Payments",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }

          item { AccountInfoView(state.account, state.demographic) }

          item { Spacer(modifier = Modifier.height(16.dp)) }


          items(state.pending, key = { it.payment.id }) { payment ->
            CompanyClientPendingPaymentView(payment, onEvent)
          }

        }
      }
    }
  }

}


@Preview
@Composable
private fun CompanyClientPendingPaymentScreenPreview() {
  WasteManagementTheme {
    CompanyClientPendingPaymentScreen(
      state = CompanyClientPendingPaymentState.Success(
        company = company4Preview,
        account = account4Preview,
        pending = listOf(paymentRequestWithAccount4Preview),
        demographic = companyLocationWithDemographic4Preview
      ),
      onEvent = {}
    )
  }
}
