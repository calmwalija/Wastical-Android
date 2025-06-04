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

    @Serializable data object Info : Client

    @Serializable sealed interface Payment : Company {
      @Serializable data object Verify : Payment
    }

    @Serializable data object Home : Company

    @Serializable
    sealed interface Client : Company {
      @Serializable data object Browse : Client

      @Serializable data object Create : Company

      @Serializable data class Plan(val id: Long) : Company

      @Serializable data class History(val id: Long) : Company

      @Serializable data class Profile(val id: Long) : Client

      @Serializable data class Payment(val id: Long) : Client
    }
  }
}
