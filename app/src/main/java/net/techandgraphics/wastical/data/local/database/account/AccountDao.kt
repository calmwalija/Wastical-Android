package net.techandgraphics.wastical.data.local.database.account

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.Status
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.domain.model.account.AccountInfoUiModel

@Dao
interface AccountDao : BaseDao<AccountEntity> {

  @Query("SELECT updated_at FROM account WHERE id=:id")
  suspend fun getLastUpdatedTimestampById(id: Long): Long

  @Query("SELECT * FROM account")
  suspend fun query(): List<AccountEntity>

  @Query("SELECT * FROM account  WHERE role=:role AND status=:status")
  suspend fun qByRole(
    status: String = Status.Active.name,
    role: String = AccountRole.Client.name,
  ): List<AccountEntity>

  @Query("SELECT * FROM account WHERE company_location_id=:id")
  suspend fun qByCompanyLocationId(id: Long): List<AccountEntity>

  @Query("SELECT COUNT(*) FROM account")
  suspend fun getSize(): Int

  @Query("SELECT * FROM account WHERE id=:id")
  fun flowById(id: Long): Flow<AccountEntity?>

  @Query("SELECT * FROM account")
  fun flow(): Flow<List<AccountEntity>>

  @Query("SELECT * FROM account WHERE id IN (:id)")
  suspend fun get(id: Long): AccountEntity

  @Query("SELECT * FROM account WHERE username=:contact")
  suspend fun getByContact(contact: String): List<AccountEntity>

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

  @Query(
    """
    SELECT a.firstname,
           a.lastname,
           a.username,
           a.title,
           a.id as accountId,
           ds.name AS streetName,
           da.name AS areaName
    FROM account a
    JOIN company_location cl ON a.company_location_id = cl.id
    JOIN demographic_street ds ON cl.demographic_street_id = ds.id
    JOIN demographic_area da ON cl.demographic_area_id = da.id
    WHERE (a.firstname LIKE '%' || :query || '%'
        OR a.username LIKE '%' || :query || '%'
        OR a.title LIKE '%' || :query || '%'
        OR ds.name LIKE '%' || :query || '%'
        OR da.name LIKE '%' || :query || '%'
        OR a.lastname LIKE '%' || :query || '%')
        AND a.status = 'Active'
    """,
  )
  fun qAccountInfo(query: String = ""): Flow<List<AccountInfoUiModel>>

  @Query(
    """
    SELECT a.firstname,
           a.lastname,
           a.username,
           a.title,
           a.id as accountId,
           ds.name AS streetName,
           da.name AS areaName
    FROM account a
    JOIN company_location cl ON a.company_location_id = cl.id
    JOIN demographic_street ds ON cl.demographic_street_id = ds.id
    JOIN demographic_area da ON cl.demographic_area_id = da.id
    WHERE (a.firstname LIKE '%' || :query || '%'
        OR a.username LIKE '%' || :query || '%'
        OR a.title LIKE '%' || :query || '%'
        OR ds.name LIKE '%' || :query || '%'
        OR da.name LIKE '%' || :query || '%'
        OR a.lastname LIKE '%' || :query || '%')
      AND da.id IN (:ids) AND a.status = 'Active'
    """,
  )
  fun qAccountInfoFiltered(query: String = "", ids: Set<Long>): Flow<List<AccountInfoUiModel>>

  fun qAccountData(query: String = "", ids: Set<Long>? = null) =
    if (ids == null) qAccountInfo(query) else qAccountInfoFiltered(query, ids)

  @Query(
    """
        SELECT *
        FROM account
        WHERE strftime('%Y-%m', datetime(created_at, 'unixepoch')) = :createAt
    """,
  )
  suspend fun getByCreatedAt(createAt: String): List<AccountEntity>

  @Query(
    """
    SELECT a.firstname,
           a.lastname,
           a.username,
           a.title,
           a.id as accountId,
           ds.name AS streetName,
           da.name AS areaName
    FROM account a
    JOIN company_location cl ON a.company_location_id = cl.id
    JOIN demographic_street ds ON cl.demographic_street_id = ds.id
    JOIN demographic_area da ON cl.demographic_area_id = da.id
    WHERE a.username = :uname
        AND a.status = 'Active'
    """,
  )
  suspend fun qByUname(uname: String = ""): List<AccountInfoUiModel>
}

data class ReportAccountItem(
  val firstname: String,
  val lastname: String,
  val title: String,
  val leavingReason: String? = null,
  val createdAt: Long,
  val updatedAt: Long,
  val fee: Int,
  val username: String,
  val demographicStreet: String,
  val demographicArea: String,
)
