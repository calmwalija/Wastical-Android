package net.techandgraphics.wastemanagement.ui.activity.main.activity.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import net.techandgraphics.wastemanagement.getFile
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.FileHandlerScreen
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.imports.ImportEvent
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.imports.ImportScreen
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.imports.ImportViewModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.metadata.ImportMetadataScreen
import net.techandgraphics.wastemanagement.ui.screen.app.AppScreen
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<MainViewModel>()
  private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (Build.VERSION.SDK_INT >= 33) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    enableEdgeToEdge()
    setContent {
      WasteManagementTheme {
        var fileBackup by remember { mutableStateOf<File?>(null) }
        val state = viewModel.state.collectAsState().value
        Surface {
          FileHandlerScreen(
            activity = this,
            onFileReceived = { uri -> fileBackup = this.getFile(uri) },
          ) {
            fileBackup?.let {
              val iViewModel = hiltViewModel<ImportViewModel>()
              val jsonString = it.readText()
              LaunchedEffect(jsonString) { iViewModel.onEvent(ImportEvent.Import(jsonString)) }
              ImportScreen(iViewModel.channel) {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
              }
              return@let
            }

            when (state.screenState) {
              ScreenState.Load -> AppScreen(state = state)
              ScreenState.Empty -> ImportMetadataScreen(viewModel)
              ScreenState.Idle -> Unit
            }
          }
        }
      }
    }
  }
}
