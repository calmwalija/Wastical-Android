package net.techandgraphics.wastical.data.remote.company

interface CompanyApi {
  suspend fun get(): List<CompanyResponse>
}
