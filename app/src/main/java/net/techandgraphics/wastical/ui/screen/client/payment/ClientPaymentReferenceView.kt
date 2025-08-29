package net.techandgraphics.wastical.ui.screen.client.payment

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ClientPaymentReferenceView(
  state: ClientPaymentState.Success,
  onEvent: (ClientPaymentEvent) -> Unit,
) {

  val context = LocalContext.current
  val uri = state.imageUri
  val mime = if (uri != null) context.contentResolver.getType(uri) ?: "" else ""
  val isImage = mime.startsWith("image/")
  state.imageUri?.lastPathSegment?.substringAfterLast('/') ?: "Attachment"

  Card(
    colors = CardDefaults.elevatedCardColors(),
    onClick = { onEvent(ClientPaymentEvent.Button.AttachScreenshot) },
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
    ) {
      if (state.screenshotAttached) {
        Box(modifier = Modifier.fillMaxWidth()) {
          IconButton(
            onClick = { onEvent(ClientPaymentEvent.Button.RemoveScreenshot) },
            modifier = Modifier.align(Alignment.TopEnd)
          ) {
            Icon(Icons.Outlined.Close, contentDescription = null)
          }
        }
        Box(
          modifier = Modifier
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
        ) {
          if (isImage && uri != null) {
            Image(
              painter = rememberAsyncImagePainter(model = uri),
              contentDescription = null,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
            )
          } else {
            if (uri != null) {
              PdfPreview(
                uri = uri, modifier = Modifier
                  .fillMaxWidth()
                  .height(160.dp)
              )
            }
          }
        }
        Text(
          modifier = Modifier.padding(top = 8.dp),
          text = "Tap to replace. Max 500KB image or PDF.",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      } else {
        Icon(
          painterResource(R.drawable.ic_add_photo), null,
          modifier = Modifier.size(32.dp)
        )
        Text(
          modifier = Modifier.padding(4.dp),
          text = "Attach Proof Of Payment",
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "Image or PDF. Max 500KB.",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(
          onClick = { onEvent(ClientPaymentEvent.Button.AttachScreenshot) },
        ) {
          Text(text = "Choose file")
        }
      }
    }
  }
}

@Composable
private fun PdfPreview(uri: Uri, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

  LaunchedEffect(uri) {
    bitmapState.value = withContext(Dispatchers.IO) {
      try {
        context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
          PdfRenderer(pfd).use { renderer ->
            if (renderer.pageCount > 0) {
              renderer.openPage(0).use { page ->
                val width = (page.width * 2)
                val height = (page.height * 2)
                val bmp = createBitmap(width, height)
                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bmp
              }
            } else null
          }
        }
      } catch (_: Throwable) {
        null
      }
    }
  }

  if (bitmapState.value != null) {
    Image(
      bitmap = bitmapState.value!!.asImageBitmap(),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = modifier.clip(RoundedCornerShape(12.dp))
    )
  } else {
    Box(
      modifier = modifier,
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "PDF preview unavailable",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun ClientPaymentReferenceViewPreview() {
  WasticalTheme {
    Box(modifier = Modifier.padding(32.dp)) {
      ClientPaymentReferenceView(
        state = clientPaymentStateSuccess(),
        onEvent = {}
      )
    }
  }
}
