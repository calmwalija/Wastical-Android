package net.techandgraphics.wastical.data.local.database.account.otp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_opt")
data class AccountOtpEntity(
  @PrimaryKey val id: Long,
  val otp: Int,
  val contact: String,
  val attempt: Int = 0,
  @ColumnInfo("account_id") val accountId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
