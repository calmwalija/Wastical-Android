@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.company.client.profile

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.quantcal.openDialer
import net.techandgraphics.quantcal.ui.Route
import net.techandgraphics.quantcal.ui.screen.company.CompanyRoute
import net.techandgraphics.quantcal.ui.screen.company.client.profile.CompanyClientProfileEvent.Button
import net.techandgraphics.quantcal.ui.screen.company.client.profile.CompanyClientProfileEvent.Goto
import net.techandgraphics.quantcal.ui.screen.company.client.profile.CompanyClientProfileEvent.Load
import net.techandgraphics.quantcal.ui.screen.company.client.profile.CompanyClientProfileEvent.Option

fun NavGraphBuilder.CompanyClientProfileNav(navController: NavHostController) {
  composable<CompanyRoute.ClientProfile> {
    with(hiltViewModel<CompanyClientProfileViewModel>()) {
      val id = it.toRoute<CompanyRoute.ClientProfile>().id
      val state = state.collectAsState().value
      val context = LocalContext.current
      LaunchedEffect(id) { onEvent(Load(id)) }
      CompanyClientProfileScreen(state) { event ->
        when (event) {
          Option.History -> navController.navigate(Route.Company.Client.History(id))
          Option.Location -> navController.navigate(Route.Company.ClientLocation(id))
          Option.Payment -> navController.navigate(Route.Company.Client.Payment(id))
          Option.Plan -> navController.navigate(Route.Company.Client.Plan(id))
          Option.Pending -> navController.navigate(Route.Company.Payment.Pending(id))
          Option.Invoice -> navController.navigate(Route.Company.Client.Invoice(id))
          Option.Info -> navController.navigate(CompanyRoute.ClientInfo(id))

          Option.Revoke -> Unit

          Goto.BackHandler -> navController.navigateUp()

          is Goto.Location -> {
            navController.navigate(Route.Company.LocationOverview(event.id)) {
              popUpTo(navController.graph.startDestinationId) {
                inclusive = false
              }
              launchSingleTop = true
            }
          }

          is Button.Phone -> context.openDialer(event.contact)

          else -> Unit
        }
      }
    }
  }
}
