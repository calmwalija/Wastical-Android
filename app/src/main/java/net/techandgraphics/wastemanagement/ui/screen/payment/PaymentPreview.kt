package net.techandgraphics.wastemanagement.ui.screen.payment

import android.content.Context
import net.techandgraphics.wastemanagement.data.PaymentPeriod
import net.techandgraphics.wastemanagement.data.Status
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentType
import net.techandgraphics.wastemanagement.di.ImageCacheModule
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel

internal val paymentPlan4Preview = PaymentPlanUiModel(
  id = 1L,
  fee = 10_000,
  name = "Premium",
  period = PaymentPeriod.Monthly,
  status = Status.Active,
  companyId = 1L,
  createdAt = System.currentTimeMillis(),
  updatedAt = null,
)

internal val paymentMethod4Preview = PaymentMethodUiModel(
  id = 1L,
  name = "National Bank",
  type = PaymentType.Bank,
  account = "1005099530",
  paymentPlanId = 1L,
  paymentGatewayId = 1L,
  createdAt = System.currentTimeMillis(),
  updatedAt = null,
)

internal fun imageLoader(context: Context) = ImageCacheModule.providesImageLoader(context)
