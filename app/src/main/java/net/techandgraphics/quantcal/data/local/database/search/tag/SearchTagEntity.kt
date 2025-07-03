package net.techandgraphics.quantcal.data.local.database.search.tag

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = "search_tag")
data class SearchTagEntity(
  @ColumnInfo(index = true) val query: String,
  val tag: String,
  val timestamp: Long = ZonedDateTime.now().toEpochSecond(),
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
