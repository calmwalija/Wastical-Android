package net.techandgraphics.quantcal.data.remote

import net.techandgraphics.quantcal.data.local.database.AccountRole
import net.techandgraphics.quantcal.data.local.database.account.plan.AccountPaymentPlanEntity
import net.techandgraphics.quantcal.data.local.database.account.plan.request.AccountPaymentPlanRequestEntity
import net.techandgraphics.quantcal.data.local.database.account.request.AccountRequestEntity
import net.techandgraphics.quantcal.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.quantcal.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.quantcal.data.remote.account.AccountRequest
import net.techandgraphics.quantcal.data.remote.account.HttpOperation
import net.techandgraphics.quantcal.data.remote.account.plan.AccountPaymentPlanRequest
import net.techandgraphics.quantcal.data.remote.account.token.AccountFcmTokenRequest
import net.techandgraphics.quantcal.data.remote.payment.PaymentRequest
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel

fun PaymentRequestEntity.toPaymentRequest() = PaymentRequest(
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  companyId = companyId,
  months = months,
  executedById = executedById,
  createdAt = createdAt,
  updateAt = updatedAt,
  httpOperation = httpOperation,
  status = PaymentStatus.valueOf(status),
)

fun PaymentUiModel.toPaymentRequest(httpOperation: HttpOperation) = PaymentRequest(
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  // TODO
  months = 2,
  status = status,
  companyId = companyId,
  executedById = executedById,
  createdAt = createdAt,
  httpOperation = httpOperation.name,
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
    updatedAt = updatedAt,
    createdAt = createdAt,
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
  createdAt = createdAt,
  updateAt = updatedAt,
  role = AccountRole.valueOf(role),
  httpOperation = httpOperation,
  status = status,
  leavingReason = leavingReason,
  leavingTimestamp = leavingTimestamp,
)

fun AccountPaymentPlanRequestEntity.toAccountPaymentPlanRequest() =
  AccountPaymentPlanRequest(
    accountUuid = accountUuid,
    accountId = accountId,
    paymentPlanId = paymentPlanId,
    updatedAt = updatedAt,
    createdAt = createdAt,
  )

fun AccountPaymentPlanEntity.toAccountPaymentPlanRequest() =
  AccountPaymentPlanRequest(
    accountUuid = accountUuid,
    accountId = accountId,
    paymentPlanId = paymentPlanId,
    updatedAt = updatedAt,
    createdAt = createdAt,
  )
