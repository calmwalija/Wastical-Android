package net.techandgraphics.wastemanagement.data.remote.dto

import net.techandgraphics.wastemanagement.data.remote.company.CompanyContactResponse
import net.techandgraphics.wastemanagement.data.remote.company.CompanyResponse

data class CompanyWithContacts(
  val company: CompanyResponse,
  val contacts: List<CompanyContactResponse>,
)
