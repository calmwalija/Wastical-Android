package net.techandgraphics.qgateway.data.local.database.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.techandgraphics.qgateway.data.remote.AccountResponse
import net.techandgraphics.qgateway.domain.model.AccountUiModel

@Entity(tableName = "account")
data class AccountEntity(
  @PrimaryKey val id: Long,
  val uuid: String,
  val title: String,
  val firstname: String,
  val lastname: String,
  val username: String,
  val email: String?,
  @ColumnInfo(name = "updated_at") val updatedAt: Long,
  @ColumnInfo(name = "created_at") val createdAt: Long,
) {

  fun toAccountResponse() = AccountResponse(
    id = id,
    uuid = uuid,
    title = title,
    firstname = firstname,
    lastname = lastname,
    username = username,
    email = email,
    updatedAt = updatedAt,
    createdAt = createdAt,
  )

  fun toAccountUiModel() = AccountUiModel(
    id = id,
    uuid = uuid,
    title = title,
    firstname = firstname,
    lastname = lastname,
    username = username,
    email = email,
    updatedAt = updatedAt,
    createdAt = createdAt,
  )
}
