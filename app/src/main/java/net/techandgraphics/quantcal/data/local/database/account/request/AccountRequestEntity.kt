package net.techandgraphics.quantcal.data.local.database.account.request

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.quantcal.data.local.database.account.AccountEntity
import net.techandgraphics.quantcal.data.local.database.account.AccountTitle
import net.techandgraphics.quantcal.data.local.database.company.CompanyEntity
import net.techandgraphics.quantcal.data.local.database.company.location.CompanyLocationEntity
import net.techandgraphics.quantcal.data.local.database.payment.plan.PaymentPlanEntity
import net.techandgraphics.quantcal.data.remote.account.HttpOperation
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
  val role: String,
  val status: String,
  @ColumnInfo("http_operation") val httpOperation: String = HttpOperation.Post.name,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("account_id") val accountId: Long,
  @ColumnInfo("company_location_id") val companyLocationId: Long,
  @ColumnInfo("payment_plan_id") val paymentPlanId: Long,
  @ColumnInfo(name = "leaving_reason") val leavingReason: String? = null,
  @ColumnInfo(name = "leaving_timestamp") val leavingTimestamp: Long? = null,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
  @PrimaryKey(autoGenerate = false) val id: Long = 0,
)
