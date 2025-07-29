@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.client.info

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.wastical.ui.Route

fun NavGraphBuilder.ClientInfoNav(navController: NavHostController) {
  composable<Route.Client.Info> {
    with(hiltViewModel<ClientInfoViewModel>()) {
      val state = state.collectAsState().value
      ClientInfoScreen(state, channel) { event ->
        when (event) {
          ClientInfoEvent.Button.BackHandler -> navController.navigateUp()
          else -> onEvent(event)
        }
      }
    }
  }
}
