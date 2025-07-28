package net.techandgraphics.wastical.ui.screen.company.payment.verify

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company.payment.verify.status.CompanyVerifyStatusWaitingView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyVerifyPaymentScreen(
  state: CompanyVerifyPaymentState,
  onEvent: (CompanyVerifyPaymentEvent) -> Unit,
) {

  when (state) {
    CompanyVerifyPaymentState.Loading -> LoadingIndicatorView()
    is CompanyVerifyPaymentState.Success -> {
      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyVerifyPaymentEvent.Goto.BackHandler)
          }
        },
        contentWindowInsets = WindowInsets.safeGestures
      ) {

        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {
          item {
            Text(
              text = "Verify Payments",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }

          item {
            val statuses = remember { listOf(PaymentStatus.Approved, PaymentStatus.Waiting) }

            if (state.pending.isEmpty()) onEvent(
              CompanyVerifyPaymentEvent.Button.Status(
                PaymentStatus.Approved
              )
            )

            AnimatedVisibility(state.pending.isNotEmpty()) {
              SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                  .fillMaxWidth()
              ) {
                statuses.forEachIndexed { index, status ->
                  SegmentedButton(
                    selected = state.ofType == status,
                    onClick = { onEvent(CompanyVerifyPaymentEvent.Button.Status(status)) },
                    icon = {},
                    colors = SegmentedButtonDefaults.colors(
                      activeContainerColor = MaterialTheme.colorScheme.primary.copy(.4f),
                      activeContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = SegmentedButtonDefaults.itemShape(
                      index = index,
                      count = statuses.count()
                    )
                  ) {
                    BadgedBox(badge = {
                      Badge {
                        Text(
                          text =
                            when (status) {
                              PaymentStatus.Waiting -> state.pending.size
                              else -> state.payments.size
                            }.toString(),
                          style = MaterialTheme.typography.bodySmall
                        )
                      }
                    }) {
                      Text(
                        text = status.name,
                        modifier = Modifier.padding(8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                      )
                    }

                  }
                }
              }
            }
          }


          item { Spacer(modifier = Modifier.height(16.dp)) }

          when (state.ofType) {
            PaymentStatus.Approved ->
              items(state.payments, key = { it.payment.id }) { entity ->
                CompanyVerifyPaymentView(
                  entity = entity,
                  onEvent = onEvent
                )
              }

            else ->
              items(state.pending, key = { it.payment.id }) { entity ->
                CompanyVerifyStatusWaitingView(
                  entity = entity,
                  onEvent = onEvent
                )
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
