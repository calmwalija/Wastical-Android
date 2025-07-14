package net.techandgraphics.quantcal.ui.screen.client.payment

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.quantcal.data.remote.payment.PaymentType
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.screen.account4Preview
import net.techandgraphics.quantcal.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.paymentMethodWithGatewayAndPlan4Preview
import net.techandgraphics.quantcal.ui.screen.paymentPlan4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientPaymentScreen(
  state: ClientPaymentState,
  channel: Flow<ClientPaymentChannel>,
  onEvent: (ClientPaymentEvent) -> Unit,
) {


  when (state) {
    ClientPaymentState.Loading -> LoadingIndicatorView()
    is ClientPaymentState.Success -> {

      val scrollState = rememberLazyListState()
      var loading by remember { mutableStateOf(false) }

      val paymentMethodCash = state.paymentMethods
        .filter { PaymentType.Cash == PaymentType.valueOf(it.gateway.type) }
        .any { it.method.isSelected }

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
          CompanyInfoTopAppBarView(state.company) {

          }
        },

        bottomBar = {
          BottomAppBar {

            val isPaymentMethodCash = state.paymentMethods
              .filter { it.gateway.type == PaymentType.Cash.name }
              .any { it.method.isSelected }

            Row(
              modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Total",
                  style = MaterialTheme.typography.labelMedium
                )

                val animatedSum by animateIntAsState(
                  targetValue = state.monthsCovered.times(state.paymentPlan.fee),
                  animationSpec = tween(
                    delayMillis = 1_000,
                    durationMillis = 1_000,
                  )
                )

                Text(
                  text = animatedSum.toAmount(),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.fillMaxWidth(.4f)
                )

              }
              Spacer(modifier = Modifier.weight(1f))
              Button(
                enabled = paymentMethodCash.not() ||
                  (state.screenshotAttached && loading.not()) ||
                  (isPaymentMethodCash && loading.not()),
                onClick = {

                  loading = true
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary.copy(.3f)
                ),
                modifier = Modifier.fillMaxWidth(.6f),
              ) {
                if (loading) Row(verticalAlignment = Alignment.CenterVertically) {
                  CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else Text(
                  text = "Pay",
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }

          }

        },

        ) {

        LazyColumn(
          state = scrollState,
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
        ) {

          item {
            Text(
              text = "Send Payment Screenshot",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }


          item {
            Text(
              text = "Payment Plan",
              modifier = Modifier.padding(8.dp)
            )
          }

          item { ClientPaymentPlanView(state, onEvent) }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Text(
              text = "Payment Method Used",
              modifier = Modifier.padding(8.dp)
            )
          }
          items(state.paymentMethods) { ClientPaymentMethodView(it, onEvent) }


          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Text(
              text = "Payment Reference",
              modifier = Modifier.padding(8.dp)
            )
          }
          item { ClientPaymentReferenceView(state, onEvent) }

          item { Spacer(modifier = Modifier.height(24.dp)) }
        }
      }
    }
  }
}


@Preview
@Composable
private fun ClientPaymentScreenPreview() {
  QuantcalTheme {
    ClientPaymentScreen(
      state = clientPaymentStateSuccess(),
      channel = flow { },
      onEvent = {}
    )
  }
}

fun clientPaymentStateSuccess() = ClientPaymentState.Success(
  account = account4Preview,
  company = company4Preview,
  paymentPlan = paymentPlan4Preview,
  paymentMethods = listOf(paymentMethodWithGatewayAndPlan4Preview),
)
