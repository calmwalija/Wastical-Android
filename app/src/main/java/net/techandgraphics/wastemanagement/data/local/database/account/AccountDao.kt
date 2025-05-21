package net.techandgraphics.wastemanagement.data.local.database.account

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface AccountDao : BaseDao<AccountEntity> {
  @Query("SELECT * FROM account")
  suspend fun query(): List<AccountEntity>
}
