@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.company.client.pending

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

fun NavGraphBuilder.CompanyClientPendingPaymentNav(navController: NavHostController) {
  composable<CompanyRoute.PaymentPending> {
    with(hiltViewModel<CompanyClientPendingPaymentViewModel>()) {
      val state = state.collectAsState().value
      val id = it.toRoute<CompanyRoute.PaymentPending>().id
      val context = LocalContext.current
      LaunchedEffect(id) { onEvent(CompanyClientPendingPaymentEvent.Load(id)) }
      CompanyClientPendingPaymentScreen(state) { event ->
        when (event) {
          CompanyClientPendingPaymentEvent.Goto.BackHandler -> navController.navigateUp()
          is CompanyClientPendingPaymentEvent.Goto.Location -> {
            navController.navigate(CompanyRoute.LocationOverview(event.id)) {
              popUpTo(navController.graph.startDestinationId) {
                inclusive = false
              }
              launchSingleTop = true
            }
          }

          is CompanyClientPendingPaymentEvent.Button.Phone -> context.openDialer(event.contact)

          else -> onEvent(event)
        }
      }
    }
  }
}
