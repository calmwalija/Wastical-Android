package net.techandgraphics.wastemanagement.data.remote.payment

import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PaymentApi {
  @Multipart
  @POST("payment")
  suspend fun pay(
    @Part file: MultipartBody.Part,
    @Part("request") body: RequestBody,
  ): PaymentResponse
}
