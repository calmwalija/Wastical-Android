package net.techandgraphics.quantcal.ui.screen.company.payment.pay

import android.net.Uri
import coil.ImageLoader
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentMethodWithGatewayUiModel

sealed interface CompanyMakePaymentState {
  object Loading : CompanyMakePaymentState

  data class Success(
    val account: AccountUiModel,
    val paymentPlan: PaymentPlanUiModel,
    val paymentMethods: List<PaymentMethodWithGatewayUiModel> = listOf(),
    val imageLoader: ImageLoader,
    val company: CompanyUiModel,
    val numberOfMonths: Int = 1,
    val showCropView: Boolean = false,
    val screenshotAttached: Boolean = false,
    val imageUri: Uri? = null,
    val lastPaymentId: Long = 1L,
    val screenshotText: String = "",
  ) : CompanyMakePaymentState
}
