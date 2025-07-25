@file:Suppress("FunctionName")

package net.techandgraphics.wastical.ui.screen.company.client.plan

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

fun NavGraphBuilder.CompanyClientPlanNav(navController: NavHostController) {
  composable<CompanyRoute.ClientPlan> {
    with(hiltViewModel<CompanyClientPlanViewModel>()) {
      val id = it.toRoute<CompanyRoute.ClientPlan>().id
      val state = state.collectAsState().value
      val context = LocalContext.current
      LaunchedEffect(id) { onEvent(CompanyClientPlanEvent.Load(id)) }
      CompanyClientPlanScreen(state, channel) { event ->
        when (event) {
          CompanyClientPlanEvent.Goto.BackHandler -> navController.navigateUp()

          is CompanyClientPlanEvent.Goto.Location -> {
            navController.navigate(CompanyRoute.LocationOverview(event.id)) {
              popUpTo(navController.graph.startDestinationId) {
                inclusive = false
              }
              launchSingleTop = true
            }
          }

          is CompanyClientPlanEvent.Button.Phone -> context.openDialer(event.contact)

          else -> onEvent(event)
        }
      }
    }
  }
}
