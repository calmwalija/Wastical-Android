package net.techandgraphics.quantcal.data.local.database

import net.techandgraphics.quantcal.data.local.database.account.AccountEntity
import net.techandgraphics.quantcal.data.local.database.account.contact.AccountContactEntity
import net.techandgraphics.quantcal.data.local.database.account.plan.AccountPaymentPlanEntity
import net.techandgraphics.quantcal.data.local.database.account.plan.request.AccountPaymentPlanRequestEntity
import net.techandgraphics.quantcal.data.local.database.account.request.AccountRequestEntity
import net.techandgraphics.quantcal.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.quantcal.data.local.database.company.CompanyEntity
import net.techandgraphics.quantcal.data.local.database.company.bin.collection.CompanyBinCollectionEntity
import net.techandgraphics.quantcal.data.local.database.company.contact.CompanyContactEntity
import net.techandgraphics.quantcal.data.local.database.company.location.CompanyLocationEntity
import net.techandgraphics.quantcal.data.local.database.demographic.area.DemographicAreaEntity
import net.techandgraphics.quantcal.data.local.database.demographic.district.DemographicDistrictEntity
import net.techandgraphics.quantcal.data.local.database.demographic.street.DemographicStreetEntity
import net.techandgraphics.quantcal.data.local.database.payment.collection.PaymentCollectionDayEntity
import net.techandgraphics.quantcal.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.quantcal.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.quantcal.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.quantcal.data.local.database.payment.pay.month.covered.PaymentMonthCoveredEntity
import net.techandgraphics.quantcal.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.quantcal.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.quantcal.data.local.database.relations.CompanyLocationWithDemographicEntity
import net.techandgraphics.quantcal.data.local.database.relations.PaymentRequestWithAccountEntity
import net.techandgraphics.quantcal.data.local.database.search.tag.SearchTagEntity
import net.techandgraphics.quantcal.data.remote.account.AccountResponse
import net.techandgraphics.quantcal.data.remote.account.contact.AccountContactResponse
import net.techandgraphics.quantcal.data.remote.account.plan.AccountPaymentPlanRequest
import net.techandgraphics.quantcal.data.remote.account.plan.AccountPaymentPlanResponse
import net.techandgraphics.quantcal.data.remote.account.token.AccountFcmTokenResponse
import net.techandgraphics.quantcal.data.remote.company.CompanyContactResponse
import net.techandgraphics.quantcal.data.remote.company.CompanyResponse
import net.techandgraphics.quantcal.data.remote.company.bin.collection.CompanyBinCollectionResponse
import net.techandgraphics.quantcal.data.remote.company.location.CompanyLocationResponse
import net.techandgraphics.quantcal.data.remote.demographic.DemographicAreaResponse
import net.techandgraphics.quantcal.data.remote.demographic.DemographicDistrictResponse
import net.techandgraphics.quantcal.data.remote.demographic.DemographicStreetResponse
import net.techandgraphics.quantcal.data.remote.payment.PaymentRequest
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.data.remote.payment.collection.PaymentCollectionDayResponse
import net.techandgraphics.quantcal.data.remote.payment.gateway.PaymentGatewayResponse
import net.techandgraphics.quantcal.data.remote.payment.method.PaymentMethodResponse
import net.techandgraphics.quantcal.data.remote.payment.pay.PaymentResponse
import net.techandgraphics.quantcal.data.remote.payment.pay.month.covered.PaymentMonthCoveredResponse
import net.techandgraphics.quantcal.data.remote.payment.plan.PaymentPlanResponse
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.quantcal.domain.model.demographic.DemographicDistrictUiModel
import net.techandgraphics.quantcal.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.quantcal.domain.model.payment.CompanyLocationUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentRequestUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentRequestWithAccountUiModel
import net.techandgraphics.quantcal.domain.model.search.SearchTagUiModel

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

