package net.techandgraphics.quantcal.ui.screen.company.client.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.toast
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.screen.account4Preview
import net.techandgraphics.quantcal.ui.screen.company.AccountInfoView
import net.techandgraphics.quantcal.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.quantcal.ui.screen.paymentPlan4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

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

      var isProcessing by remember { mutableStateOf(false) }
      val hapticFeedback = LocalHapticFeedback.current
      val context = LocalContext.current
      val lifecycleOwner = LocalLifecycleOwner.current

      LaunchedEffect(key1 = channel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
          channel.collectLatest { event ->
            when (event) {
              is CompanyClientPlanChannel.Error -> {
                isProcessing = false
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                context.toast(event.error.message)
              }

              CompanyClientPlanChannel.Success -> {
                isProcessing = false
                context.toast("Payment Plan Changed")
                onEvent(CompanyClientPlanEvent.Button.BackHandler)
              }

              CompanyClientPlanChannel.Processing -> isProcessing = true
            }
          }
        }
      }



      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent
          }
        },
        contentWindowInsets = WindowInsets.safeGestures
      ) {

        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp)
        ) {
          item {
            Text(
              text = "Payment Plan",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }

          item { AccountInfoView(state.account, state.demographic) }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          itemsIndexed(state.paymentPlans) { index, paymentPlan ->
            OutlinedCard(
              modifier = Modifier.padding(vertical = 4.dp),
              colors = if (paymentPlan.active) CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(.1f)
              ) else {
                CardDefaults.elevatedCardColors()
              }
            ) {
              Row(
                modifier = Modifier
                  .clickable { onEvent(CompanyClientPlanEvent.Button.ChangePlan(paymentPlan)) }
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {

                RadioButton(selected = paymentPlan.active, onClick = {
                  onEvent(CompanyClientPlanEvent.Button.ChangePlan(paymentPlan))
                })

                Column(
                  modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
                ) {

                  Text(
                    text = "Payment Plan ${index.plus(1)}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis
                  )

                  Text(
                    text = paymentPlan.name,
                    style = MaterialTheme.typography.titleMedium
                  )

                }

                Box(
                  modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.secondary.copy(.1f))
                    .fillMaxHeight(.05f)
                    .width(1.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = paymentPlan.fee.toAmount(),
                    color = MaterialTheme.colorScheme.primary
                  )

                  Text(
                    text = paymentPlan.period.name,
                    style = MaterialTheme.typography.bodySmall
                  )
                }


              }

            }
          }


          item {
            Row(
              modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.Center
            ) {
              ElevatedButton(
                enabled = isProcessing.not(),
                shape = RoundedCornerShape(8),
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(CompanyClientPlanEvent.Button.Submit) }) {
                Box {
                  Text(
                    text = "Change Payment Plan",
                    modifier = Modifier.padding(8.dp)
                  )
                }
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
private fun CompanyClientPlanScreenPreview() {
  QuantcalTheme {
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
