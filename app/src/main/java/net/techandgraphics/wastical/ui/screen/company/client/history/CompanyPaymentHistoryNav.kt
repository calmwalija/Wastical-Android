@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.client.history

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastical.openDialer
import net.techandgraphics.wastical.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyPaymentHistoryNav(navController: NavHostController) {
  composable<CompanyRoute.ClientHistory> {
    with(hiltViewModel<CompanyPaymentHistoryViewModel>()) {
      val id = it.toRoute<CompanyRoute.ClientHistory>().id
      val state = state.collectAsState().value
      val context = LocalContext.current
      LaunchedEffect(id) { onEvent(CompanyPaymentHistoryEvent.Load(id)) }
      CompanyPaymentHistoryScreen(state) { event ->
        when (event) {
          is CompanyPaymentHistoryEvent.Button.Phone -> context.openDialer(event.contact)
          CompanyPaymentHistoryEvent.Goto.BackHandler -> navController.navigateUp()
          is CompanyPaymentHistoryEvent.Goto.Location -> {
            navController.navigate(CompanyRoute.LocationOverview(event.id)) {
              popUpTo(navController.graph.startDestinationId) {
                inclusive = false
              }
              launchSingleTop = true
            }
          }

          else -> onEvent(event)
        }
      }
    }
  }
}
