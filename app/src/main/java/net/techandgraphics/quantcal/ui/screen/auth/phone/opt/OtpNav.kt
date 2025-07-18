@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.auth.phone.opt

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.quantcal.ui.screen.auth.phone.PhoneRoute

fun NavGraphBuilder.OtpNav(navController: NavHostController) {
  composable<PhoneRoute.Opt> {
    with(hiltViewModel<OptViewModel>()) {
      val phone = it.toRoute<PhoneRoute.Opt>().phone
      val state = state.collectAsState().value
      LaunchedEffect(phone) { onEvent(OptEvent.Load(phone)) }
      OptScreen(state, ::onEvent)
    }
  }
}
