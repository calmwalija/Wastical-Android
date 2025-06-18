package net.techandgraphics.wastemanagement.data.local.database.account.contact

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface AccountContactDao : BaseDao<AccountContactEntity> {
  @Query("SELECT * FROM account_contact")
  suspend fun query(): List<AccountContactEntity>

  @Query("SELECT * FROM account_contact")
  fun flow(): Flow<List<AccountContactEntity>>

  @Query("SELECT * FROM account_contact WHERE account_id=:id")
  suspend fun getByAccountId(id: Long): List<AccountContactEntity>
}
