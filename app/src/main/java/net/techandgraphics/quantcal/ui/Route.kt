package net.techandgraphics.quantcal.ui

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
}
