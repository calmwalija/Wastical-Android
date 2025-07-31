package net.techandgraphics.wastical.data.local.database.account.contact

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
interface AccountContactDao : BaseDao<AccountContactEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM account_contact ORDER BY updated_at DESC LIMIT 1")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM account_contact")
  suspend fun query(): List<AccountContactEntity>

  @Query("SELECT * FROM account_contact")
  fun flow(): Flow<List<AccountContactEntity>>

  @Query("SELECT * FROM account_contact WHERE account_id=:id")
  suspend fun getByAccountId(id: Long): List<AccountContactEntity>

  @Query("SELECT * FROM account_contact WHERE contact=:contact")
  suspend fun getByContact(contact: String): List<AccountContactEntity>
}
