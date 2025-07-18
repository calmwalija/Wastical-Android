package net.techandgraphics.qgateway.data.remote

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface SmsApi {

  @GET suspend fun gRaw(@Url url: String): ResponseBody

  @POST("fcm_token")
  suspend fun fcmToken(@Body request: FcmTokenRequest): FcmTokenResponse

  @GET("otp")
  suspend fun getLatest(@Query("epoch_second") epochSecond: Long): List<OtpResponse>
}
