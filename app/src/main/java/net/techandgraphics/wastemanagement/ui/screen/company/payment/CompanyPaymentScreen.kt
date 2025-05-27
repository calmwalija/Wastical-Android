package net.techandgraphics.wastemanagement.ui.screen.company.payment

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.ui.screen.appState
import net.techandgraphics.wastemanagement.ui.screen.paymentAccount4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPaymentScreen(
  state: CompanyPaymentState,
  channel: Flow<CompanyPaymentChannel>,
  onEvent: (CompanyPaymentEvent) -> Unit
) {


  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(onClick = { }) {
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
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))


      LazyColumn {
        items(state.payments) { payment ->
          CompanyPaymentView(
            paymentAccount = payment,
            imageLoader = state.state.imageLoader!!,
            channel = channel,
            onEvent = onEvent
          )
        }
      }

    }
  }


}


@Preview
@Composable
private fun PaymentScreenPreview() {
  WasteManagementTheme {
    CompanyPaymentScreen(
      state = CompanyPaymentState(
        state = appState(LocalContext.current),
        payments = listOf(paymentAccount4Preview, paymentAccount4Preview)
      ),
      channel = flow { },
      onEvent = {}
    )
  }
}
