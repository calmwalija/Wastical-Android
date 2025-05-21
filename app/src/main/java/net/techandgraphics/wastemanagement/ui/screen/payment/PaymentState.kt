package net.techandgraphics.wastemanagement.ui.screen.payment

import android.net.Uri
import coil.ImageLoader
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel

data class PaymentState(
  val numberOfMonths: Int = 1,
  val showCropView: Boolean = false,
  val screenshotAttached: Boolean = false,
  val imageUri: Uri? = null,
  val imageLoader: ImageLoader? = null,
  val lastPaymentId: Long = 1L,
  val screenshotText: String = "",
  val paymentPlans: List<PaymentPlanUiModel> = listOf(),
  val paymentMethods: List<PaymentMethodUiModel> = listOf(),
)
