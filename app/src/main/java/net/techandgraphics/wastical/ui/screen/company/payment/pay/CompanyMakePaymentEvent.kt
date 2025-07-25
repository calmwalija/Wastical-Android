package net.techandgraphics.wastical.ui.screen.company.payment.pay

import android.net.Uri
import net.techandgraphics.wastical.domain.model.payment.PaymentMethodUiModel

sealed interface CompanyMakePaymentEvent {

  data class Load(val id: Long) : CompanyMakePaymentEvent

  sealed interface Button : CompanyMakePaymentEvent {
    data object RecordPayment : Button
    data object ScreenshotAttached : Button
    data class ImageUri(val uri: Uri?) : Button
    data class ShowCropView(val show: Boolean) : Button
    data class NumberOfMonths(val isAdd: Boolean) : Button
    data class PaymentMethod(val method: PaymentMethodUiModel) : CompanyMakePaymentEvent
  }

  sealed interface GoTo : CompanyMakePaymentEvent {
    data object BackHandler : GoTo
  }
}
