package net.techandgraphics.wastemanagement.data.remote.account

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.techandgraphics.wastemanagement.data.remote.account.session.AccountSessionResponse
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenRequest
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenResponse
import javax.inject.Inject

class AccountApiImpl @Inject constructor(
  private val httpClient: HttpClient,
) : AccountApi {

  override suspend fun get() = httpClient
    .get("session/$ACCOUNT_ID")
    .body<AccountSessionResponse>()

  override suspend fun fcmToken(request: AccountFcmTokenRequest) =
    httpClient.post("fcm_token") {
      contentType(ContentType.Application.Json)
      setBody(Gson().toJson(request))
    }.body<AccountFcmTokenResponse>()
}

const val ACCOUNT_ID = 3L
