package net.techandgraphics.wastical.data.remote.account

import net.techandgraphics.wastical.data.remote.ServerResponse
import net.techandgraphics.wastical.data.remote.account.plan.AccountPaymentPlanRequest
import net.techandgraphics.wastical.data.remote.account.plan.AccountPaymentPlanResponse
import net.techandgraphics.wastical.data.remote.account.token.AccountFcmTokenRequest
import net.techandgraphics.wastical.data.remote.account.token.AccountFcmTokenResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface AccountApi {

  @GET suspend fun getRaw(@Url url: String): ResponseBody

  @GET("session/{id}")
  suspend fun get(@Path("id") id: Long): ServerResponse

  @PUT("account/plan/{id}")
  suspend fun plan(
    @Path("id") id: Long,
    @Body request: AccountPaymentPlanRequest,
  ): AccountPaymentPlanResponse

  @POST("account")
  suspend fun create(@Body accountRequest: AccountRequest): ServerResponse

  @PUT("account/{id}")
  suspend fun put(@Path("id") id: Long, @Body accountRequest: AccountRequest): AccountResponse

  @PUT("account/demographic/{id}")
  suspend fun demographic(@Path("id") id: Long, @Body request: AccountRequest): AccountResponse

  @POST("account/verify")
  suspend fun verify(@Body contact: String): AccountResponse

  @POST("fcm_token")
  suspend fun fcmToken(@Body request: AccountFcmTokenRequest): AccountFcmTokenResponse
}
