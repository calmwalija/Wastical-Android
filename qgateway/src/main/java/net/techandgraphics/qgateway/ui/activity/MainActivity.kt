package net.techandgraphics.qgateway.ui.activity

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import net.techandgraphics.qgateway.toast
import net.techandgraphics.qgateway.ui.theme.QuantcalTheme

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
      .launch(arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS))


  @OptIn(ExperimentalPermissionsApi::class)
  @Composable
  fun SmsPermissionChecker(
    onSmsRequest: (isGranted: Boolean) -> Unit,
  ) {
    val receiveSmsPermission = rememberPermissionState(Manifest.permission.RECEIVE_SMS)
    val readSmsPermission = rememberPermissionState(Manifest.permission.READ_SMS)

    LaunchedEffect(Unit) {
      if (!receiveSmsPermission.status.isGranted) {
        receiveSmsPermission.launchPermissionRequest()
      }
      if (!readSmsPermission.status.isGranted) {
        readSmsPermission.launchPermissionRequest()
      }
    }

    onSmsRequest(receiveSmsPermission.status.isGranted && readSmsPermission.status.isGranted)
  }

  private val viewModel by viewModels<MainActivityViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      QuantcalTheme {
        viewModel.state.collectAsState().value
        Scaffold(modifier = Modifier.Companion.fillMaxSize()) { contentPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(contentPadding),
            contentAlignment = Alignment.Center
          ) {
            Button(onClick = {

            }) {
              Text(text = "Send SMS")
            }
          }
        }
      }
    }
  }

}
