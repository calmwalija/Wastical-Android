package net.techandgraphics.wastemanagement.data.local.database.session

interface SessionRepository {
  suspend operator fun invoke()
}
