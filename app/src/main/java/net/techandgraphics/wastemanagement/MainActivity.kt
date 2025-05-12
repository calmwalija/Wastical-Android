package net.techandgraphics.wastemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      WasteManagementTheme {
        Surface {
        }
      }
    }
  }
}
