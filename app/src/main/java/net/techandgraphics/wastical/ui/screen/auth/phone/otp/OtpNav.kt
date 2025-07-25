@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.auth.phone.otp

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastical.ui.Route
import net.techandgraphics.wastical.ui.screen.auth.phone.PhoneRoute

fun NavGraphBuilder.OtpNav(navController: NavHostController) {
  composable<PhoneRoute.Opt> {
    with(hiltViewModel<OtpViewModel>()) {
      val phone = it.toRoute<PhoneRoute.Opt>().phone
      val state = state.collectAsState().value
      LaunchedEffect(phone) { onEvent(OtpEvent.Load(phone)) }
      OtpScreen(state, channel) { event ->
        when (event) {
          OtpEvent.Goto.Home ->
            navController.navigate(Route.Load(false)) { popUpTo(0) }

          else -> onEvent(event)
        }
      }
    }
  }
}
