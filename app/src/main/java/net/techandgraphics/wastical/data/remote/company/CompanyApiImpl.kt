package net.techandgraphics.wastical.data.remote.company

import javax.inject.Inject

class CompanyApiImpl @Inject constructor() : CompanyApi {

  override suspend fun get(): List<CompanyResponse> {
    return emptyList()
  }
}
