package net.techandgraphics.wastemanagement.domain.model.search

import java.time.ZonedDateTime

data class SearchTagUiModel(
  val query: String,
  val tag: String,
  val timestamp: Long = ZonedDateTime.now().toEpochSecond(),
  val id: Int = 0,
)
