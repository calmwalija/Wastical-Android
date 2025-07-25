package net.techandgraphics.wastical.data.local.database.account.contact

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastical.data.local.database.account.AccountEntity

@Entity(
  tableName = "account_contact",
  foreignKeys = [
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = ["id"],
      childColumns = ["account_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [Index("account_id")],
)
data class AccountContactEntity(
  @PrimaryKey val id: Long,
  val uuid: String,
  val email: String?,
  val contact: String,
  val primary: Boolean,
  @ColumnInfo("account_id") val accountId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
