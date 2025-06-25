package net.techandgraphics.wastemanagement.ui.activity.main.activity.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.toast
import net.techandgraphics.wastemanagement.ui.screen.app.AppScreen
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<MainViewModel>()
  private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

  enum class ScreenState { Load, Empty, Idle }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (Build.VERSION.SDK_INT >= 33) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    enableEdgeToEdge()
    setContent {
      WasteManagementTheme {
        val state = viewModel.state.collectAsState().value
        var screenState by remember { mutableStateOf(ScreenState.Idle) }
        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
        var showLoader by remember { mutableStateOf(false) }
        var totalItems by remember { mutableStateOf(0) }
        var currentPosition by remember { mutableStateOf(0) }

        LaunchedEffect(key1 = viewModel.channel) {
          lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.channel.collect { event ->
              when (event) {
                MainActivityChannel.Empty -> screenState = ScreenState.Empty
                MainActivityChannel.Load -> screenState = ScreenState.Load
                is MainActivityChannel.Import.Data -> toast(event.status.name)
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

        Surface {
          when (screenState) {
            ScreenState.Load -> AppScreen(state = state)
            ScreenState.Empty -> Box(
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

            ScreenState.Idle -> Unit
          }
        }
      }
    }
  }
}
