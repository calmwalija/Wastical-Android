package net.techandgraphics.wastical.ui.screen.company.client.pending

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.toast
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.AccountInfoEvent
import net.techandgraphics.wastical.ui.screen.company.AccountInfoView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.screen.paymentRequestWithAccount4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientPendingPaymentScreen(
  state: CompanyClientPendingPaymentState,
  onEvent: (CompanyClientPendingPaymentEvent) -> Unit,
) {
  when (state) {
    CompanyClientPendingPaymentState.Loading -> LoadingIndicatorView()
    is CompanyClientPendingPaymentState.Success -> {

      val context = LocalContext.current
      val hapticFeedback = LocalHapticFeedback.current
      LaunchedEffect(state.pending) {
        if (state.pending.isEmpty()) {
          onEvent(CompanyClientPendingPaymentEvent.Goto.BackHandler)
          hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
          context.toast("No pending proof of payments available")
        }
      }

      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyClientPendingPaymentEvent.Goto.BackHandler)
          }
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {
          item {
            Text(
              text = "Pending Proof Of Payments",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }


          item {
            AccountInfoView(state.account, state.demographic) { event ->
              when (event) {
                is AccountInfoEvent.Location ->
                  onEvent(CompanyClientPendingPaymentEvent.Goto.Location(event.id))

                is AccountInfoEvent.Phone ->
                  onEvent(CompanyClientPendingPaymentEvent.Button.Phone(event.contact))
              }
            }
          }

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
  WasticalTheme {
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
