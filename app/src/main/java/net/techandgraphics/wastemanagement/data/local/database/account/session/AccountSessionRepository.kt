package net.techandgraphics.wastemanagement.data.local.database.account.session

interface AccountSessionRepository {
  suspend operator fun invoke()
}
