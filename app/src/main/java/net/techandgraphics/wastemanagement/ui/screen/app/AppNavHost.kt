package net.techandgraphics.wastemanagement.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastemanagement.ui.Route
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState
import net.techandgraphics.wastemanagement.ui.screen.auth.signin.SignInEvent
import net.techandgraphics.wastemanagement.ui.screen.auth.signin.SignInScreen
import net.techandgraphics.wastemanagement.ui.screen.auth.signin.SignInViewModel
import net.techandgraphics.wastemanagement.ui.screen.client.home.ClientHomeEvent
import net.techandgraphics.wastemanagement.ui.screen.client.home.HomeScreen
import net.techandgraphics.wastemanagement.ui.screen.client.home.ClientHomeViewModel
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.ClientInvoiceEvent
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.ClientInvoiceScreen
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.ClientInvoiceViewModel
import net.techandgraphics.wastemanagement.ui.screen.client.payment.ClientPaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.client.payment.ClientPaymentResponseScreen
import net.techandgraphics.wastemanagement.ui.screen.client.payment.ClientPaymentScreen
import net.techandgraphics.wastemanagement.ui.screen.client.payment.ClientPaymentViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.client.create.CompanyCreateClientEvent
import net.techandgraphics.wastemanagement.ui.screen.company.client.create.CompanyCreateClientScreen
import net.techandgraphics.wastemanagement.ui.screen.company.client.create.CompanyCreateClientViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentScreen
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentViewModel

@Composable
fun AppNavHost(
  navController: NavHostController,
  appState: MainActivityState
) {
  NavHost(
    navController = navController,
    startDestination = Route.Client.Home
  ) {

    composable<Route.SignIn> {
      with(hiltViewModel<SignInViewModel>()) {
        val state = state.collectAsState().value
        SignInScreen(state, channel) { event ->
          when (event) {
            is SignInEvent.GoTo -> when (event) {
              SignInEvent.GoTo.Main -> Unit
            }

            else -> onEvent(event)
          }
        }
      }
    }

    composable<Route.Client.Payment> {
      with(hiltViewModel<ClientPaymentViewModel>()) {
        val state = state.collectAsState().value
        onEvent(ClientPaymentEvent.AppState(appState))
        ClientPaymentScreen(state, channel) { event ->
          when (event) {
            is ClientPaymentEvent.Response ->
              navController.navigate(Route.Client.PaymentResponse(event.isSuccess, event.error)) {
                popUpTo(Route.Client.Payment) { inclusive = true }
              }

            ClientPaymentEvent.GoTo.BackHandler -> navController.navigateUp()
            else -> onEvent(event)
          }
        }
      }
    }

    composable<Route.Client.Home> {
      with(hiltViewModel<ClientHomeViewModel>()) {
        val state = state.collectAsState().value
        onEvent(ClientHomeEvent.AppState(appState))
        HomeScreen(state, channel) { event ->
          when (event) {
            is ClientHomeEvent.Goto ->
              when (event) {
                ClientHomeEvent.Goto.Invoice -> navController.navigate(Route.Client.Invoice)
              }

            ClientHomeEvent.Button.MakePayment -> navController.navigate(Route.Client.Payment)

            else -> onEvent(event)
          }
        }
      }
    }

    composable<Route.Client.PaymentResponse> {
      val isSuccess = it.toRoute<Route.Client.PaymentResponse>().isSuccess
      val error = it.toRoute<Route.Client.PaymentResponse>().error
      ClientPaymentResponseScreen(isSuccess = isSuccess, error) { navController.navigateUp() }
    }

    composable<Route.Client.Invoice> {
      with(hiltViewModel<ClientInvoiceViewModel>()) {
        val state = state.collectAsState().value
        onEvent(ClientInvoiceEvent.AppState(appState))
        ClientInvoiceScreen(state, channel) { event ->
          when (event) {
            is ClientInvoiceEvent.GoTo ->
              when (event) {
                ClientInvoiceEvent.GoTo.BackHandler -> navController.navigateUp()
              }

            else -> onEvent(event)
          }
        }
      }
    }


    composable<Route.Company.Account.Create> {
      with(hiltViewModel<CompanyCreateClientViewModel>()) {
        val state = state.collectAsState().value
        LaunchedEffect(appState) { onEvent(CompanyCreateClientEvent.AppState(appState)) }
        CompanyCreateClientScreen(state, channel, ::onEvent)
      }

    }
    composable<Route.Company.Payment> {
      with(hiltViewModel<CompanyVerifyPaymentViewModel>()) {
        val state = state.collectAsState().value
        LaunchedEffect(appState) { onEvent(CompanyVerifyPaymentEvent.AppState(appState)) }
        CompanyVerifyPaymentScreen(state, channel, ::onEvent)
      }
    }


  }
}
