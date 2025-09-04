package net.techandgraphics.wastical.ui.screen.auth.phone.verify

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.toast
import net.techandgraphics.wastical.ui.screen.auth.phone.verify.VerifyPhoneEvent.Goto.Otp
import net.techandgraphics.wastical.ui.theme.Muted
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable
fun VerifyPhoneScreen(
  state: VerifyPhoneState,
  channel: Flow<VerifyPhoneChannel>,
  onEvent: (VerifyPhoneEvent) -> Unit,
) {

  val context = LocalContext.current
  val hapticFeedback = LocalHapticFeedback.current
  var isProcessing by remember { mutableStateOf(false) }

  val textFieldDefaults = TextFieldDefaults.colors(
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    focusedPlaceholderColor = Muted,
    unfocusedPlaceholderColor = Muted
  )

  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        isProcessing = false
        when (event) {
          is VerifyPhoneChannel.Response.Failure -> context.toast(event.error.message)
          is VerifyPhoneChannel.Response.Success ->
            onEvent(Otp(event.sms.contact))

          is VerifyPhoneChannel.Continue ->
            onEvent(Otp(event.contact))
        }
      }
    }
  }

  Scaffold { contentPadding ->
    LazyColumn(
      contentPadding = contentPadding,
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      item {
        Icon(
          painter = painterResource(R.drawable.ic_logo),
          contentDescription = null,
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .size(102.dp),
          tint = MaterialTheme.colorScheme.primary,
        )
      }

      item {
        Text(
          text = "Welcome",
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.headlineMedium,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center,
        )
      }

      item { Spacer(modifier = Modifier.height(4.dp)) }

      item {
        Text(
          text = "Let's get you verified. It's quick & easy",
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center,
        )
      }

      item { Spacer(modifier = Modifier.height(32.dp)) }

      item {
        Text(
          text = "Enter your phone number",
          modifier = Modifier.fillMaxWidth(),
          style = MaterialTheme.typography.labelMedium
        )
      }

      item { Spacer(modifier = Modifier.height(8.dp)) }


      item {
        Column(modifier = Modifier) {
          var lSize by remember { mutableIntStateOf(0) }
          LaunchedEffect(state.contact) {
            lSize = state.contact.length
            if (state.contact.length > 9) hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
          }
          TextField(
            leadingIcon = {
              Icon(Icons.Rounded.Phone, null)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "e.g., 999001122") },
            shape = RoundedCornerShape(8),
            maxLines = 1,
            value = state.contact,
            onValueChange = { newValue ->
              if (newValue.length < 11) onEvent(VerifyPhoneEvent.Input.Phone(newValue))
            },
            colors = textFieldDefaults,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Number,
              imeAction = ImeAction.Done
            ),
            trailingIcon = {
              Text(
                text = "$lSize/10",
                style = MaterialTheme.typography.bodySmall,
                color = if (lSize < 10) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(end = 24.dp, start = 8.dp)
              )
            },
          )
        }
      }

      item { Spacer(modifier = Modifier.height(32.dp)) }

      item {
        Button(
          enabled = isProcessing.not(),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8),
          onClick = {
            if (state.contact.isDigitsOnly().not() || state.contact.length < 9) {
              context.toast("Please enter a valid phone number")
              hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
              return@Button
            }
            onEvent(VerifyPhoneEvent.Button.Verify)
            isProcessing = true
          },
        ) {
          Box(modifier = Modifier.padding(vertical = 8.dp)) {
            if (isProcessing) CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = MaterialTheme.colorScheme.secondary
            ) else {
              Text(
                text = "Send Verification Code",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(32.dp)) }

      item {
        Text(
          text = buildAnnotatedString {
            append("By continuing, you agree to our ")
            withStyle(
              SpanStyle(
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                color = MaterialTheme.colorScheme.primary
              )
            ) {
              append("Terms of Service")
            }
            append("  and  ")
            withStyle(
              SpanStyle(
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                color = MaterialTheme.colorScheme.primary
              )
            ) {
              append("Privacy Policy")
            }
          },
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center
        )
      }

      item { Spacer(modifier = Modifier.fillParentMaxHeight(.2f)) }

    }
  }


}

@Preview(
  showBackground = true,
  uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun VerifyPhoneScreenPreview() {
  WasticalTheme {
    VerifyPhoneScreen(
      state = VerifyPhoneState(),
      channel = flow { },
      onEvent = {}
    )
  }
}
