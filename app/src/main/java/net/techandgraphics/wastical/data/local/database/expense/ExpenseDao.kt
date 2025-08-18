package net.techandgraphics.wastical.data.local.database.expense

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao

@Dao
interface ExpenseDao : BaseDao<ExpenseEntity> {

  @Query("SELECT * FROM expense WHERE id = :id")
  suspend fun get(id: Long): ExpenseEntity

  @Query("SELECT * FROM expense WHERE expense_date BETWEEN :start AND :end ORDER BY expense_date DESC")
  fun flowOfRange(start: Long, end: Long): Flow<List<ExpenseEntity>>

  @Query("SELECT COALESCE(SUM(amount), 0) FROM expense WHERE expense_date BETWEEN :start AND :end")
  fun sumInRange(start: Long, end: Long): Flow<Long>

  @Query("SELECT COALESCE(SUM(amount), 0) FROM expense WHERE category = :category AND expense_date BETWEEN :start AND :end")
  fun sumByCategoryInRange(category: String, start: Long, end: Long): Flow<Long>

  @Query("SELECT * FROM expense ORDER BY expense_date DESC")
  fun flowAll(): Flow<List<ExpenseEntity>>
}
