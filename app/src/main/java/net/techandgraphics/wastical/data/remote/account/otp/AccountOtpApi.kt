package net.techandgraphics.wastical.data.remote.account.otp

import net.techandgraphics.wastical.data.remote.ServerResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountOtpApi {

  @POST("otp")
  suspend fun otp(@Body contact: String): ServerResponse
}
