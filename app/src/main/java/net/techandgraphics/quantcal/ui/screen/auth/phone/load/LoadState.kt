package net.techandgraphics.quantcal.ui.screen.auth.phone.load

import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel

data class LoadState(
  val companies: List<CompanyUiModel> = listOf(),
)
