package net.techandgraphics.wastemanagement.data.remote

import net.techandgraphics.wastemanagement.data.local.database.account.plan.AccountPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.account.plan.request.AccountPaymentPlanRequestEntity
import net.techandgraphics.wastemanagement.data.local.database.account.request.AccountRequestEntity
import net.techandgraphics.wastemanagement.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastemanagement.data.remote.account.AccountRequest
import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanRequest
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenRequest
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

fun PaymentRequestEntity.toPaymentRequest() = PaymentRequest(
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  companyId = companyId,
  months = months,
  executedById = executedById,
  createdAt = createdAt,
)

fun PaymentUiModel.toPaymentRequest() = PaymentRequest(
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  // TODO
  months = 2,
  status = status,
  companyId = companyId,
  executedById = executedById,
  createdAt = createdAt,
)

fun AccountFcmTokenEntity.toAccountFcmTokenRequest(accountId: Long) =
  AccountFcmTokenRequest(
    token = token,
    accountId = accountId,
  )

fun PaymentPlanUiModel.toAccountPaymentPlanRequest(account: AccountUiModel) =
  AccountPaymentPlanRequest(
    accountId = account.id,
    accountUuid = account.uuid,
    paymentPlanId = id,
  )

fun AccountRequestEntity.toAccountRequest() = AccountRequest(
  uuid = uuid,
  title = title,
  firstname = firstname,
  lastname = lastname,
  contacts = listOf(contact),
  email = email,
  companyId = companyId,
  companyLocationId = companyLocationId,
  paymentPlanId = paymentPlanId,
)

fun AccountPaymentPlanRequestEntity.toAccountPaymentPlanRequest() =
  AccountPaymentPlanRequest(
    accountUuid = accountUuid,
    accountId = accountId,
    paymentPlanId = paymentPlanId,
  )

fun AccountPaymentPlanEntity.toAccountPaymentPlanRequest() =
  AccountPaymentPlanRequest(
    accountUuid = accountUuid,
    accountId = accountId,
    paymentPlanId = paymentPlanId,
  )
