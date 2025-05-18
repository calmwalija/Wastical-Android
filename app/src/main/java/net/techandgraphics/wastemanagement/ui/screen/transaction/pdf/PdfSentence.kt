package net.techandgraphics.wastemanagement.ui.screen.transaction.pdf

import android.graphics.Canvas
import android.graphics.Paint

fun Canvas.pdfSentence(
  theSentence: String,
  xAxis: Float,
  yAxis: Float,
  paint: Paint,
) {
  /***************************************************************/
  drawText(theSentence, xAxis, yAxis, paint)
  /***************************************************************/
}
