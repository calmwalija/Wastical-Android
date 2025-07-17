package net.techandgraphics.quantcal.data.local.database.account.session

import net.techandgraphics.quantcal.data.remote.ServerResponse

interface AccountSessionRepository {
  suspend fun fetchSession()
  suspend fun fetch(id: Long): ServerResponse
  suspend fun purseData(data: ServerResponse, onProgress: suspend (Int, Int) -> Unit)
}
