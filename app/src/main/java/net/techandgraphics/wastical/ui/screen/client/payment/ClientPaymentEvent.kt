package net.techandgraphics.wastical.ui.screen.client.payment

import android.net.Uri
import net.techandgraphics.wastical.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel

sealed interface ClientPaymentEvent {

  data class Load(val id: Long) : ClientPaymentEvent

  data class Response(val isSuccess: Boolean, val error: String?) : ClientPaymentEvent

  sealed interface Button : ClientPaymentEvent {
    data object Submit : Button
    data object AttachScreenshot : Button
    data object ScreenshotAttached : Button
    data object RemoveScreenshot : Button
    data class ImageUri(val uri: Uri?) : Button
    data class ShowCropView(val show: Boolean) : Button
    data class MonthCovered(val isAdd: Boolean) : Button
    data class PaymentMethod(val item: PaymentMethodWithGatewayAndPlanUiModel) : Button
  }

  sealed interface GoTo : ClientPaymentEvent {
    data object BackHandler : GoTo
  }
}
