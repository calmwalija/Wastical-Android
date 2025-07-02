package net.techandgraphics.wastemanagement.data.local.database.account.request

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastemanagement.data.local.database.AccountRole
import net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.company.location.CompanyLocationEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.wastemanagement.data.remote.account.HttpOperation
import java.time.ZonedDateTime
import java.util.UUID

@Entity(
  tableName = "account_request",
  foreignKeys = [
    ForeignKey(
      entity = CompanyEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = PaymentPlanEntity::class,
      parentColumns = ["id"],
      childColumns = ["payment_plan_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = CompanyLocationEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_location_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = ["id"],
      childColumns = ["account_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [
    Index("account_id"),
    Index("company_id"),
    Index("company_location_id"),
    Index("payment_plan_id"),
  ],
)
data class AccountRequestEntity(
  val uuid: String = UUID.randomUUID().toString(),
  val title: AccountTitle,
  val firstname: String,
  val lastname: String,
  val contact: String,
  val altContact: String,
  val email: String? = null,
  val role: String = AccountRole.Client.name,
  @ColumnInfo("http_operation") val httpOperation: String = HttpOperation.Create.name,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("account_id") val accountId: Long,
  @ColumnInfo("company_location_id") val companyLocationId: Long,
  @ColumnInfo("payment_plan_id") val paymentPlanId: Long,
  @ColumnInfo("created_at") val createdAt: Long = ZonedDateTime.now().toEpochSecond(),
  @PrimaryKey(autoGenerate = false) val id: Long = 0,
)
