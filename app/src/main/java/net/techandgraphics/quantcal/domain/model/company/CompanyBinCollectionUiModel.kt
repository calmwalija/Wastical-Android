package net.techandgraphics.quantcal.domain.model.company

data class CompanyBinCollectionUiModel(
  val id: Long,
  val dayOfWeek: String,
  val companyId: Long,
  val streetId: Long,
  val createdAt: Long,
  val updatedAt: Long,
)
