package net.techandgraphics.wastemanagement.ui.screen.payment

import android.graphics.Bitmap

sealed interface PaymentEvent {

  sealed interface Button : PaymentEvent {
    data object Screenshot : PaymentEvent
    data class Pay(val message: String) : PaymentEvent
    data class ImageBitmap(val bitmap: Bitmap) : PaymentEvent
    data class NumberOfMonths(val isAdd: Boolean) : PaymentEvent
  }
}
