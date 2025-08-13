package net.techandgraphics.wastical.domain

import net.techandgraphics.wastical.data.PaymentPeriod
import net.techandgraphics.wastical.data.Status
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.account.AccountEntity
import net.techandgraphics.wastical.data.local.database.account.AccountTitle
import net.techandgraphics.wastical.data.local.database.account.contact.AccountContactEntity
import net.techandgraphics.wastical.data.local.database.account.request.AccountRequestEntity
import net.techandgraphics.wastical.data.local.database.company.CompanyEntity
import net.techandgraphics.wastical.data.local.database.company.bin.collection.CompanyBinCollectionEntity
import net.techandgraphics.wastical.data.local.database.company.contact.CompanyContactEntity
import net.techandgraphics.wastical.data.local.database.company.location.CompanyLocationEntity
import net.techandgraphics.wastical.data.local.database.dashboard.payment.AccountWithPaymentStatusEntity
import net.techandgraphics.wastical.data.local.database.demographic.area.DemographicAreaEntity
import net.techandgraphics.wastical.data.local.database.demographic.district.DemographicDistrictEntity
import net.techandgraphics.wastical.data.local.database.demographic.street.DemographicStreetEntity
import net.techandgraphics.wastical.data.local.database.notification.NotificationEntity
import net.techandgraphics.wastical.data.local.database.notification.NotificationSyncStatus
import net.techandgraphics.wastical.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.wastical.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastical.data.local.database.payment.pay.month.covered.PaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastical.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.wastical.data.local.database.relations.CompanyLocationWithDemographicEntity
import net.techandgraphics.wastical.data.local.database.relations.PaymentMethodWithGatewayAndPlanEntity
import net.techandgraphics.wastical.data.local.database.relations.PaymentRequestWithAccountEntity
import net.techandgraphics.wastical.data.local.database.relations.PaymentWithAccountAndMethodWithGatewayEntity
import net.techandgraphics.wastical.data.local.database.relations.PaymentWithMonthsCoveredEntity
import net.techandgraphics.wastical.data.local.database.search.tag.SearchTagEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.domain.model.NotificationUiModel
import net.techandgraphics.wastical.domain.model.account.AccountContactUiModel
import net.techandgraphics.wastical.domain.model.account.AccountRequestUiModel
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
import net.techandgraphics.wastical.domain.model.search.SearchTagUiModel
import net.techandgraphics.wastical.notification.NotificationType

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
  account = account,
  paymentPlanId = paymentPlanId,
  paymentGatewayId = paymentGatewayId,
  createdAt = createdAt,
  updatedAt = updatedAt,
  isSelected = isSelected,
)

fun AccountEntity.toAccountUiModel() = AccountUiModel(
  id = id,
  uuid = uuid,
  title = AccountTitle.valueOf(title),
  firstname = firstname,
  lastname = lastname,
  username = username,
  role = role,
  email = email,
  status = Status.valueOf(status),
  companyId = companyId,
  leavingReason = leavingReason,
  leavingTimestamp = leavingTimestamp,
  updatedAt = updatedAt,
  latitude = latitude,
  longitude = longitude,
  createdAt = createdAt,
  companyLocationId = companyLocationId,
)

fun PaymentEntity.toPaymentUiModel() = PaymentUiModel(
  id = id,
  screenshotText = screenshotText,
  transactionId = transactionId,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  status = PaymentStatus.valueOf(status),
  createdAt = createdAt,
  updatedAt = updatedAt,
  companyId = companyId,
  executedById = accountId,
  paymentReference = paymentReference,
)

fun CompanyEntity.toCompanyUiModel() = CompanyUiModel(
  id = id,
  name = name,
  email = email,
  address = address,
  slogan = slogan,
  latitude = latitude,
  longitude = longitude,
  status = status,
  updatedAt = updatedAt,
  createdAt = createdAt,
)

