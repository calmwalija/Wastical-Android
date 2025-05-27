package net.techandgraphics.wastemanagement.data.remote

import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

fun PaymentEntity.toPaymentRequest() = PaymentRequest(
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  numberOfMonths = numberOfMonths,
)

fun PaymentUiModel.toPaymentRequest() = PaymentRequest(
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  numberOfMonths = numberOfMonths,
  status = status,
)
