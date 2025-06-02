package net.techandgraphics.wastemanagement.ui.screen.company.payment.pay

import android.net.Uri
import coil.ImageLoader
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel

sealed interface CompanyMakePaymentState {
  object Loading : CompanyMakePaymentState

  data class Success(
    val account: AccountUiModel,
    val paymentPlan: PaymentPlanUiModel,
    val paymentMethods: List<PaymentMethodUiModel> = listOf(),
    val imageLoader: ImageLoader,

    val numberOfMonths: Int = 1,
    val showCropView: Boolean = false,
    val screenshotAttached: Boolean = false,
    val imageUri: Uri? = null,
    val lastPaymentId: Long = 1L,
    val screenshotText: String = "",
  ) : CompanyMakePaymentState
}
