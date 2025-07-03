package net.techandgraphics.wastemanagement.keycloak

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.Date
import javax.inject.Inject

class JwtManager @Inject constructor(private val keycloakApi: KeycloakApi) {

  fun toJwtAccount(jwt: String): JwtAccount? {
    return try {
      val decoded: DecodedJWT = JWT.decode(jwt)
      val claimsMap = decoded.claims.mapValues { it.value }
      val json = Gson().toJson(claimsMap)
      Gson().fromJson(json, JwtAccount::class.java)
    } catch (e: Exception) {
      println("JWT decode failed: ${e.message}")
      null
    }
  }

  fun jwtValidator(accessToken: String): Result<Boolean> {
    return try {
      val decoded: DecodedJWT = JWT.decode(accessToken)
      val exp: Date? = decoded.expiresAt
      if (exp != null && exp.before(Date())) {
        Result.failure(Exception("Token expired"))
      } else {
        Result.success(true)
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun fetchAccessToken(keycloakSignInRequest: KeycloakSignInRequest): JsonObject? {
    return keycloakApi.getToken(keycloakSignInRequest)
  }
}
