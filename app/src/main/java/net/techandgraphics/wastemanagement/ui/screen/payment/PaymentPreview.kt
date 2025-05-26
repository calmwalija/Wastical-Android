package net.techandgraphics.wastemanagement.ui.screen.payment

import android.content.Context
import net.techandgraphics.wastemanagement.data.PaymentPeriod
import net.techandgraphics.wastemanagement.data.Status
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentType
import net.techandgraphics.wastemanagement.di.ImageCacheModule
import net.techandgraphics.wastemanagement.domain.model.company.TrashCollectionScheduleUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState
import net.techandgraphics.wastemanagement.ui.screen.home.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.home.payment4Preview
import java.time.DayOfWeek
import java.time.ZonedDateTime

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

internal val trashSchedules4Preview = TrashCollectionScheduleUiModel(
  id = 1L,
  dayOfWeek = DayOfWeek.MONDAY.name,
  companyId = 1L,
  streetId = 1L,
  createdAt = ZonedDateTime.now().toEpochSecond(),
  updatedAt = null,
)

internal fun appState(context: Context) = MainActivityState(
  accounts = listOf(account4Preview),
  payments = listOf(payment4Preview, payment4Preview),
  invoices = listOf(payment4Preview, payment4Preview),
  paymentPlans = listOf(paymentPlan4Preview),
  paymentMethods = listOf(
    paymentMethod4Preview,
    paymentMethod4Preview,
    paymentMethod4Preview,
  ),
  imageLoader = imageLoader(context),
  trashSchedules = listOf(trashSchedules4Preview),
)

internal fun imageLoader(context: Context) = ImageCacheModule.providesImageLoader(context)
