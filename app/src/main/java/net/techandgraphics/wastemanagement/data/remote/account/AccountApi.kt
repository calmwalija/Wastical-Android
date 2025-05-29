package net.techandgraphics.wastemanagement.data.remote.account

interface AccountApi {
  suspend fun get(url: String)
}
