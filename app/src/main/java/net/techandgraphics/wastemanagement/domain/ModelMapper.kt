package net.techandgraphics.wastemanagement.domain

import net.techandgraphics.wastemanagement.data.PaymentPeriod
import net.techandgraphics.wastemanagement.data.Status
import net.techandgraphics.wastemanagement.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentType
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel

fun PaymentPlanEntity.toPaymentPlanUiModel() = PaymentPlanUiModel(
  id = id,
  fee = fee,
  name = name,
  period = PaymentPeriod.valueOf(period),
  status = Status.valueOf(status),
  companyId = companyId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun PaymentMethodEntity.toPaymentMethodUiModel() = PaymentMethodUiModel(
  id = id,
  name = name,
  type = PaymentType.valueOf(type),
  account = number,
  paymentPlanId = paymentPlanId,
  paymentGatewayId = paymentGatewayId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)
