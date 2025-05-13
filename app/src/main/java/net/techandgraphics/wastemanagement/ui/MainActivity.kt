package net.techandgraphics.wastemanagement.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import dagger.hilt.android.AndroidEntryPoint
import net.techandgraphics.wastemanagement.ui.screen.app.AppScreen
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<MainViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      WasteManagementTheme {
        Surface { AppScreen(viewModel) }
      }
    }
  }
}
