package net.techandgraphics.wastical.data.local.database

import net.techandgraphics.wastical.data.local.database.account.AccountEntity
import net.techandgraphics.wastical.data.local.database.account.AccountTitle
import net.techandgraphics.wastical.data.local.database.account.contact.AccountContactEntity
import net.techandgraphics.wastical.data.local.database.account.otp.AccountOtpEntity
import net.techandgraphics.wastical.data.local.database.account.plan.AccountPaymentPlanEntity
import net.techandgraphics.wastical.data.local.database.account.plan.request.AccountPaymentPlanRequestEntity
import net.techandgraphics.wastical.data.local.database.account.request.AccountRequestEntity
import net.techandgraphics.wastical.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.wastical.data.local.database.company.CompanyEntity
import net.techandgraphics.wastical.data.local.database.company.bin.collection.CompanyBinCollectionEntity
import net.techandgraphics.wastical.data.local.database.company.contact.CompanyContactEntity
import net.techandgraphics.wastical.data.local.database.company.location.CompanyLocationEntity
import net.techandgraphics.wastical.data.local.database.demographic.area.DemographicAreaEntity
import net.techandgraphics.wastical.data.local.database.demographic.district.DemographicDistrictEntity
import net.techandgraphics.wastical.data.local.database.demographic.street.DemographicStreetEntity
import net.techandgraphics.wastical.data.local.database.notification.NotificationEntity
import net.techandgraphics.wastical.data.local.database.payment.collection.PaymentCollectionDayEntity
import net.techandgraphics.wastical.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.wastical.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastical.data.local.database.payment.pay.month.covered.PaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastical.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.wastical.data.local.database.relations.CompanyLocationWithDemographicEntity
import net.techandgraphics.wastical.data.local.database.relations.PaymentRequestWithAccountEntity
import net.techandgraphics.wastical.data.local.database.search.tag.SearchTagEntity
import net.techandgraphics.wastical.data.remote.account.AccountResponse
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.account.contact.AccountContactResponse
import net.techandgraphics.wastical.data.remote.account.otp.AccountOtpResponse
import net.techandgraphics.wastical.data.remote.account.plan.AccountPaymentPlanRequest
import net.techandgraphics.wastical.data.remote.account.plan.AccountPaymentPlanResponse
import net.techandgraphics.wastical.data.remote.account.token.AccountFcmTokenResponse
import net.techandgraphics.wastical.data.remote.company.CompanyContactResponse
import net.techandgraphics.wastical.data.remote.company.CompanyResponse
import net.techandgraphics.wastical.data.remote.company.bin.collection.CompanyBinCollectionResponse
import net.techandgraphics.wastical.data.remote.company.location.CompanyLocationResponse
import net.techandgraphics.wastical.data.remote.demographic.DemographicAreaResponse
import net.techandgraphics.wastical.data.remote.demographic.DemographicDistrictResponse
import net.techandgraphics.wastical.data.remote.demographic.DemographicStreetResponse
import net.techandgraphics.wastical.data.remote.notification.NotificationResponse
import net.techandgraphics.wastical.data.remote.payment.PaymentRequest
import net.techandgraphics.wastical.data.remote.payment.collection.PaymentCollectionDayResponse
import net.techandgraphics.wastical.data.remote.payment.gateway.PaymentGatewayResponse
import net.techandgraphics.wastical.data.remote.payment.method.PaymentMethodResponse
import net.techandgraphics.wastical.data.remote.payment.pay.PaymentResponse
import net.techandgraphics.wastical.data.remote.payment.pay.month.covered.PaymentMonthCoveredResponse
import net.techandgraphics.wastical.data.remote.payment.plan.PaymentPlanResponse
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicDistrictUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.wastical.domain.model.payment.CompanyLocationUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentRequestUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentRequestWithAccountUiModel
import net.techandgraphics.wastical.domain.model.search.SearchTagUiModel
import java.time.ZonedDateTime

fun CompanyResponse.toCompanyEntity() = CompanyEntity(
  id = id,
  uuid = uuid,
  name = name,
  latitude = latitude,
  email = email,
  longitude = longitude,
  status = status,
  address = address,
  slogan = slogan,
  billingDate = billingDate,
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
  proofExt = proofExt,
  companyId = companyId,
  executedById = executedById,
  paymentReference = paymentReference,
)

fun AccountResponse.toAccountEntity() = AccountEntity(
  id = id,
  uuid = uuid,
  title = title,
  firstname = firstname,
  lastname = lastname,
  username = username,
  email = email,
  role = role,
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
  status = status.name,
  accountId = accountId,
  screenshotText = screenshotText,
  paymentMethodId = paymentMethodId,
  companyId = companyId,
  executedById = executedById,
  months = months,
  httpOperation = httpOperation,
  createdAt = createdAt,
  updatedAt = updateAt,
  paymentReference = paymentReference,
  id = System.currentTimeMillis().plus(ZonedDateTime.now().toEpochSecond()),
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
  uuid = uuid,
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
)

fun CompanyUiModel.toCompanyEntity() = CompanyEntity(
  id = id,
  uuid = uuid,
  name = name,
  latitude = latitude,
  email = email,
  longitude = longitude,
  status = status,
  address = address,
  billingDate = billingDate,
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
  firstname = firstname.trim(),
  lastname = lastname.trim(),
  username = username,
  role = role,
  email = email?.trim(),
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
  uuid = uuid,
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
  paymentReference = paymentReference,
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
  paymentReference = paymentReference,
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
    role = role,
    companyLocationId = companyLocationId,
    companyId = companyId,
    updatedAt = updatedAt,
    createdAt = createdAt,
  )

fun AccountEntity.toAccountEntity(planId: Long) =
  AccountRequestEntity(
    id = id,
    uuid = uuid,
    title = AccountTitle.valueOf(title),
    firstname = firstname,
    lastname = lastname,
    email = email,
    companyLocationId = companyLocationId,
    companyId = companyId,
    updatedAt = updatedAt,
    createdAt = createdAt,
    contact = username,
    altContact = "",
    role = role,
    status = status,
    accountId = id,
    paymentPlanId = planId,
    leavingReason = leavingReason,
    leavingTimestamp = leavingTimestamp,
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

fun AccountPaymentPlanRequest.toAccountPaymentPlanRequestEntity(id: Long) =
  AccountPaymentPlanRequestEntity(
    accountUuid = accountUuid,
    accountId = accountId,
    paymentPlanId = paymentPlanId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    id = id,
  )

fun PaymentEntity.toPaymentRequestEntity(httpOperation: HttpOperation) =
  PaymentRequestEntity(
    id = id,
    status = status,
    accountId = accountId,
    screenshotText = screenshotText,
    paymentMethodId = paymentMethodId,
    companyId = companyId,
    executedById = executedById,
    months = -1,
    httpOperation = httpOperation.name,
    createdAt = createdAt,
    paymentReference = paymentReference,
  )

fun AccountOtpResponse.toAccountOtpEntity(contact: String) =
  AccountOtpEntity(
    id = id,
    otp = otp,
    contact = contact,
    accountId = accountId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )

fun NotificationResponse.toNotificationEntity() =
  NotificationEntity(
    id = id,
    topic = topic,
    reference = reference,
    uuid = uuid,
    isRead = isRead,
    recipientId = recipientId,
    recipientRole = recipientRole,
    senderId = senderId,
    companyId = companyId,
    paymentId = paymentId,
    type = type,
    title = title,
    body = body,
    metadata = metadata,
    deliveredAt = deliveredAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )
