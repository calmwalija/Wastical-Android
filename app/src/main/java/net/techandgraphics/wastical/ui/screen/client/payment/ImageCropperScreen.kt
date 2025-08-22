package net.techandgraphics.wastical.ui.screen.client.payment

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.moyuru.cropify.Cropify
import io.moyuru.cropify.rememberCropifyState
import net.techandgraphics.wastical.toUri


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCropperScreen(
  imageUri: Uri?,
  onCropComplete: (Uri) -> Unit,
  onDismissRequest: () -> Unit,
) {

  val cropifyState = rememberCropifyState()
  val context = LocalContext.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
  ) {
    Scaffold(
      bottomBar = {
        BottomAppBar {
          Row(
            modifier = Modifier
              .padding(horizontal = 8.dp)
              .fillMaxWidth(),
          ) {
            Button(
              onClick = { cropifyState.crop() },
              modifier = Modifier.weight(1f)
            ) {
              Text(text = "Crop")
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(onClick = onDismissRequest) {
              Text(text = "Cancel")
            }
          }

        }
      }
    ) {
      Cropify(
        uri = imageUri!!,
        state = cropifyState,
        onImageCropped = { croppedImageBitmap ->
          val croppedUri = croppedImageBitmap.asAndroidBitmap().toUri(context)
          onCropComplete(croppedUri!!)
        },
        onFailedToLoadImage = { },
        modifier = Modifier
          .padding(bottom = it.calculateBottomPadding())
          .fillMaxSize(),
      )

    }
  }
}
