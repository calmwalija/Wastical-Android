package net.techandgraphics.quantcal.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun SnackbarThemed(snackbarData: SnackbarData) {
  Snackbar(
    modifier = Modifier.padding(16.dp),
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    contentColor = MaterialTheme.colorScheme.secondary,
    action = {
      TextButton(
        onClick = { snackbarData.performAction() }
      ) {
        Text(
          "Confirm",
          color = MaterialTheme.colorScheme.primary
        )
      }
    },
    content = {
      Text(snackbarData.visuals.message)
    }
  )
}
