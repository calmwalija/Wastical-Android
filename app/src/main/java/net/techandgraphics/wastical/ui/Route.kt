package net.techandgraphics.wastical.ui

import kotlinx.serialization.Serializable

sealed interface Route {

  @Serializable data class Load(val shouldLogout: Boolean = false) : Client

  @Serializable sealed interface Client : Route {
    @Serializable data object Home : Client

    @Serializable data class Payment(val id: Long) : Client

    @Serializable data class Invoice(val id: Long) : Client

    @Serializable data class Settings(val id: Long) : Client

    @Serializable data class Info(val id: Long) : Client

    @Serializable data class Notification(val id: Long) : Client

    @Serializable data class PaymentResponse(
      val isSuccess: Boolean,
      val error: String? = null,
    ) : Client
  }
}
