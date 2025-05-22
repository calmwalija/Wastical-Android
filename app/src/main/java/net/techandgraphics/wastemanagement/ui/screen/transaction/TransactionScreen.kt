package net.techandgraphics.wastemanagement.ui.screen.transaction

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
  state: TransactionState,
  channel: Flow<TransactionChannel>,
  onEvent: (TransactionEvent) -> Unit
) {


  var showInvoice by remember { mutableStateOf(false) }
  val optionsMenu = listOf("Transactions", "Invoices")

  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
      }
    }
  }


  val brush = Brush.horizontalGradient(
    listOf(
      MaterialTheme.colorScheme.primary.copy(.7f),
      MaterialTheme.colorScheme.primary.copy(.8f),
      MaterialTheme.colorScheme.primary
    )
  )

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
        text = "Transactions",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
      )


      val showInvoiceState by remember { derivedStateOf { if (showInvoice) 0 else 1 } }
      val animateAsState by animateFloatAsState(targetValue = if (showInvoiceState == 1) 0f else 1f)


      Row {
        optionsMenu.forEachIndexed { index, menuItem ->
          Card(
            modifier = Modifier.padding(8.dp),
            onClick = { showInvoice = index == 1 },
            colors = CardDefaults.elevatedCardColors(containerColor = Color.Transparent)
          ) {
            Column(
              modifier = Modifier
                .padding(8.dp)
                .wrapContentWidth()
            ) {
              Text(
                text = if (showInvoice) menuItem.uppercase() else menuItem,
                modifier = Modifier,
                style = MaterialTheme.typography.titleSmall,
              )
              Box(
                modifier = Modifier
                  .padding(vertical = 2.dp)
                  .height(2.dp)
                  .fillMaxWidth(.12f)
                  .background(brush)
              )
            }
          }
        }
      }

//      LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
//        items(state.transactionUiModels) { transactionUiModel ->
//          if (showInvoice) InvoiceView(transactionUiModel, onEvent) else {
//            TransactionView(transactionUiModel, onEvent)
//          }
//        }
//      }

    }
  }
}


@Preview(showBackground = true)
@Composable
private fun TransactionScreenPreview() {
  WasteManagementTheme {
    TransactionScreen(
      state = TransactionState(),
      channel = flow { },
      onEvent = {}
    )
  }
}
