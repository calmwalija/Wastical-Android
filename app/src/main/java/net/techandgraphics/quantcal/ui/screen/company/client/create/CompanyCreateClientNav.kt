@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.company.client.create

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.quantcal.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyCreateClientNav(navController: NavHostController) {
  composable<CompanyRoute.ClientCreate> {
    with(hiltViewModel<CompanyCreateClientViewModel>()) {
      val state = state.collectAsState().value
      CompanyCreateClientScreen(state, channel) { event ->
        when (event) {
          CompanyCreateClientEvent.Goto.BackHandler -> navController.navigateUp()
          is CompanyCreateClientEvent.Goto.Profile ->
            navController.navigate(CompanyRoute.ClientProfile(event.id)) {
              popUpTo(CompanyRoute.ClientCreate) { inclusive = true }
            }

          else -> onEvent(event)
        }
      }
    }
  }
}
