package net.techandgraphics.wcompanion.ui.activity

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import net.techandgraphics.wcompanion.toast
import net.techandgraphics.wcompanion.ui.screen.app.AppScreen
import net.techandgraphics.wcompanion.ui.theme.WasticalTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val requestPermissionLauncher =
    registerForActivityResult(
      ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
      if (result.values.all { it }) toast("Permissions granted") else {
        toast("SMS permission required")
      }
    }

  private fun requestPermissions() =
    requestPermissionLauncher
      .launch(
        arrayOf(
          Manifest.permission.RECEIVE_SMS,
          Manifest.permission.READ_SMS,
          Manifest.permission.SEND_SMS,
          Manifest.permission.READ_PHONE_STATE
        )
      )


  @OptIn(ExperimentalPermissionsApi::class)
  @Composable
  fun PermissionChecker(
    onSmsRequest: (isGranted: Boolean) -> Unit,
  ) {
    val receiveSmsPermission = rememberPermissionState(Manifest.permission.RECEIVE_SMS)
    val readSmsPermission = rememberPermissionState(Manifest.permission.READ_SMS)
    val readPhoneStatePermission = rememberPermissionState(Manifest.permission.READ_PHONE_STATE)
    val sendSmsStatePermission = rememberPermissionState(Manifest.permission.SEND_SMS)

    LaunchedEffect(Unit) {
      if (!receiveSmsPermission.status.isGranted) {
        receiveSmsPermission.launchPermissionRequest()
      }
      if (!readSmsPermission.status.isGranted) {
        readSmsPermission.launchPermissionRequest()
      }

      if (!readPhoneStatePermission.status.isGranted) {
        readPhoneStatePermission.launchPermissionRequest()
      }

      if (!sendSmsStatePermission.status.isGranted) {
        sendSmsStatePermission.launchPermissionRequest()
      }

    }

    onSmsRequest(
      receiveSmsPermission.status.isGranted
        && readSmsPermission.status.isGranted
        && readPhoneStatePermission.status.isGranted
        && sendSmsStatePermission.status.isGranted
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      WasticalTheme {
        var isGranted by remember { mutableStateOf(false) }
        Surface(modifier = Modifier.Companion.fillMaxSize()) {
          PermissionChecker { isGranted = it }
          if (isGranted) {
            AppScreen()
          } else requestPermissions()
        }
      }
    }
  }
}
