package net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.metadata

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.toast
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityChannel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityEvent
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainViewModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.ScreenState

@Composable
fun ImportMetadataScreen(
  viewModel: MainViewModel,
) {

  var totalItems by remember { mutableIntStateOf(0) }
  var currentPosition by remember { mutableIntStateOf(0) }
  var showLoader by remember { mutableStateOf(false) }
  val lifecycleOwner = LocalLifecycleOwner.current

  LaunchedEffect(key1 = viewModel.channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      viewModel.channel.collect { event ->
        when (event) {
          MainActivityChannel.Empty -> viewModel.onEvent(
            MainActivityEvent.ChangeScreenState(
              ScreenState.Empty
            )
          )

          MainActivityChannel.Load -> viewModel.onEvent(
            MainActivityEvent.ChangeScreenState(
              ScreenState.Load
            )
          )

          is MainActivityChannel.Import.Data -> viewModel.application.toast(event.status.name)
          is MainActivityChannel.Import.Progress -> {
            showLoader = true
            totalItems = event.total
            currentPosition = event.current
          }
        }
      }
    }
  }

  val jsonPicker = rememberLauncherForActivityResult(contract = GetContent()) { uri ->
    uri?.let {
      viewModel.onEvent(MainActivityEvent.Import(it))
    }
  }

  Box(
    modifier = Modifier
      .padding(32.dp)
      .fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Icon(
        painterResource(R.drawable.ic_database_upload),
        contentDescription = null,
        modifier = Modifier.size(72.dp),
      )

      Text(
        text = "Import app data to proceed",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(vertical = 8.dp),
      )

      TextButton(
        enabled = showLoader.not(),
        onClick = { jsonPicker.launch("application/json") },
      ) {
        Text(text = "Tap Here To Select File")
      }

      if (showLoader) {
        LinearProgressIndicator(
          progress = {
            currentPosition.toFloat().div(totalItems)
          },
          modifier = Modifier.fillMaxWidth(),
        )
      }
    }
  }
}
