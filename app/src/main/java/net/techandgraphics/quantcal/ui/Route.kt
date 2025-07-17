package net.techandgraphics.quantcal.ui

import kotlinx.serialization.Serializable

sealed interface Route {

  @Serializable data class Load(val logout: Boolean = false) : Client

  @Serializable sealed interface Client : Route {
    @Serializable data object Home : Client

    @Serializable data class Payment(val id: Long) : Client

    @Serializable data class Invoice(val id: Long) : Client

    @Serializable data class PaymentResponse(
      val isSuccess: Boolean,
      val error: String? = null,
    ) : Client
  }
}
