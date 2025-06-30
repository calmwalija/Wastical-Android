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

  @Serializable
  sealed interface Company : Route {

    @Serializable sealed interface Info : Company {
      @Serializable data object This : Client

      @Serializable data object Plan : Client

      @Serializable data object Method : Client
    }

    @Serializable sealed interface Payment : Company {
      @Serializable data class Verify(val ofType: String) : Payment

      @Serializable data class Pending(val id: Long) : Payment

      @Serializable data object Timeline : Payment
    }

    @Serializable data object Home : Company

    @Serializable data object PerLocation : Company

    @Serializable data class LocationOverview(val id: Long) : Company

    @Serializable data class ClientLocation(val id: Long) : Company

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
