package net.techandgraphics.wastemanagement.ui.screen.company.client.pending

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company.AccountInfoView
import net.techandgraphics.wastemanagement.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
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
    is CompanyClientPendingPaymentState.Success ->

      Scaffold(
        topBar = {
          TopAppBar(
            title = { CompanyInfoTopAppBarView(state.company) },
            navigationIcon = {
              IconButton(onClick = { CompanyClientPendingPaymentEvent.Goto.BackHandler }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
              }
            },
            modifier = Modifier.shadow(0.dp),
            colors = TopAppBarDefaults.topAppBarColors()
          )
        },
      ) {
        Column(modifier = Modifier.padding(it)) {

          Text(
            text = "Pending Payments",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
          )

          AccountInfoView(state.account)
          Spacer(modifier = Modifier.height(24.dp))

          LazyColumn(contentPadding = PaddingValues(16.dp)) {
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
        pending = listOf(paymentRequestWithAccount4Preview)
      ),
      onEvent = {}
    )
  }
}