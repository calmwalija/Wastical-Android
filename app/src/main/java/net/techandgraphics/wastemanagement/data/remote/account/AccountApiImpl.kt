package net.techandgraphics.wastemanagement.data.remote.account

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanResponse
import net.techandgraphics.wastemanagement.data.remote.account.session.AccountSessionResponse
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenRequest
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenResponse
import net.techandgraphics.wastemanagement.data.remote.toAccountPaymentPlanRequest
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
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

  override suspend fun verify(contact: String) =
    httpClient.post("verify") {
      contentType(ContentType.Application.Json)
      setBody(contact)
    }.body<AccountSessionResponse>()

  override suspend fun plan(plan: PaymentPlanUiModel, account: AccountUiModel) =
    httpClient.put("account/plan/${plan.id}") {
      contentType(ContentType.Application.Json)
      setBody(Gson().toJson(plan.toAccountPaymentPlanRequest(account)))
    }.body<AccountPaymentPlanResponse>()
}

const val ACCOUNT_ID = 399L
