package net.techandgraphics.wastical.ui.screen

import android.content.Context
import net.techandgraphics.wastical.data.PaymentPeriod
import net.techandgraphics.wastical.data.Status
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.account.AccountTitle
import net.techandgraphics.wastical.data.local.database.account.request.AccountRequestEntity
import net.techandgraphics.wastical.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.di.ImageCacheModule
import net.techandgraphics.wastical.domain.model.account.AccountInfoUiModel
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.account.AccountWithPaymentStatusUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyBinCollectionUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicDistrictUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.wastical.domain.model.payment.CompanyLocationUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentMonthCoveredUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentRequestUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentRequestWithAccountUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithMonthsCoveredUiModel
import java.time.DayOfWeek
import java.time.ZonedDateTime
import kotlin.random.Random

internal val account4Preview = AccountUiModel(
  id = 1L,
  uuid = "",
  title = AccountTitle.DR,
  firstname = "Lorem",
  lastname = "Ipsum",
  username = "999001122",
  email = "example@email.com",
  status = Status.Active,
  companyId = 1L,
  role = AccountRole.Client.name,
  createdAt = System.currentTimeMillis(),
  leavingTimestamp = null,
  companyLocationId = 1L,
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
  account = "1005099530",
  paymentPlanId = 1L,
  paymentGatewayId = 1L,
  createdAt = System.currentTimeMillis(),
  updatedAt = ZonedDateTime.now().toEpochSecond(),
  isSelected = true,
)

internal val trashSchedules4Preview = CompanyBinCollectionUiModel(
  id = 1L,
  dayOfWeek = DayOfWeek.MONDAY.name,
  companyId = 1L,
  streetId = 1L,
  createdAt = ZonedDateTime.now().toEpochSecond(),
  updatedAt = ZonedDateTime.now().toEpochSecond(),
)

internal val paymentGateway4Preview = PaymentGatewayUiModel(
  id = 1L,
  name = "Airtel Money",
  type = "Wallet",
  createdAt = ZonedDateTime.now().toEpochSecond(),
  updatedAt = ZonedDateTime.now().toEpochSecond(),
)

internal val company4Preview = CompanyUiModel(
  id = 1L,
  name = "Tech And Graphics TAG, Inc.",
  email = "example@email.com",
  slogan = "Lorem Ipsum",
  address = "John Smith, 123 Main Street, Suite 2, Downtown, CA 91234, GA",
  createdAt = ZonedDateTime.now().toEpochSecond(),
  updatedAt = ZonedDateTime.now().toEpochSecond(),
)

internal val companyContact4Preview = CompanyContactUiModel(
  id = 1,
  email = "example@email.com",
  contact = "999112233",
  primary = true,
  companyId = 1,
  createdAt = 1,
  updatedAt = 1,
)

internal val payment4Preview = PaymentUiModel(
  id = 1L,
  status = PaymentStatus.Approved,
  transactionId = "TXN-5983-1747899108",
  paymentMethodId = 1L,
  createdAt = ZonedDateTime.now().toEpochSecond(),
  screenshotText = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
  accountId = account4Preview.id,
  updatedAt = ZonedDateTime.now().toEpochSecond(),
  companyId = account4Preview.companyId,
  executedById = account4Preview.id,
)

internal val demographicStreet4Preview = DemographicStreetUiModel(
  id = 1L,
  name = "Sector 5",
  latitude = -1F,
  longitude = -1F,
  createdAt = 1L,
  updatedAt = 1L,
  belongTo = true,
)

internal val demographicDistrict4Preview = DemographicDistrictUiModel(
  id = 1L,
  name = "Lilongwe",
  createdAt = 1L,
  updatedAt = 1L,
  region = "Central",
)

internal val companyLocation4Preview = CompanyLocationUiModel(
  id = 1,
  status = Status.Active.name,
  companyId = 1,
  demographicStreetId = 1,
  demographicAreaId = 1,
  demographicDistrictId = 1,
  createdAt = 1,
  updatedAt = 1,
)

