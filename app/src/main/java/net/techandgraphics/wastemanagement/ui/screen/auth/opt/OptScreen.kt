package net.techandgraphics.wastemanagement.ui.screen.auth.opt

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@Composable
fun OptScreen(
  state: OptState,
  channel: Flow<OptChannel>,
  onEvent: (OptEvent) -> Unit,
) {

  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->

      }
    }
  }

  Column {
    OtpInput {

    }
  }

}

@Composable private fun OtpInput(
  otpLength: Int = 4,
  onOtpComplete: (String) -> Unit,
) {

  val otpValues = remember { mutableStateListOf(*Array(otpLength) { "" }) }
  val keyboardController = LocalSoftwareKeyboardController.current

  fun handleOtpInput(input: String, index: Int) {
    if (input.length == 1 && input.all { it.isDigit() }) {
      otpValues[index] = input
      if (index < otpLength - 1) {
        otpValues[index + 1] = ""
      }
      if (otpValues.all { it.isNotEmpty() }) {
        keyboardController?.hide()
        onOtpComplete(otpValues.joinToString(""))
      }
    }
  }

  fun handleOtpDelete() {
    val lastFilledIndex = otpValues.indexOfLast { it.isNotEmpty() }
    if (lastFilledIndex >= 0) {
      otpValues[lastFilledIndex] = ""
    }
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier.padding(16.dp)
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      otpValues.forEachIndexed { index, value ->
        val scale by animateFloatAsState(
          targetValue = if (value.isNotEmpty()) 1.1f else 1f,
          animationSpec = tween(150)
        )
        OutlinedTextField(
          value = value,
          onValueChange = {},
          modifier = Modifier
            .weight(1f)
            .height(60.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)),
          textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 20.sp
          ),
          singleLine = true,
          enabled = false,
        )
      }
    }

    OtpNumberPadView { char ->
      if (char == "⌫") handleOtpDelete() else {
        val firstEmptyIndex = otpValues.indexOfFirst { it.isEmpty() }
        if (firstEmptyIndex >= 0) {
          handleOtpInput(char, firstEmptyIndex)
        }
      }
    }

  }
}

@Preview
@Composable
private fun OptScreenPreview() {
  WasteManagementTheme {
    OptScreen(
      state = OptState(),
      channel = flow { },
      onEvent = {}
    )
  }
}
