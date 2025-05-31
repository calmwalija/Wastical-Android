package net.techandgraphics.wastemanagement.domain.model.company

data class TrashCollectionScheduleUiModel(
  val id: Long,
  val dayOfWeek: String,
  val companyId: Long,
  val streetId: Long,
  val createdAt: Long,
  val updatedAt: Long,
)
