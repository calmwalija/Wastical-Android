package net.techandgraphics.wastemanagement.ui.screen.payment

import android.graphics.Bitmap
import coil.ImageLoader
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel

data class PaymentState(
  val numberOfMonths: Int = 1,
  val bitmapImage: Bitmap? = null,
  val imageLoader: ImageLoader? = null,
  val paymentPlans: List<PaymentPlanUiModel> = listOf(),
  val paymentMethods: List<PaymentMethodUiModel> = listOf(),
)
