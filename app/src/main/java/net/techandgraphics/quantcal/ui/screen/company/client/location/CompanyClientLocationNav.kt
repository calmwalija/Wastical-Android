@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.company.client.location

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.quantcal.openDialer
import net.techandgraphics.quantcal.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyClientLocationNav(navController: NavHostController) {
  composable<CompanyRoute.ClientLocation> {
    with(hiltViewModel<CompanyClientLocationViewModel>()) {
      val state = state.collectAsState().value
      val id = it.toRoute<CompanyRoute.ClientLocation>().id
      val context = LocalContext.current
      LaunchedEffect(id) { onEvent(CompanyClientLocationEvent.Load(id)) }
      CompanyClientLocationScreen(state) { event ->
        when (event) {
          CompanyClientLocationEvent.Goto.BackHandler -> navController.navigateUp()

          is CompanyClientLocationEvent.Goto.Location ->
            navController.navigate(CompanyRoute.LocationOverview(event.id))

          is CompanyClientLocationEvent.Button.Phone -> context.openDialer(event.contact)

          else -> onEvent(event)
        }
      }
    }
  }
}
