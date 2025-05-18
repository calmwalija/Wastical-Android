package net.techandgraphics.wastemanagement.ui.screen.transaction.canvas

import android.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas

fun DrawScope.canvasSentence(
  theSentence: String,
  theXAxis: Float,
  theYAxis: Float,
  paint: Paint,
) = with(theSentence) {
  drawContext.canvas.nativeCanvas.drawText(
    this,
    theXAxis,
    theYAxis,
    paint,
  )
}
