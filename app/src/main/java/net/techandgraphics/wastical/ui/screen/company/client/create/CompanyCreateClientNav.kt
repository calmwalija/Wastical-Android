@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.client.create

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyCreateClientNav(navController: NavHostController) {
  composable<CompanyRoute.ClientCreate> {
    with(hiltViewModel<CompanyCreateClientViewModel>()) {
      val locationId = it.toRoute<CompanyRoute.ClientCreate>().locationId
      LaunchedEffect(locationId) { onEvent(CompanyCreateClientEvent.Load(locationId)) }
      val state = state.collectAsState().value
      CompanyCreateClientScreen(state, channel) { event ->
        when (event) {
          CompanyCreateClientEvent.Goto.BackHandler -> navController.navigateUp()
          else -> onEvent(event)
        }
      }
    }
  }
}
