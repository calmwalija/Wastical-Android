package net.techandgraphics.wastical.ui.invoice.canvas

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import net.techandgraphics.wastical.R

fun DrawScope.canvasImage(
  theSentence: String,
  theHorizontalAxis: Float,
  theVerticalAxis: Float,
  context: Context,
) = with(theSentence) {
  val imageSize = 140
  val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
  val imageBitmap: ImageBitmap = bitmap.asImageBitmap()

  clipPath(
    Path().apply {
      addOval(
        Rect(
          Offset(theHorizontalAxis, theVerticalAxis),
          Size(imageSize.toFloat(), imageSize.toFloat()),
        ),
      )
    },
  ) {
    drawImage(
      image = imageBitmap,
      dstOffset = IntOffset(theHorizontalAxis.toInt(), theVerticalAxis.toInt()),
      dstSize = IntSize(imageSize, imageSize),
    )
  }
}
