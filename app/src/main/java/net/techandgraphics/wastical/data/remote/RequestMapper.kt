package net.techandgraphics.wastical.data.remote

import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.account.plan.AccountPaymentPlanEntity
import net.techandgraphics.wastical.data.local.database.account.plan.request.AccountPaymentPlanRequestEntity
import net.techandgraphics.wastical.data.local.database.account.request.AccountRequestEntity
import net.techandgraphics.wastical.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.wastical.data.local.database.notification.request.NotificationRequestEntity
import net.techandgraphics.wastical.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastical.data.remote.account.AccountRequest
import net.techandgraphics.wastical.data.remote.account.plan.AccountPaymentPlanRequest
import net.techandgraphics.wastical.data.remote.account.token.AccountFcmTokenRequest
import net.techandgraphics.wastical.data.remote.notification.NotificationRequest
import net.techandgraphics.wastical.data.remote.payment.PaymentRequest
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel

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
  paymentReference = paymentReference,
  proofExt = proofExt,
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

fun NotificationRequestEntity.toNotificationRequest() =
  NotificationRequest(
    title = title,
    body = body,
    senderId = senderId,
    companyId = companyId,
    topic = topic,
    reference = reference,
    type = type,
    recipientRole = recipientRole,
    paymentId = paymentId,
    isRead = isRead,
    recipientId = recipientId,
    metadata = metadata,
    deliveredAt = deliveredAt,
    uuid = uuid,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )
