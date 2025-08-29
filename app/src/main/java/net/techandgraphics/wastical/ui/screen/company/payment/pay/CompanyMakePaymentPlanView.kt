package net.techandgraphics.wastical.ui.screen.company.payment.pay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CompanyMakePaymentPlanView(
  state: CompanyMakePaymentState.Success,
  onEvent: (CompanyMakePaymentEvent) -> Unit,
) {
  Column {
    Text(
      text = "Payment Plan",
      modifier = Modifier.padding(8.dp)
    )
    Card(colors = CardDefaults.elevatedCardColors()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {

        Column(modifier = Modifier.weight(1f)) {
          Text(text = state.paymentPlan.name)
          Text(
            text = state.paymentPlan.fee.toAmount(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = state.paymentPlan.period.name,
            style = MaterialTheme.typography.bodySmall
          )
        }

        Card {
          Row(verticalAlignment = Alignment.CenterVertically) {
            AutoRepeatIconButton(
              onClick = { onEvent(CompanyMakePaymentEvent.Button.NumberOfMonths(false)) },
              enabled = state.numberOfMonths > 1
            ) { Icon(Icons.AutoMirrored.TwoTone.KeyboardArrowLeft, null) }
            Text(
              text = "${state.numberOfMonths}",
              modifier = Modifier.padding(horizontal = 4.dp)
            )
            AutoRepeatIconButton(
              onClick = { onEvent(CompanyMakePaymentEvent.Button.NumberOfMonths(true)) },
              enabled = state.numberOfMonths < 12
            ) { Icon(Icons.AutoMirrored.TwoTone.KeyboardArrowRight, null) }
          }
        }

      }
    }
  }

}


@Composable
private fun AutoRepeatIconButton(
  onClick: () -> Unit,
  enabled: Boolean,
  content: @Composable () -> Unit,
) {
  val interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() }
  val haptic = LocalHapticFeedback.current

  androidx.compose.material3.IconButton(
    onClick = {},
    enabled = enabled,
    interactionSource = interactionSource,
  ) { content() }

  LaunchedEffect(interactionSource, enabled) {
    var repeatJob: Job? = null
    interactionSource.interactions.collect { interaction ->
      when (interaction) {
        is PressInteraction.Press -> {
          if (!enabled) return@collect
          haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
          onClick()
          repeatJob?.cancel()
          repeatJob = launch {
            var delayMs = 250L
            while (enabled) {
              delay(delayMs)
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onClick()
              delayMs = (delayMs * 0.80).toLong().coerceAtLeast(30L)
            }
          }
        }
        is PressInteraction.Release, is PressInteraction.Cancel -> {
          repeatJob?.cancel()
          repeatJob = null
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun CompanyMakePaymentPlanViewPreview() {
  WasticalTheme {
    CompanyMakePaymentPlanView(
      state = companySuccessState(LocalContext.current),
      onEvent = {}
    )
  }
}
