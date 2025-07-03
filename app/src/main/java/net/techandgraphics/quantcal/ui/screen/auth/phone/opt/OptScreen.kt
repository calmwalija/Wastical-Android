package net.techandgraphics.quantcal.ui.screen.auth.phone.opt

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@Composable
fun OptScreen(
  state: OptState,
  onEvent: (OptEvent) -> Unit,
) {


  Column(
    modifier = Modifier
      .padding(24.dp)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally

  ) {

    Icon(
      painter = painterResource(R.drawable.ic_sms),
      contentDescription = null,
      modifier = Modifier
        .fillMaxWidth()
        .padding(32.dp)
        .size(82.dp),
      tint = MaterialTheme.colorScheme.secondary,
    )

    Text(
      text = "OTP Verification",
      fontWeight = FontWeight.Bold,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier
        .padding(bottom = 8.dp)
        .fillMaxWidth(),
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.primary
    )

    if (state is OptState.Success)
      Text(
        text = "Enter the 4 digit code sent to ${state.phone}",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
      )


    Spacer(modifier = Modifier.height(8.dp))

    OtpInput {

    }

    if (state is OptState.Success)
      Button(
        modifier = Modifier.fillMaxWidth(.7f),
        onClick = { }
      ) {
        Box {
          if (false) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else {
            Text(text = "Verify")
          }
        }
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
            .padding(bottom = 16.dp)
            .weight(1f)
            .height(60.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .border(2.dp, MaterialTheme.colorScheme.secondary),
          textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 20.sp
          ),
          singleLine = true,
          enabled = false,
          colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.secondary
          )
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

@Preview(showBackground = true)
@Composable
private fun OptScreenPreview() {
  QuantcalTheme {
    OptScreen(
      state = OptState.Success(phone = "999112233"),
      onEvent = {}
    )
  }
}
