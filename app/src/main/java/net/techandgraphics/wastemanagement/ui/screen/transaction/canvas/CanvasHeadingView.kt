package net.techandgraphics.wastemanagement.ui.screen.transaction.canvas

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas

fun DrawScope.canvasHeadingView(
  theHeading: String,
  theXAxis: Float,
  theYAxis: Float,
  paint: Paint,
): CanvasAxis {
  var verticalAxis = theYAxis

  /***************************************************************/
  with(theHeading) {
    drawContext.canvas.nativeCanvas.drawText(
      this@with,
      theXAxis,
      verticalAxis,
      paint,
    )
  }
  /***************************************************************/

  verticalAxis = verticalAxis.plus(30)

  drawRect(
    color = androidx.compose.ui.graphics.Color.LightGray,
    size = Size(paint.measureText(theHeading).times(0.6f), 10f),
    topLeft = Offset(theXAxis, verticalAxis),
  )

  verticalAxis = verticalAxis.plus(paint.textSize.plus(10))

  return CanvasAxis(theXAxis, verticalAxis)
}
