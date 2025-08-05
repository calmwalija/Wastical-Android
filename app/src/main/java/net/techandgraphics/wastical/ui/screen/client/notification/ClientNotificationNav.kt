@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.client.notification

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.wastical.ui.Route

fun NavGraphBuilder.ClientNotificationNav(navController: NavHostController) {
  composable<Route.Client.Notification> {
    with(hiltViewModel<ClientNotificationViewModel>()) {
      val state = state.collectAsState().value
      ClientNotificationScreen(state) { event ->
        when (event) {
          ClientNotificationEvent.Button.BackHandler -> navController.navigateUp()
          is ClientNotificationEvent.Load -> Unit
        }
      }
    }
  }
}
