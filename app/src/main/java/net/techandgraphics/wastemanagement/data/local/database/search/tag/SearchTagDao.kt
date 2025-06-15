package net.techandgraphics.wastemanagement.data.local.database.search.tag

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
abstract class SearchTagDao : BaseDao<SearchTagEntity> {
  @Query("SELECT * FROM search_tag ORDER BY id DESC LIMIT 32")
  abstract fun query(): Flow<List<SearchTagEntity>>
}
