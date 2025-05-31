package net.techandgraphics.wastemanagement.data.local.database.account

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface AccountDao : BaseDao<AccountEntity> {
  @Query("SELECT * FROM account")
  suspend fun query(): List<AccountEntity>

  @Query("SELECT * FROM account")
  fun flow(): Flow<List<AccountEntity>>

  @Query("SELECT * FROM account WHERE id IN (:id)")
  suspend fun get(id: Long): AccountEntity

  @Query("SELECT * FROM account WHERE id IN (:ids)")
  suspend fun gets(ids: List<Long>): List<AccountEntity>
}
