package net.techandgraphics.quantcal.data.local.database.relations

import net.techandgraphics.quantcal.data.local.database.account.AccountEntity
import net.techandgraphics.quantcal.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.quantcal.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.quantcal.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.quantcal.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.quantcal.data.local.database.query.PaymentWithAccountAndMethodWithGatewayQuery

data class PaymentWithAccountAndMethodWithGatewayEntity(
  val payment: PaymentEntity,
  val account: AccountEntity,
  val method: PaymentMethodEntity,
  val gateway: PaymentGatewayEntity,
  val plan: PaymentPlanEntity,
  val coveredSize: Int,
)

fun PaymentWithAccountAndMethodWithGatewayQuery.toEntity(): PaymentWithAccountAndMethodWithGatewayEntity {
  return PaymentWithAccountAndMethodWithGatewayEntity(
    payment = PaymentEntity(
      id = paymentId,
      screenshotText = screenshotText,
      transactionId = transactionId,
      paymentMethodId = paymentMethodId,
      accountId = accountId,
      status = paymentStatus,
      createdAt = paymentCreatedAt,
      updatedAt = paymentUpdatedAt,
      companyId = paymentCompanyId,
      executedById = executedById,
    ),
    account = AccountEntity(
      id = accId,
      uuid = uuid,
      title = title,
      firstname = firstname,
      lastname = lastname,
      username = username,
      email = email,
      latitude = latitude,
      longitude = longitude,
      status = accStatus,
      companyLocationId = companyLocationId,
      companyId = accCompanyId,
      leavingReason = leavingReason,
      leavingTimestamp = leavingTimestamp,
      updatedAt = accUpdatedAt,
      createdAt = accCreatedAt,
    ),
    method = PaymentMethodEntity(
      id = methodId,
      account = methodAccount,
      isSelected = isSelected,
      paymentPlanId = paymentPlanId,
      paymentGatewayId = paymentGatewayId,
      createdAt = methodCreatedAt,
      updatedAt = methodUpdatedAt,
    ),
    gateway = PaymentGatewayEntity(
      id = gatewayId,
      name = gatewayName,
      type = gatewayType,
      createdAt = gatewayCreatedAt,
      updatedAt = gatewayUpdatedAt,
    ),
    plan = PaymentPlanEntity(
      id = planId,
      fee = planFee,
      name = planName,
      period = planPeriod.name,
      status = planStatus.name,
      companyId = planCompanyId,
      createdAt = planCreatedAt,
      updatedAt = planUpdatedAt,
    ),
    coveredSize = coveredSize,
  )
}
