package net.techandgraphics.wastemanagement.data.remote.account

import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanResponse
import net.techandgraphics.wastemanagement.data.remote.account.session.AccountSessionResponse
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenRequest
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenResponse
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel

interface AccountApi {
  suspend fun get(): AccountSessionResponse
  suspend fun plan(plan: PaymentPlanUiModel, account: AccountUiModel): AccountPaymentPlanResponse
  suspend fun verify(contact: String): AccountSessionResponse
  suspend fun fcmToken(request: AccountFcmTokenRequest): AccountFcmTokenResponse
}
