@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.location.browse

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyBrowseLocationNav(navController: NavHostController) {
  composable<CompanyRoute.BrowseLocation> {
    with(hiltViewModel<CompanyBrowseLocationViewModel>()) {
      val state = state.collectAsState().value
      CompanyBrowseLocationScreen(state) { event ->
        when (event) {
          CompanyBrowseLocationEvent.Button.BackHandler -> navController.navigateUp()
          CompanyBrowseLocationEvent.Load -> Unit
          is CompanyBrowseLocationEvent.Goto.LocationOverview ->
            navController.navigate(CompanyRoute.LocationOverview(event.id))

          else -> onEvent(event)
        }
      }
    }
  }
}
