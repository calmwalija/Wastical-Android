package net.techandgraphics.wastical.data.local.database.search.tag

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = "search_tag")
data class SearchTagEntity(
  @PrimaryKey val query: String,
  val tag: String,
  val timestamp: Long = ZonedDateTime.now().toEpochSecond(),
)