internal val payment4CurrentLocationMonth4Preview = Payment4CurrentLocationMonth(
  streetName = "Area 25",
  areaName = "Sector 3",
  totalAccounts = 10,
  paidAccounts = 3,
  streetId = 1,
  districtName = "Lilongwe",
  districtRegion = "Central",
  latitude = -1f,
  longitude = -1f,
)

internal val accountWithStreetAndArea4Preview = AccountInfoUiModel(
  lastname = account4Preview.lastname,
  firstname = account4Preview.firstname,
  accountId = account4Preview.id,
  streetName = demographicStreet4Preview.name,
  areaName = demographicStreet4Preview.name,
  title = AccountTitle.MR.name,
  username = account4Preview.username,
)

internal val demographicArea4Preview = DemographicAreaUiModel(
  id = 1,
  name = "Lorem Area",
  type = "",
  description = "",
  latitude = 1f,
  longitude = 1f,
  createdAt = 1,
  updatedAt = 1,
)

internal val accountInfo4Preview = AccountInfoUiModel(
  lastname = account4Preview.lastname,
  firstname = account4Preview.firstname,
  title = account4Preview.title.name,
  username = account4Preview.username,
  accountId = account4Preview.id,
  streetName = demographicStreet4Preview.name,
  areaName = demographicArea4Preview.name,
)

internal val paymentMethodWithGatewayAndPlan4Preview =
  PaymentMethodWithGatewayAndPlanUiModel(
    paymentMethod4Preview,
    paymentGateway4Preview,
    paymentPlan4Preview,
  )

internal val paymentWithAccountAndMethodWithGateway4Preview =
  PaymentWithAccountAndMethodWithGatewayUiModel(
    payment4Preview,
    account4Preview,
    paymentMethod4Preview,
    paymentGateway4Preview,
    paymentPlan4Preview,
    coveredSize = 3,
  )

internal val accountWithPaymentStatus4Preview =
  AccountWithPaymentStatusUiModel(account4Preview, Random.nextBoolean(), 10_000)

internal val paymentRequest4Preview = PaymentRequestUiModel(
  id = 1,
  months = 1,
  screenshotText = "",
  paymentMethodId = 1,
  accountId = 1,
  companyId = 1,
  executedById = 1,
  status = PaymentStatus.Waiting.name,
  createdAt = ZonedDateTime.now().toEpochSecond(),
)

internal val paymentRequestWithAccount4Preview = PaymentRequestWithAccountUiModel(
  account = account4Preview,
  payment = paymentRequest4Preview,
  fee = 10_000,
)

internal val paymentMonthCoveredUiModel = PaymentMonthCoveredUiModel(
  id = 1,
  month = 6,
  year = 2025,
  paymentId = 1,
  accountId = 1,
  createdAt = 1,
  updatedAt = 1,
)

internal val paymentWithMonthsCovered4Preview = PaymentWithMonthsCoveredUiModel(
  payment = payment4Preview,
  covered = (1..4).map { paymentMonthCoveredUiModel },
  account = account4Preview,
)

internal val companyLocationWithDemographic4Preview = CompanyLocationWithDemographicUiModel(
  location = companyLocation4Preview,
  demographicArea = demographicArea4Preview,
  demographicStreet = demographicStreet4Preview,
)

internal val accountRequest4Preview = AccountRequestEntity(
  title = account4Preview.title,
  firstname = account4Preview.firstname,
  lastname = account4Preview.lastname,
  contact = "999002233",
  altContact = "889900332",
  email = account4Preview.email,
  companyId = 1L,
  role = AccountRole.Client.name,
  status = Status.Active.name,
  accountId = 1L,
  companyLocationId = 1L,
  paymentPlanId = 1L,
  createdAt = 1,
  updatedAt = 1,
)

internal fun imageLoader(context: Context) = ImageCacheModule.providesImageLoader(context)
