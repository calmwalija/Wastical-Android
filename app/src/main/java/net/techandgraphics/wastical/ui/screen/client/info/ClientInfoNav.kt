@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.client.info

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastical.ui.Route

fun NavGraphBuilder.ClientInfoNav(navController: NavHostController) {
  composable<Route.Client.Info> {
    with(hiltViewModel<ClientInfoViewModel>()) {
      val state = state.collectAsState().value
      val id = it.toRoute<Route.Client.Info>().id
      LaunchedEffect(id) { onEvent(ClientInfoEvent.Load(id)) }
      ClientInfoScreen(state, channel) { event ->
        when (event) {
          ClientInfoEvent.Button.BackHandler -> navController.navigateUp()
          else -> onEvent(event)
        }
      }
    }
  }
}
