package net.techandgraphics.wcompanion.data.local.database.otp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.techandgraphics.wcompanion.domain.model.OtpUiModel
import java.time.ZonedDateTime

@Entity(tableName = "account_opt")
data class OtpEntity(
  @PrimaryKey val id: Long,
  val otp: Int,
  val sent: Boolean = false,
  val contact: String,
  @ColumnInfo("account_id") val accountId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
  val sentAt: Long = ZonedDateTime.now().toEpochSecond(),
) {
  fun toOtpUiModel() = OtpUiModel(
    id = id,
    otp = otp,
    sent = sent,
    contact = contact,
    accountId = accountId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    sentAt = sentAt,
  )
}
