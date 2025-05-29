package net.techandgraphics.wastemanagement.ui

import kotlinx.serialization.Serializable

sealed interface Route {

  @Serializable sealed interface Client : Route {
    @Serializable data object Home : Client

    @Serializable data object Payment : Client

    @Serializable data object Invoice : Client

    @Serializable data class PaymentResponse(
      val isSuccess: Boolean,
      val error: String? = null,
    ) : Client
  }

  @Serializable data object SignIn : Route

  @Serializable
  sealed interface Company : Route {

    @Serializable
    sealed interface Account : Company {
      @Serializable data object Create : Account
    }

    @Serializable sealed interface Payment : Company {
      @Serializable data object Verify : Payment
    }
  }
}
