package net.techandgraphics.wastemanagement.ui.screen.client.payment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentType
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.screen.appState
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientPaymentScreen(
  state: ClientPaymentState,
  channel: Flow<ClientPaymentChannel>,
  onEvent: (ClientPaymentEvent) -> Unit
) {

  val scrollState = rememberLazyListState()
  val hapticFeedback = LocalHapticFeedback.current
  val context = LocalContext.current
  var loading by remember { mutableStateOf(false) }

  val payByCash = state.state.paymentMethods
//    .filter { it.type == PaymentType.Cash }
    .any { it.isSelected.not() }

  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        when (event) {
          is ClientPaymentChannel.Pay.Failure ->
            onEvent(ClientPaymentEvent.Response(false, event.error.message))

          ClientPaymentChannel.Pay.Success ->
            onEvent(ClientPaymentEvent.Response(true, null))

        }
      }
    }
  }


  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(onClick = { onEvent(ClientPaymentEvent.GoTo.BackHandler) }) {
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
            state.state.paymentPlans.firstOrNull()?.let { paymentPlan ->

              val animatedSum by animateIntAsState(
                targetValue = state.numberOfMonths.times(paymentPlan.fee),
                animationSpec = tween(
                  delayMillis = 1_000,
                  durationMillis = 1_000,
                )
              )

              Text(
                text = animatedSum.toAmount(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(.4f)
              )
            }
          }
          Spacer(modifier = Modifier.weight(1f))
          AnimatedVisibility(state.screenshotAttached) {
            Button(
              enabled = payByCash.not() || (state.screenshotAttached && loading.not()),
              onClick = { onEvent(ClientPaymentEvent.Button.Pay); loading = true },
              modifier = Modifier.fillMaxWidth(.8f),
            ) {
              if (loading) Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = Color.White
                )
                Text(
                  text = "Please wait",
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(start = 8.dp)
                )
              } else Text(
                text = "Send Screenshot",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
              )
            }
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
            text = "Payment Screenshot",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
          )
        }

        item { ClientPaymentPlanView(state, onEvent) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { ClientPaymentMethodView(state, onEvent) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { ClientPaymentReferenceView(state, onEvent) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
      }
    }
  }


}


@Preview
@Composable
private fun ClientPaymentScreenPreview() {
  WasteManagementTheme {
    ClientPaymentScreen(
      state = ClientPaymentState(
        state = appState(LocalContext.current)
      ),
      channel = flow { },
      onEvent = {}
    )
  }
}
