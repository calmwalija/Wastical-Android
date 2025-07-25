package net.techandgraphics.wastical.ui.screen.client.invoice.pdf

import android.graphics.Canvas
import android.graphics.Paint
import net.techandgraphics.wastical.ui.screen.client.invoice.canvas.CanvasAxis

fun Canvas.pdfHeadingView(
  theHeading: String,
  yAxis: Float,
  xAxis: Float,
  paint: Paint,
): CanvasAxis {
  /***************************************************************/
  pdfSentence(theHeading, xAxis, yAxis, paint)
  /***************************************************************/

  var theYAxis = yAxis.plus(30)

  pdfLineView(theYAxis, xAxis, paint.measureText(theHeading).times(0.6f)).apply {
    theYAxis = this.yAxis
  }

  return CanvasAxis(xAxis, theYAxis)
}
