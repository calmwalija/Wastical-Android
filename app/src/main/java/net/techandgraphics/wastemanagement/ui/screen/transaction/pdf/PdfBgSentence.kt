package net.techandgraphics.wastemanagement.ui.screen.transaction.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import net.techandgraphics.wastemanagement.ui.screen.transaction.canvas.CanvasAxis

fun Canvas.pdfBgSentence(
  theSentence: String,
  xAxis: Float,
  yAxis: Float,
  paint: Paint,
  withBg: Boolean,
): CanvasAxis {
  val theYAxis = yAxis.minus(10)
  /***************************************************************/
  if (withBg) {
    drawLine(
      xAxis.minus(20),
      theYAxis,
      1500f,
      theYAxis,
      Paint().apply {
        style = Paint.Style.FILL
        color = Color.LTGRAY
        strokeWidth = 60f
      },
    )
  }
  drawText(theSentence, xAxis, yAxis, paint)
  /***************************************************************/
  return CanvasAxis(xAxis, yAxis)
}
