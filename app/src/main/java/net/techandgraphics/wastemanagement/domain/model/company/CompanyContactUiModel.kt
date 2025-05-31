package net.techandgraphics.wastemanagement.domain.model.company

data class CompanyContactUiModel(
  val id: Long,
  val email: String?,
  val contact: String,
  val primary: Boolean,
  val companyId: Long,
  val createdAt: Long,
  val updatedAt: Long,
)
