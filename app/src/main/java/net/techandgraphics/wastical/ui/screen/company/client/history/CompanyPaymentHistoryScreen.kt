package net.techandgraphics.wastical.ui.screen.company.client.history

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SnackbarThemed
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.AccountInfoEvent
import net.techandgraphics.wastical.ui.screen.company.AccountInfoView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithMonthsCovered4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPaymentHistoryScreen(
  state: CompanyPaymentHistoryState,
  onEvent: (CompanyPaymentHistoryEvent) -> Unit,
) {

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  when (state) {
    CompanyPaymentHistoryState.Loading -> LoadingIndicatorView()
    is CompanyPaymentHistoryState.Success ->

      Scaffold(
        snackbarHost = {
          SnackbarHost(hostState = snackbarHostState) { SnackbarThemed(it) }
        },
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyPaymentHistoryEvent.Goto.BackHandler)
          }
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {
          item {
            Text(
              text = "Payment History",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }

          item {
            AccountInfoView(state.account, state.demographic) { event ->
              when (event) {
                is AccountInfoEvent.Location ->
                  onEvent(CompanyPaymentHistoryEvent.Goto.Location(event.id))

                is AccountInfoEvent.Phone ->
                  onEvent(CompanyPaymentHistoryEvent.Button.Phone(event.contact))
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          items(state.payments) { payment ->
            CompanyPaymentHistoryItem(
              entity = payment,
              plan = state.plan,
              onEvent = { event ->
                when (event) {
                  is CompanyPaymentHistoryEvent.Button.Delete ->
                    scope.launch {
                      snackbarHostState.showSnackbar(
                        message = "Are you sure you want to delete this payment ?",
                        actionLabel = "Confirm",
                        duration = SnackbarDuration.Short
                      ).also { result ->
                        when (result) {
                          SnackbarResult.Dismissed -> Unit
                          SnackbarResult.ActionPerformed -> onEvent(event)
                        }
                      }
                    }

                  else -> onEvent(event)
                }
              }
            )
          }
        }
      }
  }

}


@Preview
@Composable
private fun CompanyPaymentHistoryScreenPreview() {
  WasticalTheme {
    CompanyPaymentHistoryScreen(
      state = CompanyPaymentHistoryState.Success(
        company = company4Preview,
        account = account4Preview,
        plan = paymentPlan4Preview,
        demographic = companyLocationWithDemographic4Preview,
        payments = (1..4).map { paymentWithMonthsCovered4Preview }
      ),
      onEvent = {}
    )
  }
}
