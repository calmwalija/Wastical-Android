package net.techandgraphics.wastemanagement.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.techandgraphics.wastemanagement.ui.MainViewModel
import net.techandgraphics.wastemanagement.ui.Route
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
    startDestination = Route.SignIn,
  ) {
    composable<Route.SignIn> {
      with(hiltViewModel<SignInViewModel>()) {
        val state = state.collectAsState().value
        SignInScreen(state, channel) { event ->
          when (event) {
            is SignInEvent.GoTo -> when (event) {
              SignInEvent.GoTo.Main -> {

              }
//                navController.navigate(Route.Main) {
//                popUpTo(Route.SignIn) { inclusive = true }
//              }
            }

            else -> onEvent(event)
          }
        }
      }
    }

  }
}
