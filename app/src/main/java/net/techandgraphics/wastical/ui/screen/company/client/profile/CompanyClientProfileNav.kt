@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.client.profile

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastical.openDialer
import net.techandgraphics.wastical.openWhatsApp
import net.techandgraphics.wastical.toPhone265
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute
import net.techandgraphics.wastical.ui.screen.company.client.profile.CompanyClientProfileEvent.Button
import net.techandgraphics.wastical.ui.screen.company.client.profile.CompanyClientProfileEvent.Goto
import net.techandgraphics.wastical.ui.screen.company.client.profile.CompanyClientProfileEvent.Load
import net.techandgraphics.wastical.ui.screen.company.client.profile.CompanyClientProfileEvent.Option

fun NavGraphBuilder.CompanyClientProfileNav(navController: NavHostController) {
  composable<CompanyRoute.ClientProfile> {
    with(hiltViewModel<CompanyClientProfileViewModel>()) {
      val id = it.toRoute<CompanyRoute.ClientProfile>().id
      val state = state.collectAsState().value
      val context = LocalContext.current
      LaunchedEffect(id) { onEvent(Load(id)) }
      CompanyClientProfileScreen(state, channel, templates) { event ->
        when (event) {
          Option.History -> navController.navigate(CompanyRoute.ClientHistory(id))
          Option.Location -> navController.navigate(CompanyRoute.ClientLocation(id))
          Option.Payment -> navController.navigate(CompanyRoute.MakePayment(id))
          Option.Plan -> navController.navigate(CompanyRoute.ClientPlan(id))
          Option.Pending -> navController.navigate(CompanyRoute.PaymentPending(id))
          Option.Invoice -> navController.navigate(CompanyRoute.PaymentReceipt(id))
          Option.Info -> navController.navigate(CompanyRoute.ClientInfo(id))

          Goto.BackHandler -> navController.navigateUp()

          is Goto.Location -> {
            navController.navigate(CompanyRoute.LocationOverview(event.id)) {
              popUpTo(navController.graph.startDestinationId) {
                inclusive = false
              }
              launchSingleTop = true
            }
          }

          is Button.Phone -> context.openDialer(event.contact)
          is Goto.WhatsApp -> context.openWhatsApp(event.contact.toPhone265())
          is Goto.Call -> context.openDialer(event.contact)

          else -> onEvent(event)
        }
      }
    }
  }
}
