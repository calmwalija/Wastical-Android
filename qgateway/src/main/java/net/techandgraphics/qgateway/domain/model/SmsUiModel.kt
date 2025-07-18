package net.techandgraphics.qgateway.domain.model

import net.techandgraphics.qgateway.data.local.database.sms.SmsEntity

data class SmsUiModel(
  val id: Long,
  val uuid: String,
  val contact: String,
  val message: String,
  val hashable: String,
  val handshake: Boolean,
  val timestamp: Long,
) {
  fun toSmsEntity() = SmsEntity(
    contact = contact,
    message = message,
    hashable = hashable,
    uuid = uuid,
    handshake = handshake,
    timestamp = timestamp,
    id = id,
  )
}
