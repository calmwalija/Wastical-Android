package net.techandgraphics.wastical.data.local.database.expense

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense")
data class ExpenseEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val amount: Long,
  val category: String,
  val period: String,
  @ColumnInfo(name = "interval_days") val intervalDays: Int? = null,
  @ColumnInfo(name = "quantity") val quantity: Int? = null,
  @ColumnInfo(name = "expense_date") val expenseDate: Long,
  @ColumnInfo(name = "company_id") val companyId: Long,
  @ColumnInfo(name = "created_at") val createdAt: Long,
  @ColumnInfo(name = "updated_at") val updatedAt: Long,
)

enum class ExpenseCategory { Fuel, CarHire, Salary, Utilities, Maintenance, Emergency, Other }
enum class ExpensePeriod { DAILY, WEEKLY, MONTHLY, CUSTOM_DAYS, ADHOC }
