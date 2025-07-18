package net.techandgraphics.qgateway.data.local.database.otp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_opt")
data class OtpEntity(
  @PrimaryKey val id: Long,
  val otp: Int,
  val sent: Boolean = false,
  val contact: String,
  @ColumnInfo("account_id") val accountId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
