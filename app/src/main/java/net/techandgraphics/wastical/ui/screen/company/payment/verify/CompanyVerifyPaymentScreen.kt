package net.techandgraphics.wastical.ui.screen.company.payment.verify

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SnackbarThemed
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.Muted
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyVerifyPaymentScreen(
  state: CompanyVerifyPaymentState,
  onEvent: (CompanyVerifyPaymentEvent) -> Unit,
) {

  val contentColor = MaterialTheme.colorScheme.onSecondaryContainer
  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  when (state) {
    CompanyVerifyPaymentState.Loading -> LoadingIndicatorView()
    is CompanyVerifyPaymentState.Success -> {
      Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) { SnackbarThemed(it) } },
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyVerifyPaymentEvent.Goto.BackHandler)
          }
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {
          item {
            BasicText(
              text = "Verify Proof Of Payments",
              style = MaterialTheme.typography.headlineSmall,
              color = ColorProducer { contentColor },
              modifier = Modifier.padding(end = 16.dp),
              maxLines = 1,
              autoSize = TextAutoSize.StepBased(
                maxFontSize = MaterialTheme.typography.headlineSmall.fontSize
              )
            )
          }

          item {
            Text(
              text = "Which client are you looking for ?",
              style = MaterialTheme.typography.bodyMedium,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              color = Muted
            )
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          item { CompanyVerifyClientSearchView(state = state, onEvent) }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          items(
            items = state.payments,
            key = { payment -> payment.payment.id }) { model ->
            CompanyVerifyPaymentItem(
              modifier = Modifier.animateItem(),
              model = model
            ) { event ->
              when (event) {
                is CompanyVerifyPaymentEvent.Payment.Approve ->
                  coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                      message = "Please confirm payment approval ?",
                      actionLabel = "Approve",
                      duration = SnackbarDuration.Short
                    ).also { result ->
                      when (result) {
                        SnackbarResult.Dismissed -> Unit
                        SnackbarResult.ActionPerformed -> onEvent(event)
                      }
                    }
                  }

                is CompanyVerifyPaymentEvent.Payment.Deny ->
                  coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                      message = "Please confirm payment denial ?",
                      actionLabel = "Deny",
                      duration = SnackbarDuration.Short
                    ).also { result ->
                      when (result) {
                        SnackbarResult.Dismissed -> Unit
                        SnackbarResult.ActionPerformed -> onEvent(event)
                      }
                    }
                  }

                is CompanyVerifyPaymentEvent.Payment.Image -> Unit

                else -> onEvent(event)
              }
            }
          }
        }
      }
    }
  }
}


@Preview
@Composable
private fun CompanyVerifyScreenPreview() {
  WasticalTheme {
    CompanyVerifyPaymentScreen(
      state = CompanyVerifyPaymentState.Success(
        company = company4Preview,
        payments = (1..3)
          .map { listOf(paymentWithAccountAndMethodWithGateway4Preview) }
          .toList()
          .flatten()
      ),
      onEvent = {}
    )
  }
}
