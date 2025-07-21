package net.techandgraphics.quantcal.ui.screen.company.client.profile

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientProfileRevokeView(
  onProceedWithCaution: () -> Unit,
  onCancelRequest: () -> Unit,
) {

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      Icons.Rounded.Warning,
      contentDescription = null,
      modifier = Modifier.size(92.dp),
      tint = MaterialTheme.colorScheme.error
    )

    Text(
      text = "Warning",
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(top = 16.dp)
    )

    Text(
      text = "Remove Client & Revoke Access",
      modifier = Modifier.padding(bottom = 16.dp),
      color = MaterialTheme.colorScheme.error
    )

    Text(
      text = "You are about to remove and revoke this clients's access to the system. " +
        "They will no longer be able to log in or access any features. Please proceed with caution.",
      textAlign = TextAlign.Center
    )


    Spacer(modifier = Modifier.height(32.dp))

    Row {
      Button(
        modifier = Modifier.weight(1f),
        onClick = { onProceedWithCaution.invoke() }) {
        Box {
          Text(text = "Proceed")
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      OutlinedButton(
        onClick = { onCancelRequest.invoke() }) {
        Box {
          Text(text = "Cancel")
        }
      }
    }

  }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyClientProfileRevokePreview() {
  QuantcalTheme {
    CompanyClientProfileRevokeView(
      onProceedWithCaution = {},
      onCancelRequest = {}
    )
  }
}
