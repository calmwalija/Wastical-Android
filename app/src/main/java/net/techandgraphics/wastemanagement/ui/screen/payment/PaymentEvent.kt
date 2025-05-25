package net.techandgraphics.wastemanagement.ui.screen.payment

import android.net.Uri
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface PaymentEvent {

  data class Response(val isSuccess: Boolean, val error: String?) : PaymentEvent
  data class AppState(val state: MainActivityState) : PaymentEvent

  sealed interface Button : PaymentEvent {
    data object Pay : PaymentEvent
    data object ScreenshotAttached : PaymentEvent
    data class ImageUri(val uri: Uri?) : PaymentEvent
    data class ShowCropView(val show: Boolean) : PaymentEvent
    data class NumberOfMonths(val isAdd: Boolean) : PaymentEvent
    data class TextToClipboard(val text: String) : PaymentEvent
  }

  sealed interface GoTo : PaymentEvent {
    data object BackHandler : GoTo
  }
}
