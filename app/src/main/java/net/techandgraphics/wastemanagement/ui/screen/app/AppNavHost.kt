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
import net.techandgraphics.wastemanagement.ui.screen.auth.phone.PhoneNavGraphBuilder
import net.techandgraphics.wastemanagement.ui.screen.client.home.ClientHomeEvent
import net.techandgraphics.wastemanagement.ui.screen.client.home.ClientHomeViewModel
import net.techandgraphics.wastemanagement.ui.screen.client.home.HomeScreen
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.ClientInvoiceEvent
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.ClientInvoiceScreen
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.ClientInvoiceViewModel
import net.techandgraphics.wastemanagement.ui.screen.client.payment.ClientPaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.client.payment.ClientPaymentResponseScreen
import net.techandgraphics.wastemanagement.ui.screen.client.payment.ClientPaymentScreen
import net.techandgraphics.wastemanagement.ui.screen.client.payment.ClientPaymentViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.client.browse.CompanyBrowseClientListEvent
import net.techandgraphics.wastemanagement.ui.screen.company.client.browse.CompanyBrowseClientScreen
import net.techandgraphics.wastemanagement.ui.screen.company.client.browse.CompanyBrowseClientViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.client.create.CompanyCreateClientEvent
import net.techandgraphics.wastemanagement.ui.screen.company.client.create.CompanyCreateClientScreen
import net.techandgraphics.wastemanagement.ui.screen.company.client.create.CompanyCreateClientViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.client.history.CompanyClientHistoryEvent
import net.techandgraphics.wastemanagement.ui.screen.company.client.history.CompanyClientHistoryScreen
import net.techandgraphics.wastemanagement.ui.screen.company.client.history.CompanyClientHistoryViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.client.plan.CompanyClientPlanEvent
import net.techandgraphics.wastemanagement.ui.screen.company.client.plan.CompanyClientPlanScreen
import net.techandgraphics.wastemanagement.ui.screen.company.client.plan.CompanyClientPlanViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.client.profile.CompanyClientProfileEvent
import net.techandgraphics.wastemanagement.ui.screen.company.client.profile.CompanyClientProfileScreen
import net.techandgraphics.wastemanagement.ui.screen.company.client.profile.CompanyClientProfileViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.home.CompanyHomeEvent.Goto
import net.techandgraphics.wastemanagement.ui.screen.company.home.CompanyHomeEvent.Load
import net.techandgraphics.wastemanagement.ui.screen.company.home.CompanyHomeScreen
import net.techandgraphics.wastemanagement.ui.screen.company.home.CompanyHomeViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.info.CompanyInfoScreen
import net.techandgraphics.wastemanagement.ui.screen.company.info.CompanyInfoViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.payment.pay.CompanyMakePaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.company.payment.pay.CompanyMakePaymentScreen
import net.techandgraphics.wastemanagement.ui.screen.company.payment.pay.CompanyMakePaymentViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentScreen
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentViewModel

