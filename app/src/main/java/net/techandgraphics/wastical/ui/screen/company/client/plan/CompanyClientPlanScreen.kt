package net.techandgraphics.wastical.ui.screen.company.client.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toast
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SnackbarThemed
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.AccountInfoEvent
import net.techandgraphics.wastical.ui.screen.company.AccountInfoView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientPlanScreen(
  state: CompanyClientPlanState,
  channel: Flow<CompanyClientPlanChannel>,
  onEvent: (CompanyClientPlanEvent) -> Unit,
) {
  when (state) {
    CompanyClientPlanState.Loading -> LoadingIndicatorView()
    is CompanyClientPlanState.Success -> {

      val hapticFeedback = LocalHapticFeedback.current
      val context = LocalContext.current
      val lifecycleOwner = LocalLifecycleOwner.current
      val snackbarHostState = remember { SnackbarHostState() }
      val scope = rememberCoroutineScope()

      LaunchedEffect(key1 = channel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
          channel.collectLatest { event ->
            when (event) {
              is CompanyClientPlanChannel.Error -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                context.toast(event.error.message)
              }

              CompanyClientPlanChannel.Success -> {
                context.toast("Plan change request submitted")
                onEvent(CompanyClientPlanEvent.Goto.BackHandler)
              }

              CompanyClientPlanChannel.Processing -> Unit
            }
          }
        }
      }



      Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) { SnackbarThemed(it) } },
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyClientPlanEvent.Goto.BackHandler)
          }
        },
        bottomBar = {
          BottomAppBar {
            Row(
              modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.Center
            ) {
              Button(
                modifier = Modifier.weight(1f),
                onClick = {
                  scope.launch {
                    snackbarHostState.showSnackbar(
                      message = "Please confirm the account payment plan change request for this client ?",
                      actionLabel = "Confirm",
                      duration = SnackbarDuration.Short
                    ).also { result ->
                      when (result) {
                        SnackbarResult.Dismissed -> Unit
                        SnackbarResult.ActionPerformed -> onEvent(CompanyClientPlanEvent.Button.Submit)
                      }
                    }
                  }
                }) {
                Box { Text(text = "Change Payment Plan") }
              }

              Spacer(modifier = Modifier.width(8.dp))

              OutlinedButton(onClick = { onEvent(CompanyClientPlanEvent.Goto.BackHandler) }) {
                Box { Text(text = "Cancel") }
              }
            }
          }
        }
      ) {

        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {
          item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
              Text(
                text = "Choose a plan",
                style = MaterialTheme.typography.headlineSmall,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Select a billing plan for this client account.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Spacer(modifier = Modifier.height(12.dp))
          }


          item {
            AccountInfoView(state.account, state.demographic) { event ->
              when (event) {
                is AccountInfoEvent.Location ->
                  onEvent(CompanyClientPlanEvent.Goto.Location(event.id))

                is AccountInfoEvent.Phone ->
                  onEvent(CompanyClientPlanEvent.Button.Phone(event.contact))
              }
            }
          }
          item { Spacer(modifier = Modifier.height(16.dp)) }

          item {
            ElevatedCard(
              modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
              shape = MaterialTheme.shapes.large
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = "Current plan", style = MaterialTheme.typography.labelLarge)
                  Text(text = state.plan.name, style = MaterialTheme.typography.titleMedium)
                }
                Box(
                  modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.secondary.copy(.12f))
                    .fillMaxHeight(.16f)
                    .width(1.dp)
                )
                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = state.plan.fee.toAmount(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                  )
                  Text(text = state.plan.period.name, style = MaterialTheme.typography.bodySmall)
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(12.dp)) }

          itemsIndexed(state.paymentPlans) { index, paymentPlan ->
            CompanyClientPlanItem(
              plan = paymentPlan,
              onClick = { onEvent(CompanyClientPlanEvent.Button.ChangePlan(it)) }
            )
          }

        }
      }
    }
  }
}


@Preview
@Composable
private fun CompanyClientPlanScreenPreview() {
  WasticalTheme {
    CompanyClientPlanScreen(
      state = CompanyClientPlanState.Success(
        company = company4Preview,
        account = account4Preview,
        plan = paymentPlan4Preview,
        paymentPlans = listOf(paymentPlan4Preview, paymentPlan4Preview),
        demographic = companyLocationWithDemographic4Preview
      ),
      onEvent = {},
      channel = flow { }
    )
  }
}