fun CompanyContactEntity.toCompanyContactUiModel() = CompanyContactUiModel(
  id = id,
  email = email,
  contact = contact,
  primary = primary,
  companyId = companyId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun AccountContactEntity.toAccountContactUiModel() = AccountContactUiModel(
  id = id,
  uuid = uuid,
  email = email,
  contact = contact,
  primary = primary,
  accountId = accountId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun CompanyBinCollectionEntity.toCompanyBinCollectionUiModel() =
  CompanyBinCollectionUiModel(
    id = id,
    dayOfWeek = dayOfWeek,
    companyId = companyId,
    streetId = companyLocationId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun DemographicStreetEntity.toDemographicStreetUiModel() = DemographicStreetUiModel(
  id = id,
  name = name,
  latitude = latitude,
  longitude = longitude,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun DemographicAreaEntity.toDemographicAreaUiModel() = DemographicAreaUiModel(
  id = id,
  name = name,
  type = type,
  latitude = latitude,
  longitude = longitude,
  description = description,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun DemographicDistrictEntity.toDemographicDistrictUiModel() = DemographicDistrictUiModel(
  id = id,
  name = name,
  region = region,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun PaymentGatewayEntity.toPaymentGatewayUiModel() = PaymentGatewayUiModel(
  id = id,
  name = name,
  type = type,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun SearchTagEntity.toSearchTagUiModel() = SearchTagUiModel(
  query = query,
  tag = tag,
  timestamp = timestamp,
)

fun PaymentMethodWithGatewayAndPlanEntity.toPaymentMethodWithGatewayAndPlanUiModel() =
  PaymentMethodWithGatewayAndPlanUiModel(
    method = method.toPaymentMethodUiModel(),
    gateway = gateway.toPaymentGatewayUiModel(),
    plan = plan.toPaymentPlanUiModel(),
  )

fun PaymentWithAccountAndMethodWithGatewayEntity.toPaymentWithAccountAndMethodWithGatewayUiModel() =
  PaymentWithAccountAndMethodWithGatewayUiModel(
    payment = payment.toPaymentUiModel(),
    account = account.toAccountUiModel(),
    method = method.toPaymentMethodUiModel(),
    gateway = gateway.toPaymentGatewayUiModel(),
    plan = plan.toPaymentPlanUiModel(),
    coveredSize = coveredSize,
  )

fun AccountWithPaymentStatusEntity.toAccountWithPaymentStatusUiModel() =
  AccountWithPaymentStatusUiModel(
    account = account.toAccountUiModel(),
    hasPaid = hasPaid,
    amount = amount,
    offlinePay = offlinePay,
  )

fun CompanyLocationEntity.toCompanyLocationUiModel() = CompanyLocationUiModel(
  id = id,
  status = status,
  companyId = companyId,
  demographicStreetId = demographicStreetId,
  demographicAreaId = demographicAreaId,
  demographicDistrictId = demographicDistrictId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun PaymentRequestEntity.toPaymentRequestUiModel() = PaymentRequestUiModel(
  id = id,
  months = months,
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  companyId = companyId,
  executedById = executedById,
  status = status,
  createdAt = createdAt,
  paymentReference = paymentReference,
)

fun PaymentRequestWithAccountEntity.toPaymentRequestWithAccountUiModel() =
  PaymentRequestWithAccountUiModel(
    payment = payment.toPaymentRequestUiModel(),
    account = account.toAccountUiModel(),
    fee = fee,
  )

fun PaymentMonthCoveredEntity.toPaymentMonthCoveredUiModel() =
  PaymentMonthCoveredUiModel(
    id = id,
    month = month,
    year = year,
    paymentId = paymentId,
    accountId = accountId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun PaymentWithMonthsCoveredEntity.toPaymentWithMonthsCoveredUiModel() =
  PaymentWithMonthsCoveredUiModel(
    payment = payment.toPaymentUiModel(),
    covered = covered.map { it.toPaymentMonthCoveredUiModel() },
    account = account.toAccountUiModel(),
  )

fun CompanyLocationWithDemographicEntity.toCompanyLocationWithDemographicUiModel() =
  CompanyLocationWithDemographicUiModel(
    location = location.toCompanyLocationUiModel(),
    demographicArea = demographicArea.toDemographicAreaUiModel(),
    demographicStreet = demographicStreet.toDemographicStreetUiModel(),
  )

fun AccountRequestEntity.toAccountRequestUiModel() = AccountRequestUiModel(
  uuid = uuid,
  title = title,
  firstname = firstname,
  lastname = lastname,
  contact = contact,
  altContact = altContact,
  email = email,
  role = role,
  httpOperation = httpOperation,
  companyId = companyId,
  accountId = accountId,
  companyLocationId = companyLocationId,
  paymentPlanId = paymentPlanId,
  createdAt = createdAt,
  id = id,
)

fun NotificationEntity.toNotificationUiModel() =
  NotificationUiModel(
    id = id,
    uuid = uuid,
    body = body,
    title = title,
    isRead = isRead,
    recipientId = recipientId,
    recipientRole = AccountRole.valueOf(recipientRole),
    senderId = senderId,
    type = NotificationType.valueOf(type),
    metadata = metadata,
    deliveredAt = deliveredAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
    syncStatus = NotificationSyncStatus.entries.first { it.ordinal == syncStatus },
  )
