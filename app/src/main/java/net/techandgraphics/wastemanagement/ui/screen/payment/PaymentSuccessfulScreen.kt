package net.techandgraphics.wastemanagement.ui.screen.payment

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
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
import net.techandgraphics.wastemanagement.ui.theme.Green50
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun PaymentSuccessfulScreen(
  state: PaymentState,
  onEvent: (PaymentEvent) -> Unit
) {

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    Spacer(modifier = Modifier.height(44.dp))

    Box(contentAlignment = Alignment.Center) {

      Box(
        modifier = Modifier
          .clip(CircleShape)
          .size(120.dp)
          .background(Green50.copy(.2f))
      )
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .size(200.dp)
          .background(Green50.copy(.1f))
      )
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .size(72.dp)
          .background(
            brush = Brush.horizontalGradient(
              listOf(
                Green50.copy(.7f),
                Green50.copy(.8f),
                Green50
              )
            )
          )
      )
      Icon(
        Icons.Default.Check, null,
        modifier = Modifier.size(44.dp),
        tint = Color.White
      )
    }


    Text(
      text = "Thank you for you payment",
      modifier = Modifier.padding(16.dp),
      fontWeight = FontWeight.Bold,
      style = MaterialTheme.typography.headlineSmall,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.primary
    )

    Text(
      text = "Please be patient while we verify your payment transaction, " +
        "you will receive a conformation notification when the verification is complete",
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(vertical = 16.dp)
    )


    Button(
      onClick = {},
      modifier = Modifier.fillMaxWidth(.8f)
    ) {
      Text(text = "Go to Home Screen")
    }

  }
}


@Preview(showBackground = true)
@Composable
private fun PaymentSuccessfulScreenPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(32.dp)) {
      PaymentSuccessfulScreen(
        state = PaymentState(),
        onEvent = {}
      )
    }
  }
}
