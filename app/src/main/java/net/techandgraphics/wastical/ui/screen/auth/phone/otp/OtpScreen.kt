package net.techandgraphics.wastical.ui.screen.auth.phone.otp

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastical.toast
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable
fun OtpScreen(
  state: OtpState,
  channel: Flow<OtpChannel>,
  onEvent: (OtpEvent) -> Unit,
) {

  var opt by remember { mutableStateOf("") }
  val context = LocalContext.current
  val hapticFeedback = LocalHapticFeedback.current
  var isProcessing by remember { mutableStateOf(false) }

  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        isProcessing = false
        when (event) {
          OtpChannel.Success -> onEvent(OtpEvent.Goto.Home)
          is OtpChannel.Error -> {
            context.toast(event.error.localizedMessage!!)
            hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
          }

          OtpChannel.Verify -> onEvent(OtpEvent.Goto.Verify)
        }
      }
    }
  }


  when (state) {
    OtpState.Loading -> LoadingIndicatorView()
    is OtpState.Success -> {

      LaunchedEffect(Unit) {
        SmsRetriever.getClient(context)
          .startSmsRetriever()
          .addOnSuccessListener { Unit }
          .addOnFailureListener { Unit }
      }


      OtpListener { event ->
        when (event) {
          is OtpListenerEvent.OTPReceived ->
            event.opt?.let {
              opt = it
              isProcessing = true
              onEvent(OtpEvent.Otp(it))
            }

          OtpListenerEvent.OTPTimeOut -> Unit
        }
      }

      Scaffold { contentPadding ->
        LazyColumn(
          contentPadding = contentPadding,
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {

          item {
            Text(
              text = "Verify Code",
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Center,
              color = MaterialTheme.colorScheme.primary
            )
          }

          item {
            Text(
              text = "Enter the 4 digit code sent to ${state.phone}",
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Center
            )
          }

          item { Spacer(modifier = Modifier.height(8.dp)) }

          item { OtpInput(onOtpChanged = { opt = it }) }

          item {
            Button(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8),
              enabled = isProcessing.not() && opt.trim().length > 3,
              onClick = {
                onEvent(OtpEvent.Otp(opt))
                isProcessing = true
              }
            ) {
              Box(modifier = Modifier.padding(vertical = 8.dp)) {
                if (isProcessing) CircularProgressIndicator(
                  modifier = Modifier.size(24.dp),
                  color = MaterialTheme.colorScheme.secondary
                ) else {
                  Text(text = "Verify")
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(8.dp)) }

          item {
            TextButton(onClick = { onEvent(OtpEvent.NotMe) }) {
              Text(text = "Use another phone number")
            }
          }

        }
      }
    }
  }
}


@Composable
private fun OtpInput(
  otpLength: Int = 4,
  onOtpChanged: (String) -> Unit,
) {
  val otpValues = remember { mutableStateListOf(*Array(otpLength) { "" }) }
  val keyboardController = LocalSoftwareKeyboardController.current

  fun emitChange() {
    val currentOtp = otpValues.joinToString("")
    onOtpChanged(currentOtp)
    if (otpValues.all { it.isNotEmpty() }) {
      keyboardController?.hide()
    }
  }

  fun handleOtpInput(input: String, index: Int) {
    if (input.length == 1 && input.all { it.isDigit() }) {
      otpValues[index] = input
      if (index < otpLength - 1) {
        otpValues[index + 1] = ""
      }
      emitChange()
    }
  }

  fun handleOtpDelete() {
    val lastFilledIndex = otpValues.indexOfLast { it.isNotEmpty() }
    if (lastFilledIndex >= 0) {
      otpValues[lastFilledIndex] = ""
      emitChange()
    }
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = Modifier.padding(16.dp)
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
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
            .border(
              width = 2.dp,
              color = MaterialTheme.colorScheme.secondary,
              shape = RoundedCornerShape(16)
            ),
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
  WasticalTheme {
    OtpScreen(
      state = OtpState.Success(
        phone = "999112233",
        account = account4Preview
      ),
      channel = flow { },
      onEvent = {}
    )
  }
}
