@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.payment.timeline

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute
import net.techandgraphics.wastical.ui.screen.company.location.overview.CompanyPaymentLocationOverviewEvent
import net.techandgraphics.wastical.ui.screen.company.location.overview.CompanyPaymentLocationOverviewScreen
import net.techandgraphics.wastical.ui.screen.company.location.overview.CompanyPaymentLocationOverviewViewModel

fun NavGraphBuilder.PaymentTimelineNav(navController: NavHostController) {
  composable<CompanyRoute.LocationOverview> {
    with(hiltViewModel<CompanyPaymentLocationOverviewViewModel>()) {
      val id = it.toRoute<CompanyRoute.LocationOverview>().id
      val state = state.collectAsState().value
      LaunchedEffect(id) { onEvent(CompanyPaymentLocationOverviewEvent.Load(id)) }
      CompanyPaymentLocationOverviewScreen(state) { event ->
        when (event) {
          CompanyPaymentLocationOverviewEvent.Button.BackHandler -> navController.navigateUp()

          is CompanyPaymentLocationOverviewEvent.Button.ClientCreate ->
            navController.navigate(CompanyRoute.ClientCreate(event.locationId))

          is CompanyPaymentLocationOverviewEvent.Goto.Profile ->
            navController.navigate(CompanyRoute.ClientProfile(event.id))

          is CompanyPaymentLocationOverviewEvent.Button.SortBy -> onEvent(event)
          else -> Unit
        }
      }
    }
  }
}
