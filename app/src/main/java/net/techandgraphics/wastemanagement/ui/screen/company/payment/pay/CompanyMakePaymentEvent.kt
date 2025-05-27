package net.techandgraphics.wastemanagement.ui.screen.company.payment.pay

import android.net.Uri

sealed interface CompanyMakePaymentEvent {

  sealed interface Button : CompanyMakePaymentEvent {
    data object Pay : Button
    data object ScreenshotAttached : Button
    data class ImageUri(val uri: Uri?) : Button
    data class ShowCropView(val show: Boolean) : Button
    data class NumberOfMonths(val isAdd: Boolean) : Button
    data class TextToClipboard(val text: String) : Button
  }

  sealed interface GoTo : CompanyMakePaymentEvent {
    data object BackHandler : GoTo
  }
}
