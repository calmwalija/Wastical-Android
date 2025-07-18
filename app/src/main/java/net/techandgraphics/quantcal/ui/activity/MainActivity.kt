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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import net.techandgraphics.quantcal.ui.screen.app.AppScreen
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<MainViewModel>()
  private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

  override fun onCreate(savedInstanceState: Bundle?) {
    if (Build.VERSION.SDK_INT >= 33) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    enableEdgeToEdge()
    installSplashScreen().apply {
      setKeepOnScreenCondition { viewModel.state.value.isLoading }
    }
    super.onCreate(savedInstanceState)
    setContent {
      QuantcalTheme {
        Surface {
          AppScreen(viewModel)
        }
      }
    }
  }
}