@Composable
fun AppNavHost(
  navController: NavHostController,
  appState: MainActivityState,
) {
  NavHost(
    navController = navController,
    startDestination = Route.Company.Home
  ) {

    PhoneNavGraphBuilder(navController)

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


    composable<Route.Company.Client.Create> {
      with(hiltViewModel<CompanyCreateClientViewModel>()) {
        val state = state.collectAsState().value
        LaunchedEffect(appState) { onEvent(CompanyCreateClientEvent.AppState(appState)) }
        CompanyCreateClientScreen(state, channel, ::onEvent)
      }
    }

    composable<Route.Company.Payment.Verify> {
      with(hiltViewModel<CompanyVerifyPaymentViewModel>()) {
        val state = state.collectAsState().value
        LaunchedEffect(appState) { onEvent(CompanyVerifyPaymentEvent.AppState(appState)) }
        CompanyVerifyPaymentScreen(state, channel, ::onEvent)
      }
    }

    composable<Route.Company.Client.Browse> {
      with(hiltViewModel<CompanyBrowseClientViewModel>()) {
        val state = state.collectAsState().value
        CompanyBrowseClientScreen(state, channel) { event ->
          when (event) {
            is CompanyBrowseClientListEvent.Goto ->
              when (event) {
                CompanyBrowseClientListEvent.Goto.BackHandler -> navController.navigateUp()
                CompanyBrowseClientListEvent.Goto.Create ->
                  navController.navigate(Route.Company.Client.Create)

                is CompanyBrowseClientListEvent.Goto.Profile ->
                  navController.navigate(Route.Company.Client.Profile(event.id))
              }

            else -> onEvent(event)
          }

        }
      }
    }

    composable<Route.Company.Client.Profile> {
      with(hiltViewModel<CompanyClientProfileViewModel>()) {
        val id = it.toRoute<Route.Company.Client.Profile>().id
        val state = state.collectAsState().value
        LaunchedEffect(id) { onEvent(CompanyClientProfileEvent.Load(id)) }
        CompanyClientProfileScreen(state) { event ->
          when (event) {

            CompanyClientProfileEvent.Option.History ->
              navController.navigate(Route.Company.Client.History(id))

            CompanyClientProfileEvent.Option.Location -> Unit

            CompanyClientProfileEvent.Option.Payment ->
              navController.navigate(Route.Company.Client.Payment(id))

            CompanyClientProfileEvent.Option.Plan ->
              navController.navigate(Route.Company.Client.Plan(id))

            CompanyClientProfileEvent.Option.Revoke -> Unit

            else -> Unit
          }
        }
      }
    }

    composable<Route.Company.Client.Payment> {
      with(hiltViewModel<CompanyMakePaymentViewModel>()) {
        val id = it.toRoute<Route.Company.Client.Payment>().id
        val state = state.collectAsState().value
        LaunchedEffect(id) { onEvent(CompanyMakePaymentEvent.Load(id)) }
        CompanyMakePaymentScreen(state, channel) { event ->
          when (event) {
            CompanyMakePaymentEvent.GoTo.BackHandler -> navController.navigateUp()
            else -> onEvent(event)
          }
        }
      }
    }


    composable<Route.Company.Client.History> {
      with(hiltViewModel<CompanyClientHistoryViewModel>()) {
        val id = it.toRoute<Route.Company.Client.History>().id
        val state = state.collectAsState().value
        LaunchedEffect(id) { onEvent(CompanyClientHistoryEvent.Load(id, appState)) }
        CompanyClientHistoryScreen(state, ::onEvent)
      }
    }



    composable<Route.Company.Client.Plan> {
      with(hiltViewModel<CompanyClientPlanViewModel>()) {
        val id = it.toRoute<Route.Company.Client.Plan>().id
        val state = state.collectAsState().value
        LaunchedEffect(id) { onEvent(CompanyClientPlanEvent.Load(id)) }
        CompanyClientPlanScreen(state, ::onEvent)
      }
    }


    composable<Route.Company.Info> {
      with(hiltViewModel<CompanyInfoViewModel>()) {
        val state = state.collectAsState().value
        CompanyInfoScreen(state) {}
      }
    }


    composable<Route.Company.Home> {
      with(hiltViewModel<CompanyHomeViewModel>()) {
        LaunchedEffect(appState) { onEvent(Load(appState)) }
        val state = state.collectAsState().value
        CompanyHomeScreen(state) { event ->
          when (event) {
            is Goto -> when (event) {
              Goto.Create -> navController.navigate(Route.Company.Client.Create)
              Goto.Clients -> navController.navigate(Route.Company.Client.Browse)
              Goto.Payments -> navController.navigate(Route.Company.Payment.Verify)
              Goto.Profile -> navController.navigate(Route.Company.Info)
            }

            else -> onEvent(event)
          }

        }
      }
    }

  }
}
