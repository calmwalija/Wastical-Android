package net.techandgraphics.qgateway.data.local.database.token

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.techandgraphics.qgateway.data.remote.FcmTokenRequest
import java.time.ZonedDateTime

@Entity(tableName = "fcm_token")
data class FcmTokenEntity(
  @PrimaryKey val token: String,
  val sync: Boolean = true,
  @ColumnInfo("created_at") val createdAt: Long = ZonedDateTime.now().toEpochSecond(),
  @ColumnInfo("updated_at") val updatedAt: Long = ZonedDateTime.now().toEpochSecond(),
) {

  fun fcmTokenRequest(accountId: Long) =
    FcmTokenRequest(
      token = token,
      accountId = accountId,
    )
}
