package net.techandgraphics.wastemanagement.ui.screen.auth.signin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.toast
import net.techandgraphics.wastemanagement.ui.InputField
import net.techandgraphics.wastemanagement.ui.screen.auth.signin.SignInEvent.Input.Credentials
import net.techandgraphics.wastemanagement.ui.screen.auth.signin.SignInEvent.Input.Type
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@Composable
fun SignInScreen(
  state: SignInState,
  channel: Flow<SignInChannel>,
  onEvent: (SignInEvent) -> Unit
) {

  val context = LocalContext.current
  var hidePassword by remember { mutableStateOf(true) }
  var isProcessing by remember { mutableStateOf(false) }
  val hapticFeedback = LocalHapticFeedback.current

  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        isProcessing = false
        when (event) {
          is SignInChannel.Response.Failure -> context.toast(event.error.message)

          SignInChannel.Response.Success -> onEvent(SignInEvent.GoTo.Main)
        }
      }
    }
  }

  Box(
    modifier = Modifier
      .padding(16.dp)
      .fillMaxSize()
  ) {

    Column(modifier = Modifier.padding(32.dp)) {

      Icon(
        painter = painterResource(R.drawable.ic_login),
        contentDescription = null,
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp)
          .size(82.dp),
        tint = MaterialTheme.colorScheme.secondary,
      )

      Text(
        text = "Sign In",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 24.dp)
      )


      InputField(
        painterResource = R.drawable.ic_tag,
        value = state.contactNumber,
        prompt = "type phone number",
        onValueChange = { if (it.length < 10) onEvent(Credentials(it, Type.ContactNumber)) },
        maskTransformation = "XXX-XXX-XXX",
        keyboardType = KeyboardType.Phone
      )


      Spacer(modifier = Modifier.height(16.dp))


      InputField(
        painterResource = R.drawable.ic_password,
        value = state.password,
        prompt = "type password",
        onValueChange = { onEvent(Credentials(it, Type.Password)) },
        keyboardType = KeyboardType.Password,
        hidePassword = hidePassword,
        togglePasswordVisual = { hidePassword = !hidePassword }
      )

      Text(
        text = "Forgot password ?",
        textAlign = TextAlign.End,
        modifier = Modifier
          .align(Alignment.End)
          .padding(vertical = 8.dp)
          .clip(CircleShape)
          .clickable {}
          .padding(8.dp),
        color = MaterialTheme.colorScheme.secondary
      )

      Spacer(modifier = Modifier.height(16.dp))

      ElevatedButton(
        enabled = state.contactNumber.isNotEmpty() && state.password.isNotEmpty(),
        shape = RoundedCornerShape(8),
        modifier = Modifier.fillMaxWidth(),
        onClick = { onEvent(SignInEvent.Button.AccessToken); isProcessing = true }
      ) {
        Box {
          if (isProcessing) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else {
            Text(text = "Sign in")
          }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun SignInScreenPreview() {
  WasteManagementTheme {
    SignInScreen(
      state = SignInState(),
      channel = flow { },
      onEvent = {}
    )
  }
}
