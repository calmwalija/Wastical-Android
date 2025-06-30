package net.techandgraphics.wastemanagement.ui.screen.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.ui.Route
import net.techandgraphics.wastemanagement.ui.activity.MainActivityState
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
import net.techandgraphics.wastemanagement.ui.screen.company.client.location.CompanyClientLocationEvent
import net.techandgraphics.wastemanagement.ui.screen.company.client.location.CompanyClientLocationScreen
import net.techandgraphics.wastemanagement.ui.screen.company.client.location.CompanyClientLocationViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.client.pending.CompanyClientPendingPaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.company.client.pending.CompanyClientPendingPaymentScreen
import net.techandgraphics.wastemanagement.ui.screen.company.client.pending.CompanyClientPendingPaymentViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.client.plan.CompanyClientPlanEvent
import net.techandgraphics.wastemanagement.ui.screen.company.client.plan.CompanyClientPlanScreen
import net.techandgraphics.wastemanagement.ui.screen.company.client.plan.CompanyClientPlanViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.client.profile.CompanyClientProfileEvent
import net.techandgraphics.wastemanagement.ui.screen.company.client.profile.CompanyClientProfileScreen
import net.techandgraphics.wastemanagement.ui.screen.company.client.profile.CompanyClientProfileViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.home.CompanyHomeEvent.Goto
import net.techandgraphics.wastemanagement.ui.screen.company.home.CompanyHomeScreen
import net.techandgraphics.wastemanagement.ui.screen.company.home.CompanyHomeViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.info.CompanyInfoEvent
import net.techandgraphics.wastemanagement.ui.screen.company.info.CompanyInfoScreen
import net.techandgraphics.wastemanagement.ui.screen.company.info.CompanyInfoViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.info.method.CompanyInfoMethodEvent
import net.techandgraphics.wastemanagement.ui.screen.company.info.method.CompanyInfoMethodScreen
import net.techandgraphics.wastemanagement.ui.screen.company.info.method.CompanyInfoMethodViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.info.plan.CompanyInfoPlanEvent
import net.techandgraphics.wastemanagement.ui.screen.company.info.plan.CompanyInfoPlanScreen
import net.techandgraphics.wastemanagement.ui.screen.company.info.plan.CompanyInfoPlanViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.payment.location.CompanyPaymentPerLocationEvent
import net.techandgraphics.wastemanagement.ui.screen.company.payment.location.CompanyPaymentPerLocationScreen
import net.techandgraphics.wastemanagement.ui.screen.company.payment.location.CompanyPaymentPerLocationViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.payment.location.overview.CompanyPaymentLocationOverviewEvent
import net.techandgraphics.wastemanagement.ui.screen.company.payment.location.overview.CompanyPaymentLocationOverviewScreen
import net.techandgraphics.wastemanagement.ui.screen.company.payment.location.overview.CompanyPaymentLocationOverviewViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.payment.pay.CompanyMakePaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.company.payment.pay.CompanyMakePaymentScreen
import net.techandgraphics.wastemanagement.ui.screen.company.payment.pay.CompanyMakePaymentViewModel
import net.techandgraphics.wastemanagement.ui.screen.company.payment.timeline.PaymentTimelineEvent
import net.techandgraphics.wastemanagement.ui.screen.company.payment.timeline.PaymentTimelineScreen
import net.techandgraphics.wastemanagement.ui.screen.company.payment.timeline.PaymentTimelineViewModel
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
        CompanyCreateClientScreen(state, channel) { event ->
          when (event) {
            CompanyCreateClientEvent.Goto.BackHandler -> navController.navigateUp()
            is CompanyCreateClientEvent.Goto.Profile ->
              navController.navigate(Route.Company.Client.Profile(event.id)) {
                popUpTo(Route.Company.Client.Create) { inclusive = true }
              }

            else -> onEvent(event)
          }
        }
      }
    }

    composable<Route.Company.Payment.Verify> {
      with(hiltViewModel<CompanyVerifyPaymentViewModel>()) {
        val state = state.collectAsState().value
        val ofType = it.toRoute<Route.Company.Payment.Verify>().ofType
        LaunchedEffect(ofType) { onEvent(CompanyVerifyPaymentEvent.Load(ofType)) }

        CompanyVerifyPaymentScreen(state) { event ->
          when (event) {
            CompanyVerifyPaymentEvent.Goto.BackHandler -> navController.navigateUp()
            is CompanyVerifyPaymentEvent.Goto.Profile ->
              navController.navigate(Route.Company.Client.Profile(event.id))

            else -> onEvent(event)
          }
        }
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

                is CompanyBrowseClientListEvent.Goto.Profile -> {
                  onEvent(CompanyBrowseClientListEvent.Button.HistoryTag)
                  navController.navigate(Route.Company.Client.Profile(event.id))
                }
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

            CompanyClientProfileEvent.Option.Location ->
              navController.navigate(Route.Company.ClientLocation(id))

            CompanyClientProfileEvent.Option.Payment ->
              navController.navigate(Route.Company.Client.Payment(id))

            CompanyClientProfileEvent.Option.Plan ->
              navController.navigate(Route.Company.Client.Plan(id))

            CompanyClientProfileEvent.Option.Pending ->
              navController.navigate(Route.Company.Payment.Pending(id))

            CompanyClientProfileEvent.Option.Revoke -> Unit

            CompanyClientProfileEvent.Button.BackHandler -> navController.navigateUp()

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
        CompanyClientPlanScreen(state, channel) { event ->
          when (event) {
            CompanyClientPlanEvent.Button.BackHandler -> navController.navigateUp()
            else -> onEvent(event)
          }
        }
      }
    }


    composable<Route.Company.Info.This> {
      with(hiltViewModel<CompanyInfoViewModel>()) {
        val state = state.collectAsState().value
        CompanyInfoScreen(state) { event ->
          when (event) {
            CompanyInfoEvent.Button.BackHandler -> navController.navigateUp()
            is CompanyInfoEvent.Goto -> when (event) {
              CompanyInfoEvent.Goto.Edit -> Unit
              CompanyInfoEvent.Goto.Method -> navController.navigate(Route.Company.Info.Method)
              CompanyInfoEvent.Goto.Plan -> navController.navigate(Route.Company.Info.Plan)
            }

            else -> Unit
          }
        }
      }
    }


    composable<Route.Company.Info.Plan> {
      with(hiltViewModel<CompanyInfoPlanViewModel>()) {
        val state = state.collectAsState().value
        CompanyInfoPlanScreen(state) { event ->
          when (event) {
            CompanyInfoPlanEvent.Button.BackHandler -> navController.navigateUp()
            CompanyInfoPlanEvent.Button.Plan -> Unit
          }
        }
      }
    }

    composable<Route.Company.Info.Method> {
      with(hiltViewModel<CompanyInfoMethodViewModel>()) {
        val state = state.collectAsState().value
        CompanyInfoMethodScreen(state) { event ->
          when (event) {
            CompanyInfoMethodEvent.Button.BackHandler -> navController.navigateUp()
            CompanyInfoMethodEvent.Button.Plan -> Unit
            else -> Unit
          }
        }
      }
    }

    composable<Route.Company.Home> {
      with(hiltViewModel<CompanyHomeViewModel>()) {
        val state = state.collectAsState().value
        CompanyHomeScreen(state, channel) { event ->
          when (event) {
            is Goto -> when (event) {
              Goto.Create -> navController.navigate(Route.Company.Client.Create)
              Goto.Clients -> navController.navigate(Route.Company.Client.Browse)
              Goto.Payments ->
                navController.navigate(Route.Company.Payment.Verify(PaymentStatus.Approved.name))

              Goto.Company -> navController.navigate(Route.Company.Info.This)
              Goto.PerLocation -> navController.navigate(Route.Company.PerLocation)
              Goto.VerifyPayment ->
                navController.navigate(Route.Company.Payment.Verify(PaymentStatus.Waiting.name))

              is Goto.LocationOverview ->
                navController.navigate(Route.Company.LocationOverview(event.id))

              Goto.Timeline -> navController.navigate(Route.Company.Payment.Timeline)
            }

            else -> onEvent(event)
          }
        }
      }
    }


    composable<Route.Company.PerLocation> {
      with(hiltViewModel<CompanyPaymentPerLocationViewModel>()) {
        val state = state.collectAsState().value
        CompanyPaymentPerLocationScreen(state) { event ->
          when (event) {
            CompanyPaymentPerLocationEvent.Button.BackHandler -> navController.navigateUp()
            CompanyPaymentPerLocationEvent.Load -> Unit
            is CompanyPaymentPerLocationEvent.Goto.LocationOverview ->
              navController.navigate(Route.Company.LocationOverview(event.id))

            else -> onEvent(event)
          }
        }
      }
    }


    composable<Route.Company.LocationOverview> {
      with(hiltViewModel<CompanyPaymentLocationOverviewViewModel>()) {
        val id = it.toRoute<Route.Company.LocationOverview>().id
        val state = state.collectAsState().value
        LaunchedEffect(id) { onEvent(CompanyPaymentLocationOverviewEvent.Load(id)) }
        CompanyPaymentLocationOverviewScreen(state) { event ->
          when (event) {
            CompanyPaymentLocationOverviewEvent.Button.BackHandler -> navController.navigateUp()
            is CompanyPaymentLocationOverviewEvent.Goto.Profile ->
              navController.navigate(Route.Company.Client.Profile(event.id))

            is CompanyPaymentLocationOverviewEvent.Button.SortBy -> onEvent(event)
            else -> Unit
          }
        }
      }
    }


    composable<Route.Company.Payment.Pending> {
      with(hiltViewModel<CompanyClientPendingPaymentViewModel>()) {
        val state = state.collectAsState().value
        val id = it.toRoute<Route.Company.Payment.Pending>().id
        LaunchedEffect(id) { onEvent(CompanyClientPendingPaymentEvent.Load(id)) }
        CompanyClientPendingPaymentScreen(state) { event ->
          when (event) {
            CompanyClientPendingPaymentEvent.Goto.BackHandler -> navController.navigateUp()

            else -> onEvent(event)
          }
        }
      }
    }


    composable<Route.Company.ClientLocation> {
      with(hiltViewModel<CompanyClientLocationViewModel>()) {
        val state = state.collectAsState().value
        val id = it.toRoute<Route.Company.ClientLocation>().id
        LaunchedEffect(id) { onEvent(CompanyClientLocationEvent.Load(id)) }
        CompanyClientLocationScreen(state) { event ->
          when (event) {
            CompanyClientLocationEvent.Goto.BackHandler -> navController.navigateUp()

            else -> onEvent(event)
          }
        }
      }
    }


    composable<Route.Company.Payment.Timeline> {
      with(hiltViewModel<PaymentTimelineViewModel>()) {
        val state = state.collectAsState().value
        PaymentTimelineScreen(state) { event ->
          when (event) {
            PaymentTimelineEvent.Goto.BackHandler -> navController.navigateUp()
            PaymentTimelineEvent.Load -> Unit
            is PaymentTimelineEvent.Goto.Profile ->
              navController.navigate(Route.Company.Client.Profile(event.id))
          }
        }
      }
    }


  }
}
