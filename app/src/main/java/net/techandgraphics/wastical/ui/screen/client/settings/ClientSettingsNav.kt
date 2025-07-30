@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.client.settings

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastical.ui.Route

fun NavGraphBuilder.ClientSettingsNav(navController: NavHostController) {
  composable<Route.Client.Settings> {
    with(hiltViewModel<ClientSettingsViewModel>()) {
      val state = state.collectAsState().value
      val id = it.toRoute<Route.Client.Settings>().id
      LaunchedEffect(id) { onEvent(ClientSettingsEvent.Load(id)) }
      ClientSettingsScreen(state) { event ->
        when (event) {
          ClientSettingsEvent.Button.BackHandler -> navController.navigateUp()
          is ClientSettingsEvent.Goto -> when (event) {
            ClientSettingsEvent.Goto.Settings -> navController.navigate(Route.Client.Info(id))
          }

          else -> onEvent(event)
        }
      }
    }
  }
}
