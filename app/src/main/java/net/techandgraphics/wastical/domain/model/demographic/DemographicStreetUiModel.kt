package net.techandgraphics.wastical.domain.model.demographic

data class DemographicStreetUiModel(
  val id: Long,
  val name: String,
  val latitude: Float,
  val longitude: Float,
  val createdAt: Long,
  val updatedAt: Long,
  val belongTo: Boolean = false,
)
