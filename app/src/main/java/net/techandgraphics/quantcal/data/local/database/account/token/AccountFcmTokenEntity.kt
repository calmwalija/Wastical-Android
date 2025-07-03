package net.techandgraphics.quantcal.data.local.database.account.token

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = "account_fcm_token")
data class AccountFcmTokenEntity(
  @PrimaryKey val token: String,
  val sync: Boolean = true,
  @ColumnInfo("created_at") val createdAt: Long = ZonedDateTime.now().toEpochSecond(),
  @ColumnInfo("updated_at") val updatedAt: Long = ZonedDateTime.now().toEpochSecond(),
)
