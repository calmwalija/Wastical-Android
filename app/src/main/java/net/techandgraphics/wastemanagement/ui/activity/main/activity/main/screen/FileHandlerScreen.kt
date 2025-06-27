package net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivity

@Composable
fun FileHandlerScreen(
  activity: MainActivity,
  onFileReceived: (Uri) -> Unit,
  content: @Composable () -> Unit,
) {
  val intent = remember { activity.intent }
  LaunchedEffect(intent) {
    if (intent?.action == Intent.ACTION_VIEW) {
      intent.data?.let { uri ->
        onFileReceived(uri)
      }
    }
  }
  content()
}
