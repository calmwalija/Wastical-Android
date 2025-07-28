package net.techandgraphics.wastical.ui.screen.client.invoice

import androidx.compose.foundation.layout.padding
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.paymentMethodWithGatewayAndPlan4Preview
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientInvoiceScreen(
  state: ClientInvoiceState,
  channel: Flow<ClientInvoiceChannel>,
  onEvent: (ClientInvoiceEvent) -> Unit,
) {

  when (state) {
    ClientInvoiceState.Loading -> LoadingIndicatorView()
    is ClientInvoiceState.Success -> {

      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(ClientInvoiceEvent.GoTo.BackHandler)
          }
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {

          item {
            Text(
              text = "Paid Invoice Reports",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }

          items(state.invoices) { invoice ->
            ClientInvoiceView(invoice, state.paymentPlan, onEvent)
          }

        }

      }
    }
  }

}


@Preview(showBackground = true)
@Composable
private fun ClientInvoiceScreenPreview() {
  WasticalTheme {
    ClientInvoiceScreen(
      state = clientInvoiceStateSuccess(),
      channel = flow { },
      onEvent = {}
    )
  }
}

fun clientInvoiceStateSuccess() = ClientInvoiceState.Success(
  account = account4Preview,
  company = company4Preview,
  paymentPlan = paymentPlan4Preview,
  paymentMethods = listOf(paymentMethodWithGatewayAndPlan4Preview),
  invoices = listOf(paymentWithAccountAndMethodWithGateway4Preview),
)
