package net.techandgraphics.wastical.ui.screen.client.payment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.scale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCropperScreen(
  imageUri: Uri?,
  onCropComplete: (Uri) -> Unit,
  onDismiss: () -> Unit,
) {
  var bitmap by remember { mutableStateOf<Bitmap?>(null) }
  var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

  var cropWidth by remember { mutableFloatStateOf(300f) }
  var cropHeight by remember { mutableFloatStateOf(200f) }
  var cropOffset by remember { mutableStateOf(Offset.Zero) }
  var canvasSize by remember { mutableStateOf(Size.Zero) }

  val context = LocalContext.current

  LaunchedEffect(imageUri) {
    imageUri?.let { uri ->
      withContext(Dispatchers.IO) {
        try {
          val inputStream = context.contentResolver.openInputStream(uri)
          val loadedBitmap = BitmapFactory.decodeStream(inputStream)
          inputStream?.close()

          val scaledBitmap = scaleBitmapToMaxSize(loadedBitmap, 2048, 2048)
          bitmap = scaledBitmap
          imageBitmap = scaledBitmap.asImageBitmap()
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      dismissOnBackPress = true,
      dismissOnClickOutside = false,
      usePlatformDefaultWidth = false
    )
  ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("Crop Image", fontWeight = FontWeight.Bold) },
          navigationIcon = {
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }
        )
      },
      floatingActionButton = {
        FloatingActionButton(
          onClick = {
            bitmap?.let { originalBitmap ->
              CoroutineScope(Dispatchers.IO).launch {
                try {
                  val croppedBitmap = applyCrop(
                    originalBitmap,
                    cropWidth,
                    cropHeight,
                    cropOffset,
                    canvasSize
                  )

                  val file = File(
                    context.cacheDir,
                    "cropped_${System.currentTimeMillis()}.jpg"
                  )
                  FileOutputStream(file).use { out ->
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                  }

                  onCropComplete(Uri.fromFile(file))
                } catch (e: Exception) {
                  e.printStackTrace()
                }
              }
            }
          }
        ) {
          Icon(Icons.Default.Check, contentDescription = "Apply Crop")
        }
      }
    ) { padding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black)
          .padding(padding)
      ) {
        imageBitmap?.let { imgBitmap ->
          Image(
            bitmap = imgBitmap,
            contentDescription = "Image to crop",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
          )
        }

        CropOverlay(
          cropWidth = cropWidth,
          cropHeight = cropHeight,
          cropOffset = cropOffset,
          onCanvasSizeChanged = { size ->
            canvasSize = size
            val (w, h, o) = clampCropToImage(
              cropWidth,
              cropHeight,
              cropOffset,
              size,
              imageBitmap?.width ?: 0,
              imageBitmap?.height ?: 0
            )
            cropWidth = w
            cropHeight = h
            cropOffset = o
          },
          onCropChanged = { width, height, offset ->
            val (w, h, o) = clampCropToImage(
              width,
              height,
              offset,
              canvasSize,
              imageBitmap?.width ?: 0,
              imageBitmap?.height ?: 0
            )
            cropWidth = w
            cropHeight = h
            cropOffset = o
          }
        )
      }
    }
  }
}

