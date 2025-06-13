package net.techandgraphics.wastemanagement.data.local.database.account

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface AccountDao : BaseDao<AccountEntity> {
  @Query("SELECT * FROM account")
  suspend fun query(): List<AccountEntity>

  @Query("SELECT COUNT(*) FROM account")
  suspend fun getSize(): Int

  @Query("SELECT * FROM account")
  fun flow(): Flow<List<AccountEntity>>

  @Query("SELECT * FROM account WHERE id IN (:id)")
  suspend fun get(id: Long): AccountEntity

  @Query("SELECT * FROM account WHERE id IN (:ids)")
  suspend fun gets(ids: List<Long>): List<AccountEntity>

  @Transaction
  @Query(
    """ SELECT * FROM account WHERE
               (firstname LIKE'%' || :query || '%'  OR
               username LIKE'%' || :query || '%'  OR
               title LIKE'%' || :query || '%'  OR
               lastname LIKE'%' || :query || '%')
      """,
  )
  fun query(query: String = ""): Flow<List<AccountEntity>>

//  @Transaction
//  @Query(
//    """
//      SELECT a.firstname,
//           a.lastname,
//           a.username,
//           a.title,
//           a.id as accountId,
//           ds.name AS streetName,
//           da.name AS areaName
//    FROM account a
//    JOIN demographic_street ds ON a.street_id = ds.id
//    JOIN demographic_area da ON ds.area_id = da.id
//    WHERE (a.firstname LIKE'%' || :query || '%'
//           OR a.username LIKE'%' || :query || '%'
//           OR a.title LIKE'%' || :query || '%'
//           OR ds.name LIKE'%' || :query || '%'
//           OR da.name LIKE'%' || :query || '%'
//           OR a.lastname LIKE'%' || :query || '%')
//      """,
//  )
//  fun qAccountWithStreetAndArea(query: String = ""): Flow<List<AccountWithStreetAndAreaEntity>>

  @Query(
    """
        SELECT *
        FROM account
        WHERE strftime('%Y-%m', datetime(created_at, 'unixepoch')) = :createAt
    """,
  )
  suspend fun getByCreatedAt(createAt: String): List<AccountEntity>
}

enum class TimeUnit(val format: String) {
  Minute("%Y-%m-%d %H:%M"),
  Hour("%Y-%m-%d %H"),
  Day("%Y-%m-%d"),
  Month("%Y-%m"),
  ;

  companion object {
    fun fromUnit(unit: String): TimeUnit? {
      return TimeUnit.entries.find { it.name.equals(unit, ignoreCase = true) }
    }
  }
}

fun getTimeFormatForUnit(unit: String): String? {
  val timeUnit = TimeUnit.fromUnit(unit)
  return timeUnit?.format
}
