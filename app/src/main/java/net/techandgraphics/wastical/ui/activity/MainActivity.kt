package net.techandgraphics.wastical.ui.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import dagger.hilt.android.AndroidEntryPoint
import net.techandgraphics.wastical.ui.screen.app.AppScreen
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val launcher = registerForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
  ) {}

  override fun onCreate(savedInstanceState: Bundle?) {
    if (Build.VERSION.SDK_INT >= 33) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    setContent { WasticalTheme { Surface { AppScreen() } } }
  }
}
