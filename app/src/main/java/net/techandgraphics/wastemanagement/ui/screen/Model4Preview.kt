package net.techandgraphics.wastemanagement.ui.screen

import android.content.Context
import net.techandgraphics.wastemanagement.data.PaymentPeriod
import net.techandgraphics.wastemanagement.data.Status
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentType
import net.techandgraphics.wastemanagement.di.ImageCacheModule
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.company.TrashCollectionScheduleUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentAccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState
import java.time.DayOfWeek
import java.time.ZonedDateTime

internal val account4Preview = AccountUiModel(
  id = 1L,
  uuid = "",
  title = AccountTitle.DR,
  firstname = "Lorem",
  lastname = "Ipsum",
  username = "lorem.ipsum",
  email = "example@email.com",
  status = Status.Active,
  companyId = 1L,
  createdAt = System.currentTimeMillis(),
  leavingTimestamp = null,
  updatedAt = ZonedDateTime.now().toEpochSecond(),
)

internal val paymentPlan4Preview = PaymentPlanUiModel(
  id = 1L,
  fee = 10_000,
  name = "Premium",
  period = PaymentPeriod.Monthly,
  status = Status.Active,
  companyId = 1L,
  createdAt = System.currentTimeMillis(),
  updatedAt = ZonedDateTime.now().toEpochSecond(),
)

internal val paymentMethod4Preview = PaymentMethodUiModel(
  id = 1L,
  name = "National Bank",
  type = PaymentType.Bank,
  account = "1005099530",
  paymentPlanId = 1L,
  paymentGatewayId = 1L,
  createdAt = System.currentTimeMillis(),
  updatedAt = ZonedDateTime.now().toEpochSecond(),
  isSelected = true,
)

internal val trashSchedules4Preview = TrashCollectionScheduleUiModel(
  id = 1L,
  dayOfWeek = DayOfWeek.MONDAY.name,
  companyId = 1L,
  streetId = 1L,
  createdAt = ZonedDateTime.now().toEpochSecond(),
  updatedAt = ZonedDateTime.now().toEpochSecond(),
)

internal val gateway4Preview = PaymentGatewayUiModel(
  id = 1L,
  name = "Airtel Money",
  type = "Wallet",
  createdAt = ZonedDateTime.now().toEpochSecond(),
  updatedAt = ZonedDateTime.now().toEpochSecond(),
)

internal val company4Preview = CompanyUiModel(
  id = 1L,
  name = "Adams Resources & Energy, Inc.",
  email = "example@email.com",
  slogan = "Lorem Ipsum",
  address = "John Smith, 123 Main Street, Suite 2, Downtown, CA 91234, GA",
  createdAt = ZonedDateTime.now().toEpochSecond(),
  updatedAt = ZonedDateTime.now().toEpochSecond(),
)

internal val payment4Preview = PaymentUiModel(
  id = 1L,
  status = PaymentStatus.Approved,
  numberOfMonths = 1,
  transactionId = "TXN-5983-1747899108",
  paymentMethodId = 1L,
  createdAt = ZonedDateTime.now().toEpochSecond(),
  screenshotText = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
  accountId = 1L,
  updatedAt = ZonedDateTime.now().toEpochSecond(),
  paymentPlanId = 1L,
  paymentPlanFee = paymentPlan4Preview.fee,
  paymentPlanPeriod = paymentPlan4Preview.period.name,
  paymentGatewayId = gateway4Preview.id,
  paymentGatewayName = gateway4Preview.name,
  paymentGatewayType = gateway4Preview.type,
  companyId = account4Preview.companyId,
)

internal val paymentAccount4Preview = PaymentAccountUiModel(
  payment = payment4Preview,
  account = account4Preview,
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
