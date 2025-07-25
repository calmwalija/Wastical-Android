package net.techandgraphics.wastical.data.remote.payment

import net.techandgraphics.wastical.data.remote.ServerResponse
import net.techandgraphics.wastical.data.remote.payment.pay.PaymentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface PaymentApi {

  @GET suspend fun getRaw(@Url url: String): ResponseBody

  @GET("payment/latest")
  suspend fun fetchLatest(
    @Query("account_id") accountId: Long,
    @Query("epoch_second") epochSecond: Long,
  ): ServerResponse

  @GET("payment/latest_by_company")
  suspend fun fetchLatestByCompany(
    @Query("account_id") accountId: Long,
    @Query("epoch_second") epochSecond: Long,
  ): ServerResponse

  @GET("payment/latest")
  suspend fun fetchLatest(@QueryMap params: Map<String, Any>): List<PaymentResponse>

  @POST("payment")
  suspend fun pay(@Body request: PaymentRequest): ServerResponse

  @Multipart
  @POST("payment/screenshot")
  suspend fun payWithScreenshot(
    @Part file: MultipartBody.Part,
    @Part("request") body: RequestBody,
  ): ServerResponse

  @PUT("payment/{id}")
  suspend fun put(
    @Path("id") id: Long,
    @Body request: PaymentRequest,
  ): ServerResponse

  @DELETE("payment/{id}")
  suspend fun delete(@Path("id") id: Long): Long
}
