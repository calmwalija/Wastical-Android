package net.techandgraphics.wastemanagement.data.remote

import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest

fun PaymentEntity.toPaymentRequest() = PaymentRequest(
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  numberOfMonths = numberOfMonths,
)