private fun clampCropToImage(
  width: Float,
  height: Float,
  offset: Offset,
  canvas: Size,
  imageWidthPx: Int,
  imageHeightPx: Int,
): Triple<Float, Float, Offset> {
  if (canvas.width <= 0f || canvas.height <= 0f) return Triple(width, height, offset)

  val minW = 100f
  val minH = 100f

  // If we don't have a valid image size yet, clamp to canvas bounds
  if (imageWidthPx <= 0 || imageHeightPx <= 0) {
    val maxW = canvas.width * 0.98f
    val maxH = canvas.height * 0.98f
    val clampedW = width.coerceIn(minW, maxW)
    val clampedH = height.coerceIn(minH, maxH)
    val halfW = clampedW / 2f
    val halfH = clampedH / 2f
    val maxOffsetX = (canvas.width / 2f) - halfW
    val maxOffsetY = (canvas.height / 2f) - halfH
    val clampedOffset = Offset(
      x = offset.x.coerceIn(-maxOffsetX, maxOffsetX),
      y = offset.y.coerceIn(-maxOffsetY, maxOffsetY)
    )
    return Triple(clampedW, clampedH, clampedOffset)
  }

  // Compute displayed image rect (ContentScale.Fit)
  val scaleX = canvas.width / imageWidthPx
  val scaleY = canvas.height / imageHeightPx
  val scale = min(scaleX, scaleY)
  val scaledImageWidth = imageWidthPx * scale
  val scaledImageHeight = imageHeightPx * scale

  val canvasCenterX = canvas.width / 2f
  val canvasCenterY = canvas.height / 2f
  val imageLeft = canvasCenterX - scaledImageWidth / 2f
  val imageTop = canvasCenterY - scaledImageHeight / 2f
  val imageRight = imageLeft + scaledImageWidth
  val imageBottom = imageTop + scaledImageHeight

  // Clamp width/height to image rect
  val clampedW = width.coerceIn(minW, scaledImageWidth)
  val clampedH = height.coerceIn(minH, scaledImageHeight)

  // Clamp rect center so the crop stays within image rect
  val halfW = clampedW / 2f
  val halfH = clampedH / 2f
  val desiredCenterX = canvasCenterX + offset.x
  val desiredCenterY = canvasCenterY + offset.y
  val clampedCenterX = desiredCenterX.coerceIn(imageLeft + halfW, imageRight - halfW)
  val clampedCenterY = desiredCenterY.coerceIn(imageTop + halfH, imageBottom - halfH)
  val clampedOffset = Offset(
    x = clampedCenterX - canvasCenterX,
    y = clampedCenterY - canvasCenterY
  )

  return Triple(clampedW, clampedH, clampedOffset)
}

