package net.techandgraphics.quantcal.data.local.database.query

import net.techandgraphics.quantcal.data.PaymentPeriod
import net.techandgraphics.quantcal.data.Status

data class PaymentWithAccountAndMethodWithGatewayQuery(
// Payment fields
  val paymentId: Long,
  val screenshotText: String,
  val transactionId: String,
  val paymentMethodId: Long,
  val accountId: Long,
  val paymentStatus: String,
  val paymentCreatedAt: Long,
  val paymentUpdatedAt: Long,
  val paymentCompanyId: Long,
  val executedById: Long,

// Account fields
  val accId: Long,
  val uuid: String,
  val role: String,
  val title: String,
  val firstname: String,
  val lastname: String,
  val username: String,
  val email: String?,
  val latitude: Float,
  val longitude: Float,
  val accStatus: String,
  val companyLocationId: Long,
  val accCompanyId: Long,
  val leavingReason: String?,
  val leavingTimestamp: Long?,
  val accUpdatedAt: Long,
  val accCreatedAt: Long,

// Method fields
  val methodId: Long,
  val methodAccount: String,
  val isSelected: Boolean,
  val paymentPlanId: Long,
  val paymentGatewayId: Long,
  val methodCreatedAt: Long,
  val methodUpdatedAt: Long,

// Gateway fields
  val gatewayId: Long,
  val gatewayName: String,
  val gatewayType: String,
  val gatewayCreatedAt: Long,
  val gatewayUpdatedAt: Long,

// Plan fields
  val planId: Long,
  val planFee: Int,
  val planName: String,
  val planPeriod: PaymentPeriod,
  val planStatus: Status,
  val planCompanyId: Long,
  val planCreatedAt: Long,
  val planUpdatedAt: Long,

  val coveredSize: Int,
)
