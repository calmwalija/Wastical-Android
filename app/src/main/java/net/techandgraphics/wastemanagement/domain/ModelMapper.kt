package net.techandgraphics.wastemanagement.domain

import net.techandgraphics.wastemanagement.data.PaymentPeriod
import net.techandgraphics.wastemanagement.data.Status
import net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity
import net.techandgraphics.wastemanagement.data.local.database.account.AccountStreetEntity
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.data.local.database.account.contact.AccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.company.contact.CompanyContactEntity
import net.techandgraphics.wastemanagement.data.local.database.company.trash.collection.schedule.TrashCollectionScheduleEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.area.AreaEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.district.DistrictEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.StreetEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentAccountEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentType
import net.techandgraphics.wastemanagement.domain.model.account.AccountContactUiModel
import net.techandgraphics.wastemanagement.domain.model.account.AccountStreetUiModel
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.company.TrashCollectionScheduleUiModel
import net.techandgraphics.wastemanagement.domain.model.demographic.AreaUiModel
import net.techandgraphics.wastemanagement.domain.model.demographic.DistrictUiModel
import net.techandgraphics.wastemanagement.domain.model.demographic.StreetUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentAccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

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
)

fun PaymentEntity.toPaymentUiModel() = PaymentUiModel(
  id = id,
  screenshotText = screenshotText,
  numberOfMonths = numberOfMonths,
  transactionId = transactionId,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  status = PaymentStatus.valueOf(status),
  createdAt = createdAt,
  updatedAt = updatedAt,
  paymentPlanId = paymentPlanId,
  paymentPlanFee = paymentPlanFee,
  paymentPlanPeriod = paymentPlanPeriod,
  paymentGatewayId = paymentGatewayId,
  paymentGatewayType = paymentGatewayType,
  paymentGatewayName = paymentGatewayName,
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

fun TrashCollectionScheduleEntity.toTrashCollectionScheduleUiModel() =
  TrashCollectionScheduleUiModel(
    id = id,
    dayOfWeek = dayOfWeek,
    companyId = companyId,
    streetId = streetId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun StreetEntity.toStreetUiModel() = StreetUiModel(
  id = id,
  name = name,
  latitude = latitude,
  longitude = longitude,
  areaId = areaId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun AreaEntity.toAreaUiModel() = AreaUiModel(
  id = id,
  name = name,
  type = type,
  latitude = latitude,
  longitude = longitude,
  description = description,
  districtId = districtId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun DistrictEntity.toDistrictUiModel() = DistrictUiModel(
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

fun PaymentAccountEntity.toPaymentAccountUiModel() = PaymentAccountUiModel(
  payment = payment.toPaymentUiModel(),
  account = account.toAccountUiModel(),
)

fun AccountStreetEntity.toAccountStreetUiModel() = AccountStreetUiModel(
  account = account.toAccountUiModel(),
  street = street.toStreetUiModel(),
)
