@file:Suppress("FunctionName")

package net.techandgraphics.wastemanagement.ui.screen.auth.phone

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastemanagement.ui.screen.auth.phone.opt.OptEvent
import net.techandgraphics.wastemanagement.ui.screen.auth.phone.opt.OptScreen
import net.techandgraphics.wastemanagement.ui.screen.auth.phone.opt.OptViewModel
import net.techandgraphics.wastemanagement.ui.screen.auth.phone.verify.VerifyPhoneEvent
import net.techandgraphics.wastemanagement.ui.screen.auth.phone.verify.VerifyPhoneScreen
import net.techandgraphics.wastemanagement.ui.screen.auth.phone.verify.VerifyPhoneViewModel

fun NavGraphBuilder.PhoneNavGraphBuilder(navController: NavHostController) {
  composable<PhoneRoute.Verify> {
    with(hiltViewModel<VerifyPhoneViewModel>()) {
      val state = state.collectAsState().value
      VerifyPhoneScreen(state, channel) { event ->
        when (event) {
          is VerifyPhoneEvent.Goto.Otp -> navController.navigate(PhoneRoute.Opt(event.phone))
          else -> onEvent(event)
        }
      }
    }
  }

  composable<PhoneRoute.Opt> {
    with(hiltViewModel<OptViewModel>()) {
      val phone = it.toRoute<PhoneRoute.Opt>().phone
      val state = state.collectAsState().value
      LaunchedEffect(phone) { onEvent(OptEvent.Load(phone)) }
      OptScreen(state, ::onEvent)
    }
  }
}
