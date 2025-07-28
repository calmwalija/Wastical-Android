package net.techandgraphics.wastical.data.local.database.search.tag

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao

@Dao
abstract class SearchTagDao : BaseDao<SearchTagEntity> {
  @Query("SELECT * FROM search_tag ORDER BY timestamp DESC LIMIT 16")
  abstract fun query(): Flow<List<SearchTagEntity>>
}
