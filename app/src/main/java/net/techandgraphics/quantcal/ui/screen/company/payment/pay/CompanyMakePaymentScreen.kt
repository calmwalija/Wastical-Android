package net.techandgraphics.quantcal.ui.screen.company.payment.pay

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
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
import net.techandgraphics.quantcal.toast
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.screen.account4Preview
import net.techandgraphics.quantcal.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.quantcal.ui.screen.imageLoader
import net.techandgraphics.quantcal.ui.screen.paymentMethodWithGateway4Preview
import net.techandgraphics.quantcal.ui.screen.paymentPlan4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyMakePaymentScreen(
  state: CompanyMakePaymentState,
  channel: Flow<CompanyMakePaymentChannel>,
  onEvent: (CompanyMakePaymentEvent) -> Unit,
) {

  val scrollState = rememberLazyListState()
  var loading by remember { mutableStateOf(false) }
  var isSuccess by remember { mutableStateOf(false) }
  val context = LocalContext.current


  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        loading = false
        when (event) {
          CompanyMakePaymentChannel.Pay.Success -> isSuccess = true
        }
      }
    }
  }


  if (isSuccess) {
    context.toast("Your payment request is submitted.")
    onEvent(CompanyMakePaymentEvent.GoTo.BackHandler)
    isSuccess = false
  }

  when (state) {
    CompanyMakePaymentState.Loading -> LoadingIndicatorView()
    is CompanyMakePaymentState.Success ->
      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyMakePaymentEvent.GoTo.BackHandler)
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
                  targetValue = state.numberOfMonths.times(state.paymentPlan.fee),
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
                enabled = (state.screenshotAttached && loading.not()) || (isPaymentMethodCash && loading.not()),
                onClick = {
                  onEvent(CompanyMakePaymentEvent.Button.RecordPayment)
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
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 8.dp)
        ) {


          item {
            Text(
              text = "Record Payment",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }

          item { CompanyMakePaymentClientView(state.demographic, state.account, onEvent) }
          item { Spacer(modifier = Modifier.height(24.dp)) }
          item { CompanyMakePaymentPlanView(state, onEvent) }
          item { Spacer(modifier = Modifier.height(24.dp)) }
          item { CompanyMakePaymentMethodView(state, onEvent) }
          item { Spacer(modifier = Modifier.height(24.dp)) }
          item {
            if (state.paymentMethods
                .filter { it.gateway.type == PaymentType.Cash.name }
                .any { it.method.isSelected.not() }
            ) CompanyMakePaymentReferenceView(state, onEvent)
          }
          item { Spacer(modifier = Modifier.height(24.dp)) }
        }
      }
  }
}


@Preview
@Composable
private fun CompanyMakePaymentScreenPreview() {
  QuantcalTheme {
    CompanyMakePaymentScreen(
      state = companySuccessState(LocalContext.current),
      channel = flow { }
    ) {}
  }
}


fun companySuccessState(context: Context) = CompanyMakePaymentState.Success(
  account = account4Preview,
  paymentPlan = paymentPlan4Preview,
  paymentMethods = listOf(
    paymentMethodWithGateway4Preview,
    paymentMethodWithGateway4Preview
  ),
  imageLoader = imageLoader(context),
  company = company4Preview,
  demographic = companyLocationWithDemographic4Preview
)
