package net.techandgraphics.wcompanion.ui.screen

import kotlinx.serialization.Serializable

sealed interface Route {
  @Serializable data object Otp
}
