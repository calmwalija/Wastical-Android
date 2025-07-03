package net.techandgraphics.quantcal.data.remote.company

interface CompanyApi {
  suspend fun get(): List<CompanyResponse>
}
