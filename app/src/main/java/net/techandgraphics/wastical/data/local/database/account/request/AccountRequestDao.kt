package net.techandgraphics.wastical.data.local.database.account.request

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao

@Dao interface AccountRequestDao : BaseDao<AccountRequestEntity> {

  @Query("SELECT * FROM account_request")
  suspend fun query(): List<AccountRequestEntity>

  @Query("SELECT * FROM account_request")
  fun flowOf(): Flow<List<AccountRequestEntity>>

  @Query("SELECT * FROM account_request WHERE http_operation=:op")
  suspend fun qByHttpOp(op: String): List<AccountRequestEntity>
}
