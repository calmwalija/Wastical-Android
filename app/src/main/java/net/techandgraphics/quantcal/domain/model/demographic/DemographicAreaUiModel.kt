package net.techandgraphics.quantcal.domain.model.demographic

data class DemographicAreaUiModel(
  val id: Long,
  val name: String,
  val type: String,
  val description: String,
  val latitude: Float,
  val longitude: Float,
  val createdAt: Long,
  val updatedAt: Long,
)
