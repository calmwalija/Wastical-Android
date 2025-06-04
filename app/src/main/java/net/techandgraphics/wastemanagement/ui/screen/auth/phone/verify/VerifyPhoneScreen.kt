package net.techandgraphics.wastemanagement.ui.screen.auth.phone.verify

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.toast
import net.techandgraphics.wastemanagement.ui.InputField
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@Composable
fun VerifyPhoneScreen(
  state: VerifyPhoneState,
  channel: Flow<VerifyPhoneChannel>,
  onEvent: (VerifyPhoneEvent) -> Unit,
) {

  val context = LocalContext.current
  var isProcessing by remember { mutableStateOf(false) }
  val hapticFeedback = LocalHapticFeedback.current

  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        isProcessing = false
        when (event) {
          is VerifyPhoneChannel.Response.Failure -> context.toast(event.error.message)

          is VerifyPhoneChannel.Response.Success -> onEvent(VerifyPhoneEvent.Goto.Otp(event.phone))
        }
      }
    }
  }

  Box(
    modifier = Modifier
      .padding(16.dp)
      .fillMaxSize()
  ) {

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      Icon(
        painter = painterResource(R.drawable.ic_tag),
        contentDescription = null,
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp)
          .size(82.dp),
        tint = MaterialTheme.colorScheme.secondary,
      )

      Text(
        text = "Verify Your Number",
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

      Text(
        text = buildAnnotatedString {
          append("Enter your phone number and we will send you a ")
          withStyle(
            SpanStyle(
              fontWeight = FontWeight.Bold,
              fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
          ) {
            append("One Time Password (OTP)")
          }
          append(". Use the four digit code to verify your account.")

        },
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
      )


      Spacer(modifier = Modifier.height(24.dp))


      InputField(
        painterResource = R.drawable.ic_tag,
        value = state.contact,
        prompt = "type phone number",
        onValueChange = { if (it.length < 15) onEvent(VerifyPhoneEvent.Input.Phone(it)) },
        keyboardType = KeyboardType.Phone
      )


      Spacer(modifier = Modifier.height(32.dp))

      Button(
        enabled = state.contact.isDigitsOnly() && state.contact.length > 8,
        modifier = Modifier.fillMaxWidth(.7f),
        onClick = { onEvent(VerifyPhoneEvent.Button.Verify); isProcessing = true }
      ) {
        Box {
          if (isProcessing) CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = MaterialTheme.colorScheme.secondary
          ) else {
            Text(text = "Verify")
          }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun VerifyPhoneScreenPreview() {
  WasteManagementTheme {
    VerifyPhoneScreen(
      state = VerifyPhoneState(),
      channel = flow { },
      onEvent = {}
    )
  }
}
