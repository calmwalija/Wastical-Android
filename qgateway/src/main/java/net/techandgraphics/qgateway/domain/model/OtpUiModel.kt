package net.techandgraphics.qgateway.domain.model

import net.techandgraphics.qgateway.data.local.database.otp.OtpEntity

data class OtpUiModel(
  val id: Long,
  val otp: Int,
  val sent: Boolean = false,
  val contact: String,
  val accountId: Long,
  val createdAt: Long,
  val updatedAt: Long,
  val sentAt: Long,
) {
  fun toOtpEntity() = OtpEntity(
    id = id,
    otp = otp,
    sent = sent,
    sentAt = sentAt,
    contact = contact,
    accountId = accountId,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )
}
