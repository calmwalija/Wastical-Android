package net.techandgraphics.wastemanagement.ui.screen.client.payment

import android.net.Uri
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface ClientPaymentEvent {

  data class Response(val isSuccess: Boolean, val error: String?) : ClientPaymentEvent
  data class AppState(val state: MainActivityState) : ClientPaymentEvent

  sealed interface Button : ClientPaymentEvent {
    data object Pay : ClientPaymentEvent
    data object ScreenshotAttached : ClientPaymentEvent
    data class ImageUri(val uri: Uri?) : ClientPaymentEvent
    data class ShowCropView(val show: Boolean) : ClientPaymentEvent
    data class NumberOfMonths(val isAdd: Boolean) : ClientPaymentEvent
    data class TextToClipboard(val text: String) : ClientPaymentEvent
    data class PaymentMethod(val method: PaymentMethodUiModel) : ClientPaymentEvent
  }

  sealed interface GoTo : ClientPaymentEvent {
    data object BackHandler : GoTo
  }
}
