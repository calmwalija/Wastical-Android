package net.techandgraphics.wastemanagement.ui.screen.company.payment.pay

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.data.remote.ApiResult
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentType
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.imageLoader
import net.techandgraphics.wastemanagement.ui.screen.paymentMethod4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyMakePaymentScreen(
  state: CompanyMakePaymentState,
  channel: Flow<CompanyMakePaymentChannel>,
  onEvent: (CompanyMakePaymentEvent) -> Unit,
) {

  val scrollState = rememberLazyListState()
  var loading by remember { mutableStateOf(false) }
  var showResultDialog by remember { mutableStateOf(false) }
  var isSuccess by remember { mutableStateOf(false) }
  var error by remember { mutableStateOf<ApiResult.Error?>(null) }


  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        showResultDialog = true
        loading = false
        isSuccess = when (event) {
          is CompanyMakePaymentChannel.Pay.Failure -> {
            error = event.error
            false
          }

          CompanyMakePaymentChannel.Pay.Success -> true
        }
      }
    }
  }

  if (showResultDialog) {
    Dialog(
      onDismissRequest = { showResultDialog = false },
      properties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
      )
    ) {
      CompanyRecordPaymentResponseDialog(
        account = (state as CompanyMakePaymentState.Success).account,
        isSuccess = isSuccess,
        error = error
      ) {
        showResultDialog = false
        error = null
        onEvent(CompanyMakePaymentEvent.GoTo.BackHandler)
      }
    }
  }


  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(onClick = { onEvent(CompanyMakePaymentEvent.GoTo.BackHandler) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
          }
        },
        modifier = Modifier.shadow(0.dp),
        colors = TopAppBarDefaults.topAppBarColors()
      )
    },
    bottomBar = {
      if (state is CompanyMakePaymentState.Success) {

        val isPaymentMethodCash = state.paymentMethods
          .filter { it.type == PaymentType.Cash }
          .any { it.isSelected }


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

              val animatedSum by animateIntAsState(
                targetValue = state.numberOfMonths.times(state.paymentPlan.fee),
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
            Spacer(modifier = Modifier.weight(1f))
            Button(
              enabled = (state.screenshotAttached && loading.not()) || (isPaymentMethodCash && loading.not()),
              onClick = {
                onEvent(CompanyMakePaymentEvent.Button.RecordPayment)
                loading = true
              },
              modifier = Modifier.fillMaxWidth(.8f),
            ) {
              if (loading) Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Text(
                  text = "Please wait ",
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(start = 8.dp)
                )
              } else Text(
                text = "Make Payment",
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

    if (state is CompanyMakePaymentState.Success) {
      Box(modifier = Modifier.padding(it)) {
        LazyColumn(
          state = scrollState,
          modifier = Modifier
            .padding(horizontal = 16.dp)
        ) {


          item {
            Text(
              text = "Record Payment",
              style = MaterialTheme.typography.headlineMedium,
              modifier = Modifier.padding(vertical = 16.dp)
            )
          }

          item { CompanyMakePaymentClientView(state.account, onEvent) }
          item { Spacer(modifier = Modifier.height(24.dp)) }
          item { CompanyMakePaymentPlanView(state, onEvent) }
          item { Spacer(modifier = Modifier.height(24.dp)) }
          item { CompanyMakePaymentMethodView(state, onEvent) }
          item { Spacer(modifier = Modifier.height(24.dp)) }
          item {
            if (state.paymentMethods
                .filter { it.type == PaymentType.Cash }
                .any { it.isSelected.not() }
            ) CompanyMakePaymentReferenceView(state, onEvent)
          }
          item { Spacer(modifier = Modifier.height(24.dp)) }
        }
      }
    }
  }

}


@Preview
@Composable
private fun CompanyMakePaymentScreenPreview() {
  WasteManagementTheme {
    CompanyMakePaymentScreen(
      state = companySuccessState(LocalContext.current),
      channel = flow { }
    ) {}
  }
}


fun companySuccessState(context: Context) = CompanyMakePaymentState.Success(
  account = account4Preview,
  paymentPlan = paymentPlan4Preview,
  paymentMethods = listOf(paymentMethod4Preview, paymentMethod4Preview),
  imageLoader = imageLoader(context)
)
