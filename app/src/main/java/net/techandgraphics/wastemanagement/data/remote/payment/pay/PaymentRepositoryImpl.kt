package net.techandgraphics.wastemanagement.data.remote.payment.pay

import com.google.gson.Gson
import net.techandgraphics.wastemanagement.data.remote.AppApi
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(private val api: AppApi) : PaymentRepository {

  override suspend fun onPay(file: File, paymentRequest: PaymentRequest): PaymentResponse {
    val requestPart = Gson().toJson(paymentRequest).toRequestBody("application/json".toMediaType())
    val fileRequestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
    val filePart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)
    return run { api.paymentApi.pay(filePart, requestPart) }
  }
}
