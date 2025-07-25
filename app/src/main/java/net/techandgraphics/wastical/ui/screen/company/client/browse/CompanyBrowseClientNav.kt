@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.client.browse

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyBrowseClientNav(navController: NavHostController) {
  composable<CompanyRoute.ClientBrowse> {
    with(hiltViewModel<CompanyBrowseClientViewModel>()) {
      val state = state.collectAsState().value
      CompanyBrowseClientScreen(state, channel) { event ->
        when (event) {
          is CompanyBrowseClientListEvent.Goto ->
            when (event) {
              CompanyBrowseClientListEvent.Goto.BackHandler -> navController.navigateUp()

              is CompanyBrowseClientListEvent.Goto.Profile -> {
                onEvent(CompanyBrowseClientListEvent.Button.HistoryTag)
                navController.navigate(CompanyRoute.ClientProfile(event.id))
              }
            }

          else -> onEvent(event)
        }
      }
    }
  }
}
