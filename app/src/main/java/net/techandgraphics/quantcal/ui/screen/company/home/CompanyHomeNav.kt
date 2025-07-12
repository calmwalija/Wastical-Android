@file:Suppress("FunctionName")

package net.techandgraphics.quantcal.ui.screen.company.home

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.ui.screen.company.CompanyRoute
import net.techandgraphics.quantcal.ui.screen.company.home.CompanyHomeEvent.Goto

fun NavGraphBuilder.CompanyHomeNav(navController: NavHostController) {
  composable<CompanyRoute.Home> {
    with(hiltViewModel<CompanyHomeViewModel>()) {
      val state = state.collectAsState().value
      CompanyHomeScreen(state, channel) { event ->
        when (event) {
          is Goto -> when (event) {
            Goto.Create -> navController.navigate(CompanyRoute.ClientCreate)
            Goto.Report -> navController.navigate(CompanyRoute.CompanyReport)
            Goto.Clients -> navController.navigate(CompanyRoute.ClientBrowse)
            Goto.Payments ->
              navController.navigate(CompanyRoute.PaymentVerify(PaymentStatus.Approved.name))

            Goto.Company -> navController.navigate(CompanyRoute.CompanyInfo)
            Goto.PerLocation -> navController.navigate(CompanyRoute.BrowseLocation)
            Goto.VerifyPayment ->
              navController.navigate(CompanyRoute.PaymentVerify(PaymentStatus.Waiting.name))

            is Goto.LocationOverview ->
              navController.navigate(CompanyRoute.LocationOverview(event.id))

            Goto.Timeline -> navController.navigate(CompanyRoute.PaymentTimeline)

            is Goto.Profile -> navController.navigate(CompanyRoute.ClientProfile(event.id))
          }

          else -> onEvent(event)
        }
      }
    }
  }
}
