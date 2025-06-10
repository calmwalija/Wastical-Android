package net.techandgraphics.wastemanagement.data.remote.account

import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanRequest
import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanResponse
import net.techandgraphics.wastemanagement.data.remote.account.session.AccountSessionResponse
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenRequest
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface AccountApi {

  @GET suspend fun getRaw(@Url url: String): ResponseBody

  @GET("session/{id}")
  suspend fun get(@Path("id") id: Long): AccountSessionResponse

  @PUT("account/plan/{id}")
  suspend fun plan(
    @Path("id") id: Long,
    @Body request: AccountPaymentPlanRequest,
  ): AccountPaymentPlanResponse

  suspend fun verify(contact: String): AccountSessionResponse
  suspend fun fcmToken(request: AccountFcmTokenRequest): AccountFcmTokenResponse
}

const val ACCOUNT_ID = 600L
