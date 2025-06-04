package net.techandgraphics.wastemanagement.ui.screen.auth.phone

import kotlinx.serialization.Serializable

sealed interface PhoneRoute {
  @Serializable data object Verify : PhoneRoute

  @Serializable data class Opt(val phone: String) : PhoneRoute
}
