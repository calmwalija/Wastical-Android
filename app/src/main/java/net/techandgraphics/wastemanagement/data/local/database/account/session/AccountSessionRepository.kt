package net.techandgraphics.wastemanagement.data.local.database.account.session

interface AccountSessionRepository {
  suspend fun clientSession()
  suspend fun companySession()
}
