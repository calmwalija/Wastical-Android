package net.techandgraphics.wastemanagement.data.remote

import net.techandgraphics.wastemanagement.data.Status
import net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity
import net.techandgraphics.wastemanagement.data.local.database.account.contact.AccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.account.plan.AccountPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.account.plan.request.AccountPaymentPlanRequestEntity
import net.techandgraphics.wastemanagement.data.local.database.account.request.AccountRequestEntity
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
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.month.covered.PaymentMonthCoveredEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastemanagement.data.remote.account.AccountResponse
import net.techandgraphics.wastemanagement.data.remote.account.contact.AccountContactResponse
import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanResponse
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
import java.time.DayOfWeek

fun CompanyEntity.toCompanyResponse() =
  CompanyResponse(
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

fun CompanyContactEntity.toCompanyContactResponse() =
  CompanyContactResponse(
    id = id,
    email = email,
    contact = contact,
    primary = primary,
    companyId = companyId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun PaymentMethodEntity.toPaymentMethodResponse() =
  PaymentMethodResponse(
    id = id,
    account = account,
    paymentPlanId = paymentPlanId,
    paymentGatewayId = paymentGatewayId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun PaymentPlanResponse.PaymentPlanResponse() =
  PaymentPlanResponse(
    id = id,
    fee = fee,
    name = name,
    period = period,
    status = status,
    companyId = companyId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )


fun AccountEntity.toAccountResponse() =
  AccountResponse(
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

fun AccountContactEntity.toAccountContactResponse() =
  AccountContactResponse(
    id = id,
    uuid = uuid,
    email = email,
    contact = contact,
    primary = primary,
    accountId = accountId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun PaymentRequest.toPaymentRequest() = PaymentRequestEntity(
  status = PaymentStatus.Waiting.name,
  accountId = accountId,
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  companyId = companyId,
  executedById = executedById,
  months = months,
)

fun DemographicDistrictEntity.toDemographicDistrictResponse() =
  DemographicDistrictResponse(
    id = id,
    name = name,
    region = region,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun DemographicAreaEntity.toDemographicAreaResponse() =
  DemographicAreaResponse(
    id = id,
    name = name,
    type = type,
    latitude = latitude,
    longitude = longitude,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun DemographicStreetEntity.toDemographicStreetResponse() =
  DemographicStreetResponse(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun CompanyBinCollectionEntity.toCompanyBinCollectionResponse() =
  CompanyBinCollectionResponse(
    id = id,
    dayOfWeek = DayOfWeek.valueOf(dayOfWeek),
    companyId = companyId,
    companyLocationId = companyLocationId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun PaymentCollectionDayEntity.toPaymentCollectionDayResponse() =
  PaymentCollectionDayResponse(
    id = id,
    day = day,
    companyId = companyId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun AccountPaymentPlanEntity.toAccountPaymentPlanResponse() =
  AccountPaymentPlanResponse(
    id = id,
    accountUuid = accountUuid,
    accountId = accountId,
    paymentPlanId = paymentPlanId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun PaymentGatewayEntity.toPaymentGatewayResponse() =
  PaymentGatewayResponse(
    id = id,
    name = name,
    type = type,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun PaymentMethodEntity.toPaymentMethodUiModel() =
  PaymentMethodUiModel(
    id = id,
    account = account,
    paymentPlanId = paymentPlanId,
    paymentGatewayId = paymentGatewayId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isSelected = isSelected,
  )

fun PaymentMonthCoveredEntity.toPaymentMonthCoveredResponse() =
  PaymentMonthCoveredResponse(
    id = id,
    month = month,
    year = year,
    paymentId = paymentId,
    accountId = accountId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun CompanyLocationEntity.toCompanyLocationResponse() =
  CompanyLocationResponse(
    id = id,
    status = Status.valueOf(status),
    companyId = companyId,
    demographicStreetId = demographicStreetId,
    demographicAreaId = demographicAreaId,
    demographicDistrictId = demographicDistrictId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )


fun PaymentRequestEntity.toPaymentResponse() =
  PaymentResponse(
    id = id,
    screenshotText = screenshotText,
    transactionId = "",
    paymentMethodId = paymentMethodId,
    accountId = accountId,
    status = status,
    createdAt = createdAt,
    companyId = companyId,
    executedById = executedById,
    updatedAt = createdAt,
    months = months
  )


fun AccountRequestEntity.toAccountResponse() =
  AccountResponse(
    id = id,
    uuid = uuid,
    title = title.name,
    firstname = firstname,
    lastname = lastname,
    username = contact,
    email = email,
    latitude = -1f,
    longitude = -1f,
    status = Status.Active.name,
    companyId = companyId,
    leavingReason = null,
    leavingTimestamp = -1,
    companyLocationId = companyLocationId,
    createdAt = createdAt,
    updatedAt = createdAt
  )

fun AccountPaymentPlanRequestEntity.toAccountPaymentPlanResponse() =
  AccountPaymentPlanResponse(
    id = id,
    accountUuid = accountUuid,
    accountId = accountId,
    paymentPlanId = paymentPlanId,
    createdAt = createdAt,
    updatedAt = createdAt
  )
