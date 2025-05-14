package net.techandgraphics.wastemanagement.ui.screen.payment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
  state: PaymentState,
  channel: Flow<PaymentChannel>,
  onEvent: (PaymentEvent) -> Unit
) {

  val scrollState = rememberLazyListState()
  val hapticFeedback = LocalHapticFeedback.current
  val context = LocalContext.current


  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        when (event) {
          else -> {
            TODO()
          }
        }
      }
    }
  }


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
    bottomBar = {
      Surface(shadowElevation = 10.dp, tonalElevation = 1.dp) {
        Row(
          modifier = Modifier
            .padding(vertical = 24.dp, horizontal = 16.dp)
            .fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Total",
              style = MaterialTheme.typography.titleSmall
            )
            Text(
              text = "K10,000",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
          Spacer(modifier = Modifier.weight(1f))
          Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(.8f),
          ) {
            Text(
              text = "Make Payment",
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondary
            )
          }
        }
      }
    },
    contentWindowInsets = ScaffoldDefaults
      .contentWindowInsets
      .exclude(WindowInsets.navigationBars)
      .exclude(WindowInsets.ime),
  ) {
    Box(modifier = Modifier.padding(it)) {
      LazyColumn(
        state = scrollState,
        modifier = Modifier
          .padding(horizontal = 16.dp)
      ) {
        item {
          Text(
            text = "Payment",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
          )
        }

        item { PaymentPlanView(state, onEvent) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { PaymentMethodView(state, onEvent) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { PaymentReferenceView(state, onEvent) }
        item { Spacer(modifier = Modifier.height(24.dp)) }


      }
    }
  }


}


@Preview
@Composable
private fun PaymentScreenPreview() {
  WasteManagementTheme {
    PaymentScreen(
      state = PaymentState(),
      channel = flow { },
      onEvent = {}
    )
  }
}
