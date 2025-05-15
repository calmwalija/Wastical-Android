package net.techandgraphics.wastemanagement.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.techandgraphics.wastemanagement.ui.MainViewModel
import net.techandgraphics.wastemanagement.ui.Route
import net.techandgraphics.wastemanagement.ui.screen.home.HomeScreen
import net.techandgraphics.wastemanagement.ui.screen.home.HomeViewModel
import net.techandgraphics.wastemanagement.ui.screen.payment.PaymentScreen
import net.techandgraphics.wastemanagement.ui.screen.payment.PaymentViewModel
import net.techandgraphics.wastemanagement.ui.screen.signIn.SignInEvent
import net.techandgraphics.wastemanagement.ui.screen.signIn.SignInScreen
import net.techandgraphics.wastemanagement.ui.screen.signIn.SignInViewModel

@Composable
fun AppNavHost(
  navController: NavHostController,
  viewModel: MainViewModel
) {
  NavHost(
    navController = navController,
    startDestination = Route.Home,
  ) {

    composable<Route.SignIn> {
      with(hiltViewModel<SignInViewModel>()) {
        val state = state.collectAsState().value
        SignInScreen(state, channel) { event ->
          when (event) {
            is SignInEvent.GoTo -> when (event) {
              SignInEvent.GoTo.Main -> Unit
            }

            else -> onEvent(event)
          }
        }
      }
    }

    composable<Route.Payment> {
      with(hiltViewModel<PaymentViewModel>()) {
        val state = state.collectAsState().value
        PaymentScreen(state, channel) {}
      }
    }

    composable<Route.Home> {
      with(hiltViewModel<HomeViewModel>()) {
        val state = state.collectAsState().value
        HomeScreen(state, channel) {}
      }
    }

  }
}
