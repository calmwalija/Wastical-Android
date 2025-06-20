package net.techandgraphics.wastemanagement.data.local.database.account.request

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao interface AccountRequestDao : BaseDao<AccountRequestEntity> {

  @Query("SELECT * FROM account_request")
  suspend fun query(): List<AccountRequestEntity>
}
