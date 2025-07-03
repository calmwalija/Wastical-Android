package net.techandgraphics.quantcal.ui.activity

import coil.ImageLoader
import net.techandgraphics.quantcal.domain.model.account.AccountContactUiModel
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyContactUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.company.TrashCollectionScheduleUiModel
import net.techandgraphics.quantcal.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.quantcal.domain.model.demographic.DemographicDistrictUiModel
import net.techandgraphics.quantcal.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentAccountUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel

data class MainActivityState(
  val imageLoader: ImageLoader? = null,
  val accounts: List<AccountUiModel> = listOf(),
  val accountContacts: List<AccountContactUiModel> = listOf(),
  val companies: List<CompanyUiModel> = listOf(),
  val companyContacts: List<CompanyContactUiModel> = listOf(),
  val payments: List<PaymentUiModel> = listOf(),
  val invoices: List<PaymentUiModel> = listOf(),
  val paymentAccounts: List<PaymentAccountUiModel> = listOf(),
  val paymentPlans: List<PaymentPlanUiModel> = listOf(),
  val paymentMethods: List<PaymentMethodUiModel> = listOf(),
  val trashSchedules: List<TrashCollectionScheduleUiModel> = listOf(),
  val methods: List<PaymentMethodUiModel> = listOf(),
  val paymentGateways: List<PaymentGatewayUiModel> = listOf(),
  val streets: List<DemographicStreetUiModel> = listOf(),
  val areas: List<DemographicAreaUiModel> = listOf(),
  val districts: List<DemographicDistrictUiModel> = listOf(),
  val screenState: ScreenState = ScreenState.Idle,
)

enum class ScreenState { Load, Empty, Idle }
