package net.techandgraphics.wastemanagement.domain

import net.techandgraphics.wastemanagement.data.PaymentPeriod
import net.techandgraphics.wastemanagement.data.Status
import net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.data.local.database.account.contact.AccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.company.bin.collection.CompanyBinCollectionEntity
import net.techandgraphics.wastemanagement.data.local.database.company.contact.CompanyContactEntity
import net.techandgraphics.wastemanagement.data.local.database.company.location.CompanyLocationEntity
import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.AccountWithPaymentStatusEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.area.DemographicAreaEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.district.DemographicDistrictEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.DemographicStreetEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.relations.PaymentMethodWithGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.relations.PaymentWithAccountAndMethodWithGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.search.tag.SearchTagEntity
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.domain.model.account.AccountContactUiModel
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.account.AccountWithPaymentStatusUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.company.TrashCollectionScheduleUiModel
import net.techandgraphics.wastemanagement.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.wastemanagement.domain.model.demographic.DemographicDistrictUiModel
import net.techandgraphics.wastemanagement.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.CompanyLocationUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.domain.model.relations.PaymentMethodWithGatewayUiModel
import net.techandgraphics.wastemanagement.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastemanagement.domain.model.search.SearchTagUiModel

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

fun CompanyBinCollectionEntity.toTrashCollectionScheduleUiModel() =
  TrashCollectionScheduleUiModel(
    id = id,
    dayOfWeek = dayOfWeek,
    companyId = companyId,
    streetId = companyLocationId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun DemographicStreetEntity.toStreetUiModel() = DemographicStreetUiModel(
  id = id,
  name = name,
  latitude = latitude,
  longitude = longitude,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun DemographicAreaEntity.toAreaUiModel() = DemographicAreaUiModel(
  id = id,
  name = name,
  type = type,
  latitude = latitude,
  longitude = longitude,
  description = description,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun DemographicDistrictEntity.toDistrictUiModel() = DemographicDistrictUiModel(
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
  id = id,
)

fun PaymentMethodWithGatewayEntity.toPaymentMethodWithGatewayUiModel() =
  PaymentMethodWithGatewayUiModel(
    method = method.toPaymentMethodUiModel(),
    gateway = gateway.toPaymentGatewayUiModel(),
  )

fun PaymentWithAccountAndMethodWithGatewayEntity.toPaymentWithAccountAndMethodWithGatewayUiModel() =
  PaymentWithAccountAndMethodWithGatewayUiModel(
    payment = payment.toPaymentUiModel(),
    account = account.toAccountUiModel(),
    method = method.toPaymentMethodUiModel(),
    gateway = gateway.toPaymentGatewayUiModel(),
  )

fun AccountWithPaymentStatusEntity.toAccountWithPaymentStatusUiModel() =
  AccountWithPaymentStatusUiModel(
    account = account.toAccountUiModel(),
    hasPaid = hasPaid,
    amount = amount,
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
