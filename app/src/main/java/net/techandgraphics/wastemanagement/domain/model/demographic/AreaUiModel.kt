package net.techandgraphics.wastemanagement.domain.model.demographic

data class AreaUiModel(
  val id: Long,
  val name: String,
  val type: String,
  val description: String,
  val latitude: Float,
  val longitude: Float,
  val districtId: Long,
  val createdAt: Long,
  val updatedAt: Long,
)
