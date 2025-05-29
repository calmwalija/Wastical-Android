package net.techandgraphics.wastemanagement.data.remote.account.session

interface AccountSessionApiService {
  suspend fun get(): AccountSessionResponse
}
