package net.techandgraphics.qgateway.ui.screen

import kotlinx.serialization.Serializable

sealed interface Route {
  @Serializable data object Otp
}
