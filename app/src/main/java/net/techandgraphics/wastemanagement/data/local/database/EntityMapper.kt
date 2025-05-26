package net.techandgraphics.wastemanagement.data.local.database

import net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity
import net.techandgraphics.wastemanagement.data.local.database.account.contact.AccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.account.plan.AccountPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.company.contact.CompanyContactEntity
import net.techandgraphics.wastemanagement.data.local.database.company.trash.collection.schedule.TrashCollectionScheduleEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.area.AreaEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.district.DistrictEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.StreetEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.collection.PaymentCollectionDayEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.wastemanagement.data.remote.account.AccountResponse
import net.techandgraphics.wastemanagement.data.remote.account.contact.AccountContactResponse
import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanResponse
import net.techandgraphics.wastemanagement.data.remote.company.CompanyContactResponse
import net.techandgraphics.wastemanagement.data.remote.company.CompanyResponse
import net.techandgraphics.wastemanagement.data.remote.company.trash.collection.schedule.TrashCollectionScheduleResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.AreaResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.DistrictResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.StreetResponse
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.data.remote.payment.collection.PaymentCollectionDayResponse
import net.techandgraphics.wastemanagement.data.remote.payment.gateway.PaymentGatewayResponse
import net.techandgraphics.wastemanagement.data.remote.payment.method.PaymentMethodResponse
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentResponse
import net.techandgraphics.wastemanagement.data.remote.payment.plan.PaymentPlanResponse
import java.time.ZonedDateTime
import java.util.UUID

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

fun List<PaymentMethodResponse>.toPaymentMethodEntity(gateways: List<PaymentGatewayResponse>): MutableList<PaymentMethodEntity> {
  val paymentMethods: MutableList<PaymentMethodEntity> = mutableListOf()
  map { method ->
    gateways.forEach { gateway ->
      if (method.paymentGatewayId == gateway.id) {
        paymentMethods.add(method.toPaymentMethodEntity(gateway))
      }
    }
  }
  return paymentMethods
}

fun PaymentMethodResponse.toPaymentMethodEntity(gateway: PaymentGatewayResponse) =
  PaymentMethodEntity(
    id = id,
    name = gateway.name,
    type = gateway.type,
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
  numberOfMonths = numberOfMonths,
  transactionId = transactionId,
  paymentMethodId = paymentMethodId,
  createdAt = createdAt,
  updatedAt = updatedAt,
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
  trashCollectionScheduleId = trashCollectionScheduleId,
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

fun PaymentRequest.toPaymentCacheEntity() = PaymentEntity(
  status = PaymentStatus.Failed.name,
  accountId = accountId,
  screenshotText = screenshotText,
  numberOfMonths = numberOfMonths,
  paymentMethodId = paymentMethodId,
  transactionId = UUID.randomUUID().toString(),
  createdAt = ZonedDateTime.now().toEpochSecond(),
  id = System.currentTimeMillis().times(1_000),
  updatedAt = null,
)

fun DistrictResponse.toDistrictEntity() = DistrictEntity(
  id = id,
  name = name,
  region = region,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun AreaResponse.toAreaEntity() = AreaEntity(
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

fun StreetResponse.toStreetEntity() = StreetEntity(
  id = id,
  name = name,
  latitude = latitude,
  longitude = longitude,
  areaId = areaId,
  createdAt = createdAt,
  updatedAt = updatedAt,
)

fun TrashCollectionScheduleResponse.toTrashCollectionScheduleEntity() =
  TrashCollectionScheduleEntity(
    id = id,
    dayOfWeek = dayOfWeek.name,
    companyId = companyId,
    streetId = streetId,
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
