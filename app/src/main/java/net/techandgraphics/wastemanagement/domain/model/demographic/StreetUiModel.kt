package net.techandgraphics.wastemanagement.domain.model.demographic

data class StreetUiModel(
  val id: Long,
  val name: String,
  val latitude: Float,
  val longitude: Float,
  val areaId: Long,
  val createdAt: Long,
  val updatedAt: Long,
)
