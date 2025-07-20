package net.techandgraphics.quantcal.data.remote.account.otp

import net.techandgraphics.quantcal.data.remote.ServerResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountOtpApi {

  @POST("otp")
  suspend fun otp(@Body contact: String): ServerResponse
}
