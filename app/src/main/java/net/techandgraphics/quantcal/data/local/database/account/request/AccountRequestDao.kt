package net.techandgraphics.quantcal.data.local.database.account.request

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.quantcal.data.local.database.BaseDao

@Dao interface AccountRequestDao : BaseDao<AccountRequestEntity> {

  @Query("SELECT * FROM account_request")
  suspend fun query(): List<AccountRequestEntity>

  @Query("SELECT * FROM account_request")
  fun flowOf(): Flow<List<AccountRequestEntity>>
}
