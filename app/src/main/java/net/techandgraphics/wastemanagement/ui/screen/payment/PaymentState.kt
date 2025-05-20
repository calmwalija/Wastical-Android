package net.techandgraphics.wastemanagement.ui.screen.payment

import android.graphics.Bitmap

data class PaymentState(
  val numberOfMonths: Int = 1,
  val bitmapImage: Bitmap? = null,
)
