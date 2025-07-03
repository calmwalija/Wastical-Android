package net.techandgraphics.quantcal.domain.model.company

import net.techandgraphics.quantcal.data.Status

data class CompanyUiModel(
  val id: Long,
  val name: String,
  val email: String,
  val address: String,
  val slogan: String,
  val latitude: Float = -1f,
  val longitude: Float = -1f,
  val status: Status = Status.Active,
  val updatedAt: Long,
  val createdAt: Long,
)
