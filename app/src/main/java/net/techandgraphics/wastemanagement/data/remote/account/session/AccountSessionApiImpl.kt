package net.techandgraphics.wastemanagement.data.remote.account.session

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class AccountSessionApiImpl @Inject constructor(
  private val httpClient: HttpClient,
) : AccountSessionApi {

  override suspend fun get() = httpClient
    .get("session")
    .body<AccountSessionResponse>()
}
