package net.techandgraphics.wastical.ui.screen.client.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.ui.theme.WasticalTheme



@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ClientPaymentResponseScreen(
  isSuccess: Boolean,
  error: String? = null,
  onEvent: () -> Unit
) {

  val colorScheme =
    if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

  Column(
    modifier = Modifier
      .padding(horizontal = 8.dp)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {

    Box(contentAlignment = Alignment.Center) {

      Box(
        modifier = Modifier
          .clip(CircleShape)
          .size(120.dp)
          .background(colorScheme.copy(.2f))
      )
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .size(200.dp)
          .background(colorScheme.copy(.1f))
      )
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .size(72.dp)
          .background(
            brush = Brush.horizontalGradient(
              listOf(
                colorScheme.copy(.7f),
                colorScheme.copy(.8f),
                colorScheme
              )
            )
          )
      )
      Icon(
        if (isSuccess) Icons.Rounded.Check else Icons.Rounded.Close, null,
        modifier = Modifier.size(48.dp),
        tint = Color.White
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    if (error != null && isSuccess.not()) {
      Text(
        text = error,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
      )
    }

    Text(
      text = if (isSuccess) "Thank you for your payment" else {
        "We could not process your request at the moment"
      },
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      fontWeight = FontWeight.Bold,
      style = MaterialTheme.typography.headlineSmall,
      textAlign = TextAlign.Center,
      color = colorScheme
    )

    Text(
      text = if (!isSuccess) errorMessage else {
        "Please be patient while we verify your payment transaction, " +
          "you will be a notified when the verification is complete"
      },
      textAlign = TextAlign.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    )


    Button(
      onClick = { onEvent.invoke() },
      colors = ButtonDefaults.buttonColors(containerColor = colorScheme.copy(.5f)),
      modifier = Modifier.fillMaxWidth(.6f)
    ) {
      Text(text = "Go to Home Screen")
    }

  }
}

private val errorMessages = listOf(
  "No need to be concerned, we'll handle the transaction retry in the background and inform you once it's successfully sent for verification.",
  "You don't have to worry, we'll attempt the transaction again in the background and notify you once it's sent for verification.",
  "We've got it covered. We'll try the transaction again in the background and keep you posted once it's sent for verification.",
  "No action needed on your part, we'll retry the transaction in the background and notify you once it's sent for verification.",
  "Don't worry, the transaction will be retried automatically and we'll inform you once its sent for verification.",
  "We'll take care of the retry in the background and let you know once the transaction has been sent for verification.",
  "Everything's in progress, no need to do anything. We'll retry the transaction and update you once it's sent for verification."
)

private val errorMessage = errorMessages.random()


@Preview(showBackground = true)
@Composable
private fun ClientPaymentSuccessfulScreenPreview() {
  WasticalTheme {
    Box(modifier = Modifier.padding(32.dp)) {
      ClientPaymentResponseScreen(
        isSuccess = false,
        error = "Please check your connection",
        onEvent = {}
      )
    }
  }
}
