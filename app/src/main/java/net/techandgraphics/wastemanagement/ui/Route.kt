package net.techandgraphics.wastemanagement.ui

import kotlinx.serialization.Serializable

sealed interface Route {
  @Serializable data object SignIn : Route

  @Serializable data object SignUp : Route

  @Serializable data object Main : Route

  @Serializable data class Inbox(val toId: Long) : Route
}