@Composable
private fun CropOverlay(
  cropWidth: Float,
  cropHeight: Float,
  cropOffset: Offset,
  onCanvasSizeChanged: (Size) -> Unit,
  onCropChanged: (Float, Float, Offset) -> Unit,
) {


  var dragStart by remember { mutableStateOf(Offset.Zero) }
  var resizeMode by remember { mutableStateOf<String?>(null) }
  var canvasSize by remember { mutableStateOf(Size.Zero) }
  var startWidth by remember { mutableFloatStateOf(0f) }
  var startHeight by remember { mutableFloatStateOf(0f) }
  var startOffset by remember { mutableStateOf(Offset.Zero) }
  var startLeft by remember { mutableFloatStateOf(0f) }
  var startTop by remember { mutableFloatStateOf(0f) }
  var startRight by remember { mutableFloatStateOf(0f) }
  var startBottom by remember { mutableFloatStateOf(0f) }



  Box(modifier = Modifier.fillMaxSize()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      if (canvasSize != size) {
        canvasSize = size
        onCanvasSizeChanged(size)
      }

      val centerX = size.width / 2f
      val centerY = size.height / 2f

      val cropLeft = centerX + cropOffset.x - cropWidth / 2f
      val cropTop = centerY + cropOffset.y - cropHeight / 2f
      val cropRight = centerX + cropOffset.x + cropWidth / 2f
      val cropBottom = centerY + cropOffset.y + cropHeight / 2f
      1
      // overlays + border (unchanged)
      drawRect(Color.Black.copy(alpha = 0.5f), Offset(0f, 0f), Size(size.width, cropTop))
      drawRect(
        Color.Black.copy(alpha = 0.5f),
        Offset(0f, cropBottom),
        Size(size.width, size.height - cropBottom)
      )
      drawRect(Color.Black.copy(alpha = 0.5f), Offset(0f, cropTop), Size(cropLeft, cropHeight))
      drawRect(
        Color.Black.copy(alpha = 0.5f),
        Offset(cropRight, cropTop),
        Size(size.width - cropRight, cropHeight)
      )
      drawRect(
        Color.White,
        Offset(cropLeft, cropTop),
        Size(cropWidth, cropHeight),
        style = Stroke(width = 2f)
      )

      // Corner handles: white circles with blue borders
      val handleRadius = 12f
      val handleBorderWidth = 3f
      val blue = Color(0xFF2196F3)

      val topLeft = Offset(cropLeft, cropTop)
      val topRight = Offset(cropRight, cropTop)
      val bottomLeft = Offset(cropLeft, cropBottom)
      val bottomRight = Offset(cropRight, cropBottom)

      listOf(topLeft, topRight, bottomLeft, bottomRight).forEach { center ->
        drawCircle(color = Color.White, radius = handleRadius, center = center)
        drawCircle(
          color = blue,
          radius = handleRadius,
          center = center,
          style = Stroke(width = handleBorderWidth)
        )
      }
    }

    // Complete drag logic
    Box(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
          detectDragGestures(
            onDragStart = { offset ->
              dragStart = offset

              val centerX = canvasSize.width / 2f
              val centerY = canvasSize.height / 2f
              val cropLeft = centerX + cropOffset.x - cropWidth / 2f
              val cropTop = centerY + cropOffset.y - cropHeight / 2f
              val cropRight = centerX + cropOffset.x + cropWidth / 2f
              val cropBottom = centerY + cropOffset.y + cropHeight / 2f

              startLeft = cropLeft
              startTop = cropTop
              startRight = cropRight
              startBottom = cropBottom
              startWidth = cropWidth
              startHeight = cropHeight
              startOffset = cropOffset

              val handleSize = 36f
              val edgeTol = 32f

              // Priority: corners → edges → move
              resizeMode = when {
                // Corners
                offset.isInRect(cropLeft, cropTop, handleSize) -> "TOP_LEFT"
                offset.isInRect(cropRight, cropTop, handleSize) -> "TOP_RIGHT"
                offset.isInRect(cropLeft, cropBottom, handleSize) -> "BOTTOM_LEFT"
                offset.isInRect(cropRight, cropBottom, handleSize) -> "BOTTOM_RIGHT"

                // Edges
                offset.isInVerticalEdge(cropLeft, cropTop, cropBottom, edgeTol) -> "LEFT"
                offset.isInVerticalEdge(cropRight, cropTop, cropBottom, edgeTol) -> "RIGHT"
                offset.isInHorizontalEdge(cropTop, cropLeft, cropRight, edgeTol) -> "TOP"
                offset.isInHorizontalEdge(cropBottom, cropLeft, cropRight, edgeTol) -> "BOTTOM"

                // Move inside crop
                offset.x in cropLeft..cropRight && offset.y in cropTop..cropBottom -> "MOVE"

                else -> null
              }
            },
            onDragEnd = { resizeMode = null },
            onDrag = { change, _ ->
              change.consume()
              if (resizeMode == null) return@detectDragGestures

              val delta = change.position - dragStart
              val minW = 100f
              val minH = 100f

              var newLeft = startLeft
              var newTop = startTop
              var newRight = startRight
              var newBottom = startBottom

              when (resizeMode) {
                "TOP_LEFT" -> {
                  newLeft += delta.x; newTop += delta.y
                }

                "TOP_RIGHT" -> {
                  newRight += delta.x; newTop += delta.y
                }

                "BOTTOM_LEFT" -> {
                  newLeft += delta.x; newBottom += delta.y
                }

                "BOTTOM_RIGHT" -> {
                  newRight += delta.x; newBottom += delta.y
                }

                "LEFT" -> newLeft += delta.x
                "RIGHT" -> newRight += delta.x
                "TOP" -> newTop += delta.y
                "BOTTOM" -> newBottom += delta.y
                "MOVE" -> {
                  val newOffset = startOffset + delta
                  onCropChanged(startWidth, startHeight, newOffset)
                  return@detectDragGestures
                }
              }

              // Enforce min size
              if (newRight - newLeft < minW) newRight = newLeft + minW
              if (newBottom - newTop < minH) newBottom = newTop + minH

              val newWidth = newRight - newLeft
              val newHeight = newBottom - newTop
              val centerXNew = (newLeft + newRight) / 2f
              val centerYNew = (newTop + newBottom) / 2f
              val canvasCenterX = canvasSize.width / 2f
              val canvasCenterY = canvasSize.height / 2f
              val newOffset = Offset(centerXNew - canvasCenterX, centerYNew - canvasCenterY)

              onCropChanged(newWidth, newHeight, newOffset)
            }
          )
        }

    )
  }
}

