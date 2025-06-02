package net.techandgraphics.wastemanagement.ui.screen.company.payment.pay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Card
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
import net.techandgraphics.wastemanagement.data.remote.ApiResult
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CompanyRecordPaymentResponseDialog(
  account: AccountUiModel,
  isSuccess: Boolean,
  error: ApiResult.Error? = null,
  onEvent: () -> Unit,
) {

  val colorScheme =
    if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

  Card {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(32.dp),
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
          text = error.message,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
        )
      }

      Text(
        text = if (isSuccess) "The payment has been recorded successfully" else {
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
          "Since this payment has been entered by the company owner," +
            " it has already been approved " +
            "& we will let ${account.toFullName()} know about it as well."
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
        Text(text = "Okay")
      }

    }
  }
}

private val errorMessages = listOf(
  "No need to be concerned, we'll handle the transaction retry in the background and inform you once it's successfully sent to the server.",
  "You don't have to worry, we'll attempt the transaction again in the background and notify you once it's sent to the server.",
  "We've got it covered. We'll try the transaction again in the background and keep you posted once it's sent to the server.",
  "No action needed on your part, we'll retry the transaction in the background and notify you once it's sent to the server.",
  "Don't worry, the transaction will be retried automatically and we'll inform you once its sent to the server.",
  "Everything's in progress, no need to do anything. We'll retry the transaction and update you once it's sent to the server."
)

private val errorMessage = errorMessages.random()


@Preview(showBackground = true)
@Composable
private fun CompanyRecordPaymentResponseDialogPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(32.dp)) {
      CompanyRecordPaymentResponseDialog(
        account = account4Preview,
        isSuccess = true,
        error = null,
        onEvent = {}
      )
    }
  }
}
