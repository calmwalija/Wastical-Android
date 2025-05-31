package net.techandgraphics.wastemanagement.domain.model.company

import net.techandgraphics.wastemanagement.data.Status

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
