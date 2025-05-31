package net.techandgraphics.wastemanagement.ui.screen.client.payment

import android.app.Activity.RESULT_OK
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yalantis.ucrop.UCrop
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ClientPaymentReferenceView(
  state: ClientPaymentState,
  onEvent: (ClientPaymentEvent) -> Unit
) {

  val context = LocalContext.current

  val imagePickerLauncher =
    rememberLauncherForActivityResult(
      ActivityResultContracts.PickVisualMedia()
    ) {
      onEvent(ClientPaymentEvent.Button.ShowCropView(true))
      onEvent(ClientPaymentEvent.Button.ImageUri(it))
    }

  val uCropLauncher =
    rememberLauncherForActivityResult(
      ActivityResultContracts.StartActivityForResult()
    ) { result ->
      if (result.resultCode == RESULT_OK) {
        val resultUri = UCrop.getOutput(result.data!!)
        resultUri?.let { uri ->
          onEvent(ClientPaymentEvent.Button.ShowCropView(false))
          onEvent(ClientPaymentEvent.Button.ImageUri(uri))
          onEvent(ClientPaymentEvent.Button.ScreenshotAttached)
        }
      } else onEvent(ClientPaymentEvent.Button.ShowCropView(false))
    }

  fun cropImageView(sourceUri: Uri) {
    val destinationUri = Uri.fromFile(File(context.cacheDir, "${state.lastPaymentId}.jpg"))
    val uCropIntent = UCrop.of(sourceUri, destinationUri)
      .withAspectRatio(3f, 2f)
      .withMaxResultSize(800, 800)
      .getIntent(context)
    uCropLauncher.launch(uCropIntent)
  }

  LaunchedEffect(state.showCropView) {
    if (state.showCropView) state.imageUri?.let { cropImageView(it) }
  }


  Column {
    Text(
      text = "Payment Reference",
      modifier = Modifier.padding(8.dp)
    )
    Card(
      colors = CardDefaults.elevatedCardColors(),
      onClick = { imagePickerLauncher.launch(PickVisualMediaRequest()) },

      ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .padding(16.dp)
          .fillMaxWidth()
      ) {
        if (state.screenshotAttached)
          Icon(
            Icons.Outlined.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
          ) else {
          Icon(
            painterResource(R.drawable.ic_add_photo), null,
            modifier = Modifier.size(32.dp)
          )
        }
        Text(
          modifier = Modifier.padding(4.dp),
          text = if (state.screenshotAttached) "Payment Screenshot Attached" else {
            "Attach Payment Screenshot"
          }
        )
      }
    }
  }
}

@Preview(showBackground = true)
@PreviewLightDark
@Composable
private fun ClientPaymentReferenceViewPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(32.dp)) {
      ClientPaymentReferenceView(
        state = ClientPaymentState(
          screenshotAttached = false
        ),
        onEvent = {}
      )
    }
  }
}
