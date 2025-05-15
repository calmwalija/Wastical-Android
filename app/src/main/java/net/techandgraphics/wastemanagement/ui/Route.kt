package net.techandgraphics.wastemanagement.ui

import kotlinx.serialization.Serializable

sealed interface Route {
  @Serializable data object SignIn : Route

  @Serializable data object SignUp : Route

  @Serializable data object Home : Route

  @Serializable data object Transaction : Route

  @Serializable data object Payment : Route
}
