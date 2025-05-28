package net.techandgraphics.wastemanagement.ui.screen.client.invoice

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.ui.screen.payment4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientInvoiceScreen(
  state: ClientInvoiceState,
  channel: Flow<ClientInvoiceChannel>,
  onEvent: (ClientInvoiceEvent) -> Unit
) {


  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
      }
    }
  }


  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(onClick = { onEvent(ClientInvoiceEvent.GoTo.BackHandler) }) {
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
        text = "Paid Invoice Reports",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))

      LazyColumn {
        items(state.invoices) { invoice ->
          ClientInvoiceView(invoice, state.state.paymentPlans, onEvent)
        }
      }

    }
  }
}


@Preview(showBackground = true)
@Composable
private fun ClientInvoiceScreenPreview() {
  WasteManagementTheme {
    ClientInvoiceScreen(
      state = ClientInvoiceState(
        invoices = (1..10)
          .toList()
          .mapIndexed { index, item ->
            payment4Preview.copy(
              id = index.toLong(),
              numberOfMonths = Random.nextInt(1, 5)
            )
          }
      ),
      channel = flow { },
      onEvent = {}
    )
  }
}
