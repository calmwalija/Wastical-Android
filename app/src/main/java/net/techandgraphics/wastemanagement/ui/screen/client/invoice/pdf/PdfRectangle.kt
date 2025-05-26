package net.techandgraphics.wastemanagement.ui.screen.client.invoice.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

fun Canvas.pdfRectangle() {
  val paint = Paint().apply {
    color = Color.LTGRAY
    style = Paint.Style.STROKE
    strokeWidth = 20f
  }
  val left = 1200f
  val top = 120f
  val right = 1500f
  val bottom = 280f
  drawRect(left, top, right, bottom, paint)
}
