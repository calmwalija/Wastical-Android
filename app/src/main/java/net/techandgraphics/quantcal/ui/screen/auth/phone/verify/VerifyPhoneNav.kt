@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.auth.phone.verify

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.quantcal.ui.Route
import net.techandgraphics.quantcal.ui.screen.auth.phone.PhoneRoute

fun NavGraphBuilder.VerifyPhoneNav(navController: NavHostController) {
  composable<PhoneRoute.Verify> {
    with(hiltViewModel<VerifyPhoneViewModel>()) {
      val state = state.collectAsState().value
      VerifyPhoneScreen(state, channel) { event ->
        when (event) {
          is VerifyPhoneEvent.Goto.Otp -> navController.navigate(PhoneRoute.Opt(event.phone))
          is VerifyPhoneEvent.Goto.Home -> {
            navController.navigate(Route.Load(false)) { popUpTo(0) }
          }

          else -> onEvent(event)
        }
      }
    }
  }
}