fun CompanyUiModel.toCompanyEntity() = CompanyEntity(
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

fun PaymentGatewayUiModel.toPaymentGatewayEntity() =
  PaymentGatewayEntity(
    id = id,
    name = name,
    type = type,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun AccountUiModel.toAccountEntity() = AccountEntity(
  id = id,
  uuid = uuid,
  title = title.name,
  firstname = firstname,
  lastname = lastname,
  username = username,
  email = email,
  latitude = latitude,
  longitude = longitude,
  status = status.name,
  companyId = companyId,
  leavingReason = leavingReason,
  leavingTimestamp = leavingTimestamp,
  updatedAt = updatedAt,
  createdAt = createdAt,
  companyLocationId = companyLocationId,
)

fun PaymentPlanUiModel.toPaymentPlanEntity() = PaymentPlanEntity(
  id = id,
  fee = fee,
  name = name,
  period = period.name,
  status = status.name,
  companyId = companyId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun DemographicDistrictUiModel.toDemographicDistrictEntity() = DemographicDistrictEntity(
  id = id,
  name = name,
  region = region,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun DemographicAreaUiModel.toDemographicAreaEntity() = DemographicAreaEntity(
  id = id,
  name = name,
  type = type,
  latitude = latitude,
  longitude = longitude,
  description = description,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun DemographicStreetUiModel.toDemographicStreetEntity() = DemographicStreetEntity(
  id = id,
  name = name,
  latitude = latitude,
  longitude = longitude,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun CompanyLocationUiModel.toCompanyLocationEntity() = CompanyLocationEntity(
  id = id,
  status = status,
  companyId = companyId,
  demographicStreetId = demographicStreetId,
  demographicAreaId = demographicAreaId,
  demographicDistrictId = demographicDistrictId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun PaymentRequestUiModel.toPaymentRequestEntity() = PaymentRequestEntity(
  id = id,
  months = months,
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  accountId = accountId,
  companyId = companyId,
  executedById = executedById,
  status = status,
  createdAt = createdAt,
)

fun PaymentRequestWithAccountUiModel.toPaymentRequestWithAccountEntity() =
  PaymentRequestWithAccountEntity(
    payment = payment.toPaymentRequestEntity(),
    account = account.toAccountEntity(),
    fee = fee,
  )

fun PaymentUiModel.toPaymentEntity() = PaymentEntity(
  id = id,
  status = status.name,
  accountId = accountId,
  screenshotText = screenshotText,
  transactionId = transactionId,
  paymentMethodId = paymentMethodId,
  createdAt = createdAt,
  updatedAt = updatedAt,
  companyId = companyId,
  executedById = executedById,
)

fun CompanyLocationWithDemographicUiModel.toCompanyLocationWithDemographicEntity() =
  CompanyLocationWithDemographicEntity(
    location = location.toCompanyLocationEntity(),
    demographicArea = demographicArea.toDemographicAreaEntity(),
    demographicStreet = demographicStreet.toDemographicStreetEntity(),
  )

fun AccountRequestEntity.toAccountEntity() =
  AccountEntity(
    id = id,
    uuid = uuid,
    title = title.name,
    firstname = firstname,
    lastname = lastname,
    username = contact,
    email = email,
    companyLocationId = companyLocationId,
    companyId = companyId,
    updatedAt = createdAt,
    createdAt = createdAt,
  )

fun AccountEntity.toAccountContactEntity() =
  AccountContactEntity(
    uuid = uuid,
    email = email,
    contact = username,
    primary = true,
    accountId = id,
    createdAt = createdAt,
    updatedAt = updatedAt,
    id = id,
  )

fun AccountEntity.toAccountPaymentPlanEntity(planId: Long) =
  AccountPaymentPlanEntity(
    id = id,
    accountUuid = uuid,
    accountId = id,
    paymentPlanId = planId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun AccountPaymentPlanRequest.toAccountPaymentPlanRequestEntity() =
  AccountPaymentPlanRequestEntity(
    accountUuid = accountUuid,
    accountId = accountId,
    paymentPlanId = paymentPlanId,
  )
