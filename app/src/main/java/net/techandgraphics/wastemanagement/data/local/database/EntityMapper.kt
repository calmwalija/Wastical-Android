package net.techandgraphics.wastemanagement.data.local.database

import net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity
import net.techandgraphics.wastemanagement.data.local.database.account.contact.AccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.account.plan.AccountPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.company.bin.collection.CompanyBinCollectionEntity
import net.techandgraphics.wastemanagement.data.local.database.company.contact.CompanyContactEntity
import net.techandgraphics.wastemanagement.data.local.database.company.location.CompanyLocationEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.area.DemographicAreaEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.district.DemographicDistrictEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.DemographicStreetEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.collection.PaymentCollectionDayEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.month.covered.PaymentMonthCoveredEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.search.tag.SearchTagEntity
import net.techandgraphics.wastemanagement.data.remote.account.AccountResponse
import net.techandgraphics.wastemanagement.data.remote.account.contact.AccountContactResponse
import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanResponse
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenResponse
import net.techandgraphics.wastemanagement.data.remote.company.CompanyContactResponse
import net.techandgraphics.wastemanagement.data.remote.company.CompanyResponse
import net.techandgraphics.wastemanagement.data.remote.company.bin.collection.CompanyBinCollectionResponse
import net.techandgraphics.wastemanagement.data.remote.company.location.CompanyLocationResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.DemographicAreaResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.DemographicDistrictResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.DemographicStreetResponse
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.data.remote.payment.collection.PaymentCollectionDayResponse
import net.techandgraphics.wastemanagement.data.remote.payment.gateway.PaymentGatewayResponse
import net.techandgraphics.wastemanagement.data.remote.payment.method.PaymentMethodResponse
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentResponse
import net.techandgraphics.wastemanagement.data.remote.payment.pay.month.covered.PaymentMonthCoveredResponse
import net.techandgraphics.wastemanagement.data.remote.payment.plan.PaymentPlanResponse
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.search.SearchTagUiModel

fun CompanyResponse.toCompanyEntity() = CompanyEntity(
  id = id,
  name = name,
  latitude = latitude,
  email = email,
  longitude = longitude,
  status = status,
  address = address,
  slogan = slogan,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun CompanyContactResponse.toCompanyContactEntity() = CompanyContactEntity(
  id = id,
  email = email,
  contact = contact,
  primary = primary,
  companyId = companyId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun PaymentMethodResponse.toPaymentMethodEntity() =
  PaymentMethodEntity(
    id = id,
    account = account,
    paymentPlanId = paymentPlanId,
    paymentGatewayId = paymentGatewayId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun PaymentPlanResponse.toPaymentPlanEntity() = PaymentPlanEntity(
  id = id,
  fee = fee,
  name = name,
  period = period,
  status = status,
  companyId = companyId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun PaymentResponse.toPaymentEntity() = PaymentEntity(
  id = id,
  status = status,
  accountId = accountId,
  screenshotText = screenshotText,
  transactionId = transactionId,
  paymentMethodId = paymentMethodId,
  createdAt = createdAt,
  updatedAt = updatedAt,
  companyId = companyId,
  executedById = executedById,
)

fun AccountResponse.toAccountEntity() = AccountEntity(
  id = id,
  uuid = uuid,
  title = title,
  firstname = firstname,
  lastname = lastname,
  username = username,
  email = email,
  latitude = latitude,
  longitude = longitude,
  status = status,
  companyId = companyId,
  leavingReason = leavingReason,
  leavingTimestamp = leavingTimestamp,
  updatedAt = updatedAt,
  createdAt = createdAt,
  companyLocationId = companyLocationId,
)

fun AccountContactResponse.toAccountContactEntity() = AccountContactEntity(
  id = id,
  uuid = uuid,
  email = email,
  contact = contact,
  primary = primary,
  accountId = accountId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun PaymentRequest.toPaymentRequestEntity() = PaymentRequestEntity(
  status = PaymentStatus.Waiting.name,
  accountId = accountId,
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  companyId = companyId,
  executedById = executedById,
  months = months,
)

fun DemographicDistrictResponse.toDemographicDistrictEntity() = DemographicDistrictEntity(
  id = id,
  name = name,
  region = region,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun DemographicAreaResponse.toDemographicAreaEntity() = DemographicAreaEntity(
  id = id,
  name = name,
  type = type,
  latitude = latitude,
  longitude = longitude,
  description = description,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun DemographicStreetResponse.toDemographicStreetEntity() = DemographicStreetEntity(
  id = id,
  name = name,
  latitude = latitude,
  longitude = longitude,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun CompanyBinCollectionResponse.toCompanyBinCollectionEntity() =
  CompanyBinCollectionEntity(
    id = id,
    dayOfWeek = dayOfWeek.name,
    companyId = companyId,
    companyLocationId = companyLocationId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun PaymentCollectionDayResponse.toPaymentCollectionDayEntity() =
  PaymentCollectionDayEntity(
    id = id,
    day = day,
    companyId = companyId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun AccountPaymentPlanResponse.toAccountPaymentPlanEntity() =
  AccountPaymentPlanEntity(
    id = id,
    accountUuid = accountUuid,
    accountId = accountId,
    paymentPlanId = paymentPlanId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun PaymentGatewayResponse.toPaymentGatewayEntity() = PaymentGatewayEntity(
  id = id,
  name = name,
  type = type,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun PaymentMethodUiModel.toPaymentMethodEntity() = PaymentMethodEntity(
  id = id,
  account = account,
  paymentPlanId = paymentPlanId,
  paymentGatewayId = paymentGatewayId,
  createdAt = createdAt,
  updatedAt = updatedAt,
  isSelected = isSelected,
)

fun AccountFcmTokenResponse.toAccountFcmTokenEntity() = AccountFcmTokenEntity(
  token = token,
  sync = false,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun PaymentMonthCoveredResponse.toPaymentMonthCoveredEntity() =
  PaymentMonthCoveredEntity(
    id = id,
    month = month,
    year = year,
    paymentId = paymentId,
    accountId = accountId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun CompanyLocationResponse.toCompanyLocationRequest() = CompanyLocationEntity(
  id = id,
  status = status.name,
  companyId = companyId,
  demographicStreetId = demographicStreetId,
  demographicAreaId = demographicAreaId,
  demographicDistrictId = demographicDistrictId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun SearchTagUiModel.toSearchTagEntity() = SearchTagEntity(
  query = query,
  tag = tag,
  timestamp = timestamp,
  id = id,
)
