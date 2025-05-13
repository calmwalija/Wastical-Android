package net.techandgraphics.wastemanagement.ui.screen.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.jsonwebtoken.ExpiredJwtException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.account.AuthenticatorHelper
import net.techandgraphics.wastemanagement.keycloak.AccessTokenResponse
import net.techandgraphics.wastemanagement.keycloak.JwtManager
import net.techandgraphics.wastemanagement.keycloak.KeycloakSignInRequest
import net.techandgraphics.wastemanagement.ui.screen.signIn.SignInChannel.LoginException
import net.techandgraphics.wastemanagement.ui.screen.signIn.SignInChannel.Response
import net.techandgraphics.wastemanagement.ui.screen.signIn.SignInEvent.Button
import net.techandgraphics.wastemanagement.ui.screen.signIn.SignInEvent.Input
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
  private val jwtManager: JwtManager,
  private val accountHelper: AuthenticatorHelper,
  private val gson: Gson,
) : ViewModel() {

  private val _channel = Channel<SignInChannel>()
  private val _state = MutableStateFlow(SignInState())
  val channel = _channel.receiveAsFlow()
  val state = _state.asStateFlow()
  private var job: Job? = null

  private fun onFetchAccessToken() {
    job = viewModelScope.launch {
      job?.cancel()
      delay(2_000)
      val chunkedContactNumber = state.value.contactNumber.chunked(3).joinToString("-")
      val signInRequest = KeycloakSignInRequest(chunkedContactNumber, state.value.password)
      jwtManager.fetchAccessToken(signInRequest)
        .onSuccess { jsonObject ->
          val accessToken = gson.fromJson(jsonObject, AccessTokenResponse::class.java)
          accountHelper.addAccount(accessToken, jwtManager)
          _channel.send(Response.Success)
        }
        .onFailure { throwable ->
          println(throwable)
          when (throwable) {
            is HttpException -> {
              _channel.send(
                Response.Failure(
                  exception = LoginException.Http(
                    code = throwable.code(),
                    message = throwable.message,
                  ),
                ),
              )
            }

            is IOException -> {
              _channel.send(
                Response.Failure(
                  exception = LoginException.IO(
                    message = throwable.message,
                  ),
                ),
              )
            }

            else ->
              _channel.send(
                Response.Failure(
                  exception = LoginException.Default(
                    message = throwable.message,
                  ),
                ),
              )
          }
        }
    }
  }

  init {
    viewModelScope.launch {
      accountHelper.getAccessToken()?.let {
        jwtManager.jwtValidator(it.accessToken)
          .onSuccess { if (it) _channel.send(Response.Success) }
          .onFailure { throwable ->
            when (throwable) {
              is ExpiredJwtException -> {
                println("Error: The token has expired at ${throwable.claims.expiration}")
              }

              else ->
                println("Error: JWT is invalid. ${throwable.localizedMessage}")
            }
          }
      }
    }
  }

  private fun onCredentials(event: Input.Credentials) {
    when (event.type) {
      Input.Type.ContactNumber -> _state.update { it.copy(contactNumber = event.value) }
      Input.Type.Password -> _state.update { it.copy(password = event.value) }
    }
  }

  fun onEvent(event: SignInEvent) {
    when (event) {
      is Button.AccessToken -> onFetchAccessToken()
      is Input.Credentials -> onCredentials(event)
      else -> Unit
    }
  }
}
