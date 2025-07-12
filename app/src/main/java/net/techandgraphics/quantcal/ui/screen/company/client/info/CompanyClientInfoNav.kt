@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.company.client.info

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.quantcal.ui.screen.company.CompanyRoute

fun NavGraphBuilder.CompanyClientInfoNav(navController: NavHostController) {
  composable<CompanyRoute.ClientInfo> {
    with(hiltViewModel<CompanyClientInfoViewModel>()) {
      val state = state.collectAsState().value
      val id = it.toRoute<CompanyRoute.ClientInfo>().id
      LaunchedEffect(id) { onEvent(CompanyClientInfoEvent.Load(id)) }
      CompanyClientInfoScreen(state, channel) { event ->
        when (event) {
          CompanyClientInfoEvent.Button.BackHandler -> navController.navigateUp()
          else -> onEvent(event)
        }
      }
    }
  }
}
