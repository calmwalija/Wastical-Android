package net.techandgraphics.wastemanagement.keycloak

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import javax.inject.Inject

class JwtManager @Inject constructor(private val keycloakApi: KeycloakApi) {

  private val jwtParser = Jwts.parser()

  private fun String.getToken() = substringBeforeLast('.').plus(".")

  fun toJwtAccount(jwt: String): JwtAccount? {
    return try {
      val claim = jwtParser.parseClaimsJwt(jwt.getToken()).body
      val jwtAccount = Gson().fromJson<JwtAccount>(Gson().toJson(claim), JwtAccount::class.java)
      return jwtAccount
    } catch (jwtException: JwtException) {
      println(jwtException)
      null
    }
  }

  fun jwtValidator(accessToken: String): Result<Boolean> {
    return try {
      jwtParser.parseClaimsJwt(accessToken.getToken()).body
      Result.success(true)
    } catch (jwtException: ExpiredJwtException) {
      Result.failure(jwtException)
    }
  }

  suspend fun fetchAccessToken(keycloakSignInRequest: KeycloakSignInRequest): Result<JsonObject?> {
    return try {
      Result.success(keycloakApi.getToken(keycloakSignInRequest).body())
    } catch (exception: Exception) {
      Result.failure(exception)
    }
  }
}
