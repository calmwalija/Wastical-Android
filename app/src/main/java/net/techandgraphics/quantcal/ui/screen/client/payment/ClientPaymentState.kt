package net.techandgraphics.quantcal.ui.screen.client.payment

import android.net.Uri
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel
import java.time.ZonedDateTime

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
    val timestamp: Long = ZonedDateTime.now().toEpochSecond(),

  ) : ClientPaymentState
}
