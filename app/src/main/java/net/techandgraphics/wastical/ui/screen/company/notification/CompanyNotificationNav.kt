@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.notification

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyNotificationNav(navController: NavHostController) {
  composable<CompanyRoute.Notifications> {
    with(hiltViewModel<CompanyNotificationViewModel>()) {
      val state = state.collectAsState().value
      CompanyNotificationScreen(state) { event ->
        when (event) {
          CompanyNotificationEvent.Button.BackHandler -> navController.navigateUp()
          else -> onEvent(event)
        }
      }
    }
  }
}
