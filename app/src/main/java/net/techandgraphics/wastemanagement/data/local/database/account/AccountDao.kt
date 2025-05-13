package net.techandgraphics.wastemanagement.data.local.database.account

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface AccountDao : BaseDao<AccountEntity> {
  @Query("SELECT * FROM account")
  fun query(): Flow<List<AccountEntity>>
}
