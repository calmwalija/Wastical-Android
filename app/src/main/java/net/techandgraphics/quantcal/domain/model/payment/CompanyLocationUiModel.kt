package net.techandgraphics.quantcal.domain.model.payment

data class CompanyLocationUiModel(
  val id: Long,
  val status: String,
  val companyId: Long,
  val demographicStreetId: Long,
  val demographicAreaId: Long,
  val demographicDistrictId: Long,
  val createdAt: Long,
  val updatedAt: Long,
)
