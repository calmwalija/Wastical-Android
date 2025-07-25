package net.techandgraphics.wastical.ui.screen.client.payment

import android.net.Uri
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel

sealed interface ClientPaymentState {
  data object Loading : ClientPaymentState
  data class Success(

    val account: AccountUiModel,
    val company: CompanyUiModel,
    val paymentMethods: List<PaymentMethodWithGatewayAndPlanUiModel> = listOf(),
    val paymentPlan: PaymentPlanUiModel,

    val showCropView: Boolean = false,
    val monthsCovered: Int = 1,
    val screenshotAttached: Boolean = false,
    val imageUri: Uri? = null,
    val timestamp: Long = -1,

  ) : ClientPaymentState
}
