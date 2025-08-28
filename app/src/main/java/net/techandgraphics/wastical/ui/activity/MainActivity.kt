package net.techandgraphics.wastical.ui.activity

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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import net.techandgraphics.wastical.ui.screen.app.AppScreen
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<MainActivityViewModel>()
  private val launcher = registerForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
  ) {}

  override fun onCreate(savedInstanceState: Bundle?) {
    if (Build.VERSION.SDK_INT >= 33) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    installSplashScreen()
    actionBar?.hide()
    super.onCreate(savedInstanceState)
    setContent {
      val state = viewModel.state.collectAsState().value
      WasticalTheme(darkTheme = state.darkTheme, dynamicColor = state.dynamicColor) {
        Surface { AppScreen(activity = this) }
      }
    }
    enableEdgeToEdge()
  }
}
