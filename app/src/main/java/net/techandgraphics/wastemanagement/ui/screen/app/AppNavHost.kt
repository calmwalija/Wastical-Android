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
import net.techandgraphics.wastemanagement.ui.screen.client.home.HomeEvent
import net.techandgraphics.wastemanagement.ui.screen.client.home.HomeScreen
import net.techandgraphics.wastemanagement.ui.screen.client.home.HomeViewModel
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.InvoiceEvent
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.InvoiceScreen
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.InvoiceViewModel
import net.techandgraphics.wastemanagement.ui.screen.client.payment.PaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.client.payment.PaymentResponseScreen
import net.techandgraphics.wastemanagement.ui.screen.client.payment.PaymentScreen
import net.techandgraphics.wastemanagement.ui.screen.client.payment.PaymentViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.account.create.CreateAccountEvent
import net.techandgraphics.wastemanagement.ui.screen.company.account.create.CreateAccountScreen
import net.techandgraphics.wastemanagement.ui.screen.company.account.create.CreateAccountViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.payment.CompanyPaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.company.payment.CompanyPaymentScreen
import net.techandgraphics.wastemanagement.ui.screen.company.payment.CompanyPaymentViewModel

@Composable
fun AppNavHost(
  navController: NavHostController,
  appState: MainActivityState
) {
  NavHost(
    navController = navController,
    startDestination = Route.Company.Payment
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

    composable<Route.Payment> {
      with(hiltViewModel<PaymentViewModel>()) {
        val state = state.collectAsState().value
        onEvent(PaymentEvent.AppState(appState))
        PaymentScreen(state, channel) { event ->
          when (event) {
            is PaymentEvent.Response ->
              navController.navigate(Route.PaymentResponse(event.isSuccess, event.error)) {
                popUpTo(Route.Payment) { inclusive = true }
              }

            PaymentEvent.GoTo.BackHandler -> navController.navigateUp()
            else -> onEvent(event)
          }
        }
      }
    }

    composable<Route.Home> {
      with(hiltViewModel<HomeViewModel>()) {
        val state = state.collectAsState().value
        onEvent(HomeEvent.AppState(appState))
        HomeScreen(state, channel) { event ->
          when (event) {
            is HomeEvent.Goto ->
              when (event) {
                HomeEvent.Goto.Invoice -> navController.navigate(Route.Invoice)
              }

            HomeEvent.Button.MakePayment -> navController.navigate(Route.Payment)

            else -> onEvent(event)
          }
        }
      }
    }

    composable<Route.PaymentResponse> {
      val isSuccess = it.toRoute<Route.PaymentResponse>().isSuccess
      val error = it.toRoute<Route.PaymentResponse>().error
      PaymentResponseScreen(isSuccess = isSuccess, error) { navController.navigateUp() }
    }

    composable<Route.Invoice> {
      with(hiltViewModel<InvoiceViewModel>()) {
        val state = state.collectAsState().value
        onEvent(InvoiceEvent.AppState(appState))
        InvoiceScreen(state, channel) { event ->
          when (event) {
            is InvoiceEvent.GoTo ->
              when (event) {
                InvoiceEvent.GoTo.BackHandler -> navController.navigateUp()
              }

            else -> onEvent(event)
          }
        }
      }
    }


    composable<Route.Company.Account.Create> {
      with(hiltViewModel<CreateAccountViewModel>()) {
        val state = state.collectAsState().value
        LaunchedEffect(appState) { onEvent(CreateAccountEvent.AppState(appState)) }
        CreateAccountScreen(state, channel, ::onEvent)
      }

    }
    composable<Route.Company.Payment> {
      with(hiltViewModel<CompanyPaymentViewModel>()) {
        val state = state.collectAsState().value
        LaunchedEffect(appState) { onEvent(CompanyPaymentEvent.AppState(appState)) }
        CompanyPaymentScreen(state, channel, ::onEvent)
      }
    }


  }
}
