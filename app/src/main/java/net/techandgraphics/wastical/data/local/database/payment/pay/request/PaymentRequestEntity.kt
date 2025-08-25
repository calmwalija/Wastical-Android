package net.techandgraphics.wastical.data.local.database.payment.pay.request

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastical.data.local.database.account.AccountEntity
import net.techandgraphics.wastical.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import java.time.ZonedDateTime

@Entity(
  tableName = "payment_request",
  foreignKeys = [
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = ["id"],
      childColumns = ["account_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = ["id"],
      childColumns = ["executed_by_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = PaymentMethodEntity::class,
      parentColumns = ["id"],
      childColumns = ["payment_method_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [
    Index("account_id"),
    Index("payment_method_id"),
    Index("executed_by_id"),
  ],
)
data class PaymentRequestEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val months: Int,
  @ColumnInfo("screenshot_text") val screenshotText: String,
  @ColumnInfo("payment_method_id") val paymentMethodId: Long,
  @ColumnInfo("account_id") val accountId: Long,
  @ColumnInfo("proof_ext") val proofExt: String? = null,
  @ColumnInfo("payment_reference") val paymentReference: String,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("executed_by_id") val executedById: Long,
  @ColumnInfo("payment_status") val status: String,
  @ColumnInfo("http_operation") val httpOperation: String = HttpOperation.Post.name,
  @ColumnInfo("created_at") val createdAt: Long = ZonedDateTime.now().toEpochSecond(),
  @ColumnInfo("updated_at") val updatedAt: Long = ZonedDateTime.now().toEpochSecond(),
)
