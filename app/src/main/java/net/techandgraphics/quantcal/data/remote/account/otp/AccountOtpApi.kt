package net.techandgraphics.quantcal.data.remote.account.otp

import retrofit2.http.Body
import retrofit2.http.POST

interface AccountOtpApi {

  @POST("otp")
  suspend fun otp(@Body contact: String): AccountOtpResponse
}
