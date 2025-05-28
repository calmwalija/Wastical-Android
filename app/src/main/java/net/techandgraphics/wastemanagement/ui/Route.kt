package net.techandgraphics.wastemanagement.ui

import kotlinx.serialization.Serializable

sealed interface Route {

  @Serializable sealed interface Client : Route {
    @Serializable data object Home : Client

    @Serializable data object Payment : Client
  }

  @Serializable data object SignIn : Route

  @Serializable data object Invoice : Route

  @Serializable data class PaymentResponse(
    val isSuccess: Boolean,
    val error: String? = null,
  ) : Route

  @Serializable
  sealed interface Company : Route {

    @Serializable
    sealed interface Account : Company {
      @Serializable data object Create : Account
    }

    @Serializable data object Payment : Company
  }
}
