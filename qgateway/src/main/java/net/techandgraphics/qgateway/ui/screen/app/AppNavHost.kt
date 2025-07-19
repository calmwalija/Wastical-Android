package net.techandgraphics.qgateway.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.techandgraphics.qgateway.ui.screen.Route
import net.techandgraphics.qgateway.ui.screen.otp.OtpScreen
import net.techandgraphics.qgateway.ui.screen.otp.OtpViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
  NavHost(
    navController = navController,
    startDestination = Route.Otp
  ) {
    composable<Route.Otp> {
      with(hiltViewModel<OtpViewModel>()) {
        val state = state.collectAsState().value
        OtpScreen(state)
      }
    }
  }
}
