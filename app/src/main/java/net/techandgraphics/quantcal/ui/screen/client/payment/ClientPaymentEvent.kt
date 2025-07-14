package net.techandgraphics.quantcal.ui.screen.client.payment

import android.net.Uri
import net.techandgraphics.quantcal.domain.model.payment.PaymentMethodUiModel

sealed interface ClientPaymentEvent {

  data class Load(val id: Long) : ClientPaymentEvent

  data class Response(val isSuccess: Boolean, val error: String?) : ClientPaymentEvent

  sealed interface Button : ClientPaymentEvent {
    data object Pay : ClientPaymentEvent
    data object ScreenshotAttached : ClientPaymentEvent
    data class ImageUri(val uri: Uri?) : ClientPaymentEvent
    data class ShowCropView(val show: Boolean) : ClientPaymentEvent
    data class MonthCovered(val isAdd: Boolean) : ClientPaymentEvent
    data class TextToClipboard(val text: String) : ClientPaymentEvent
    data class PaymentMethod(val method: PaymentMethodUiModel) : ClientPaymentEvent
  }

  sealed interface GoTo : ClientPaymentEvent {
    data object BackHandler : GoTo
  }
}
