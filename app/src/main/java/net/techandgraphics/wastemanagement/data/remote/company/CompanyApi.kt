package net.techandgraphics.wastemanagement.data.remote.company

interface CompanyApi {
  suspend fun get(): List<CompanyResponse>
}
