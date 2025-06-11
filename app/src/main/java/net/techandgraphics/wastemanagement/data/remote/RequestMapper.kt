package net.techandgraphics.wastemanagement.data.remote

import net.techandgraphics.wastemanagement.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanRequest
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenRequest
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

fun PaymentEntity.toPaymentRequest() = PaymentRequest(
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  companyId = companyId,
  months = months,
  executedById = executedById,
)

fun PaymentUiModel.toPaymentRequest() = PaymentRequest(
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  months = numberOfMonths,
  status = status,
  companyId = companyId,
  executedById = executedById,
)

fun AccountFcmTokenEntity.toAccountFcmTokenRequest(accountId: Long) =
  AccountFcmTokenRequest(
    token = token,
    accountId = accountId,
  )

fun PaymentPlanUiModel.toAccountPaymentPlanRequest(account: AccountUiModel) = AccountPaymentPlanRequest(
  accountId = account.id,
  accountUuid = account.uuid,
  paymentPlanId = id,
)
