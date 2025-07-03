package net.techandgraphics.quantcal.ui.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import dagger.hilt.android.AndroidEntryPoint
import net.techandgraphics.quantcal.ui.screen.app.AppScreen
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<MainViewModel>()
  private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (Build.VERSION.SDK_INT >= 33) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    enableEdgeToEdge()
    setContent {
      QuantcalTheme {
        val state = viewModel.state.collectAsState().value
        Surface {
          AppScreen(state = state)
        }
      }
    }
  }
}
