package net.techandgraphics.wastemanagement.data.remote.payment

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.InternalAPI
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentResponse
import java.io.File
import javax.inject.Inject

class PaymentApiImpl @Inject constructor(
  private val httpClient: HttpClient,
) : PaymentApi {

  companion object {
    private const val URL_STRING = "payment"
  }

  @OptIn(InternalAPI::class)
  override suspend fun pay(file: File, request: PaymentRequest): PaymentResponse {
    val formData = formData {
      append(
        "request",
        Gson().toJson(request),
        Headers.build {
          append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        },
      )
      append(
        "file",
        file.readBytes(),
        Headers.build {
          append(HttpHeaders.ContentDisposition, "filename=${file.name}")
        },
      )
    }

    return httpClient.post(URL_STRING) {
      setBody(MultiPartFormDataContent(formData))
      header("Authorization", "Bearer")
    }.body()
  }

  override suspend fun put(id: Long, request: PaymentRequest) =
    httpClient.put("$URL_STRING/$id") {
      url {
        header("Authorization", "Bearer")
      }
      contentType(ContentType.Application.Json)
      setBody(Gson().toJson(request))
    }.body<List<PaymentResponse>>()

  override suspend fun fetchLatest(accountId: Long, epochSecond: Long): List<PaymentResponse> {
    return httpClient.get("$URL_STRING/latest") {
      url {
        parameter("account_id", accountId)
        parameter("epoch_second", epochSecond)
      }
    }.body<List<PaymentResponse>>()
  }
}