private suspend fun applyCrop(
  originalBitmap: Bitmap,
  cropWidth: Float,
  cropHeight: Float,
  cropOffset: Offset,
  canvasSize: Size,
): Bitmap {
  return withContext(Dispatchers.Default) {
    val bitmapWidth = originalBitmap.width
    val bitmapHeight = originalBitmap.height

    val scaleX = canvasSize.width / bitmapWidth
    val scaleY = canvasSize.height / bitmapHeight
    val scale = min(scaleX, scaleY)

    val scaledWidth = bitmapWidth * scale
    val scaledHeight = bitmapHeight * scale

    val centerX = canvasSize.width / 2f
    val centerY = canvasSize.height / 2f
    val bitmapLeft = centerX - scaledWidth / 2f
    val bitmapTop = centerY - scaledHeight / 2f

    val cropLeft = centerX + cropOffset.x - cropWidth / 2f
    val cropTop = centerY + cropOffset.y - cropHeight / 2f

    val bitmapCropLeft = max(0f, (cropLeft - bitmapLeft) / scale)
    val bitmapCropTop = max(0f, (cropTop - bitmapTop) / scale)
    val bitmapCropWidth = min(bitmapWidth - bitmapCropLeft, cropWidth / scale)
    val bitmapCropHeight = min(bitmapHeight - bitmapCropTop, cropHeight / scale)

    Bitmap.createBitmap(
      originalBitmap,
      bitmapCropLeft.toInt(),
      bitmapCropTop.toInt(),
      bitmapCropWidth.toInt(),
      bitmapCropHeight.toInt()
    )
  }
}


private fun scaleBitmapToMaxSize(
  bitmap: Bitmap,
  maxWidth: Int,
  maxHeight: Int,
): Bitmap {
  val width = bitmap.width
  val height = bitmap.height

  // If bitmap is already within limits, return as is
  if (width <= maxWidth && height <= maxHeight) {
    return bitmap
  }

  // Calculate scale factor to fit within max dimensions
  val scaleWidth = maxWidth.toFloat() / width
  val scaleHeight = maxHeight.toFloat() / height
  val scale = min(scaleWidth, scaleHeight)

  val newWidth = (width * scale).toInt()
  val newHeight = (height * scale).toInt()

  return bitmap.scale(newWidth, newHeight).also {
    // Recycle the original bitmap if it's different from the scaled one
    if (it != bitmap) {
      bitmap.recycle()
    }
  }
}


private fun Offset.isInRect(left: Float, top: Float, size: Float) =
  x in (left - size)..(left + size) && y in (top - size)..(top + size)

private fun Offset.isInVerticalEdge(edgeX: Float, top: Float, bottom: Float, tol: Float) =
  x in (edgeX - tol)..(edgeX + tol) && y in top..bottom

private fun Offset.isInHorizontalEdge(edgeY: Float, left: Float, right: Float, tol: Float) =
  y in (edgeY - tol)..(edgeY + tol) && x in left..right
