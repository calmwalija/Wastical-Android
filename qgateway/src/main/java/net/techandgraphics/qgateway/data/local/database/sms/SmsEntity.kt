package net.techandgraphics.qgateway.data.local.database.sms

import androidx.room.Entity
import androidx.room.PrimaryKey
import net.techandgraphics.qgateway.domain.model.SmsUiModel
import java.time.ZonedDateTime

@Entity(tableName = "sms")
data class SmsEntity(
  val contact: String,
  val message: String,
  val hashable: String,
  val uuid: String,
  val handshake: Boolean = false,
  val timestamp: Long = ZonedDateTime.now().toEpochSecond(),
  @PrimaryKey(autoGenerate = true) val id: Long = 0L,
) {
  fun toSmsUiModel() = SmsUiModel(
    contact = contact,
    message = message,
    hashable = hashable,
    uuid = uuid,
    handshake = handshake,
    timestamp = timestamp,
    id = id,
  )
}
