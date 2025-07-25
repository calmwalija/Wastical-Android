package net.techandgraphics.wastical.ui.screen.company.payment.pay

import android.net.Uri
import coil.ImageLoader
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel

sealed interface CompanyMakePaymentState {
  object Loading : CompanyMakePaymentState

  data class Success(
    val account: AccountUiModel,
    val executedBy: AccountUiModel,
    val paymentPlan: PaymentPlanUiModel,
    val paymentMethods: List<PaymentMethodWithGatewayAndPlanUiModel> = listOf(),
    val imageLoader: ImageLoader,
    val company: CompanyUiModel,
    val numberOfMonths: Int = 1,
    val showCropView: Boolean = false,
    val screenshotAttached: Boolean = false,
    val imageUri: Uri? = null,
    val lastPaymentId: Long = 1L,
    val screenshotText: String = "",
    val demographic: CompanyLocationWithDemographicUiModel,
  ) : CompanyMakePaymentState
}
