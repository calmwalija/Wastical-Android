package net.techandgraphics.wastemanagement.data.local.database.account.session

import net.techandgraphics.wastemanagement.data.remote.ServerResponse

interface AccountSessionRepository {
  suspend fun fetchSession()
  suspend fun purseData(data: ServerResponse, onProgress: suspend (Int, Int) -> Unit)
}
