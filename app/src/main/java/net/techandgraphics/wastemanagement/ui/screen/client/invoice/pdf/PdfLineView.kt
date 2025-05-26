package net.techandgraphics.wastemanagement.ui.screen.client.invoice.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.canvas.CanvasAxis

fun Canvas.pdfLineView(
  yAxis: Float,
  xAxis: Float,
  lineWidth: Float,
): CanvasAxis {
  val startX = xAxis
  val startY = yAxis
  val stopY = yAxis
  val stopX = xAxis.plus(lineWidth)

  val paint = Paint().apply {
    color = Color.GRAY
    strokeWidth = 10f
    style = Paint.Style.STROKE
  }

  /***************************************************************/
  drawLine(startX, startY, stopX, stopY, paint)
  /***************************************************************/

  return CanvasAxis(xAxis, yAxis.plus(60))
}
