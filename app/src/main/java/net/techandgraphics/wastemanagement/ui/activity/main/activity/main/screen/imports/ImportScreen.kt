package net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.imports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Clear
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import net.techandgraphics.wastemanagement.ui.theme.WhiteFE

@Composable
fun ImportScreen(channel: Flow<ImportChannel>, onDone: () -> Unit) {

  var status by remember { mutableStateOf<ImportChannel>(ImportChannel.Idle) }
  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { status = it }
    }
  }

  Column(
    modifier = Modifier
      .padding(24.dp)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {

    Text(
      text = "Importing data please wait ...",
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(vertical = 24.dp),
    )
    when (status) {
      ImportChannel.Error ->
        Icon(
          Icons.Rounded.Clear,
          contentDescription = null,
          modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.error)
            .size(62.dp),
          tint = WhiteFE
        )

      ImportChannel.Idle -> CircularProgressIndicator(
        strokeWidth = 8.dp,
        modifier = Modifier.size(62.dp)
      )

      ImportChannel.Success -> Icon(
        Icons.Rounded.CheckCircle,
        contentDescription = null,
        modifier = Modifier
          .padding(vertical = 8.dp)
          .size(62.dp),
        tint = MaterialTheme.colorScheme.primary
      )

      ImportChannel.Done -> onDone.invoke()
    }


  }
}


@Preview(showBackground = true)
@Composable
private fun ImportScreenPreview() {
  WasteManagementTheme {
    ImportScreen(
      channel = flow { },
      onDone = {}
    )
  }
}
