package net.techandgraphics.wastemanagement.data.remote.account.session

interface AccountSessionApi {
  suspend fun get(): AccountSessionResponse
}
