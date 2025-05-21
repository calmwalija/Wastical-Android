package net.techandgraphics.wastemanagement.ui.screen.home

import coil.ImageLoader
import net.techandgraphics.wastemanagement.domain.model.account.AccountContactUiModel
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

data class HomeState(
  val searchQuery: String = "",
  val imageLoader: ImageLoader? = null,
  val accounts: List<AccountUiModel> = listOf(),
  val accountContacts: List<AccountContactUiModel> = listOf(),
  val company: List<CompanyUiModel> = listOf(),
  val companyContacts: List<CompanyContactUiModel> = listOf(),
  val payments: List<PaymentUiModel> = listOf(),
  val paymentPlans: List<PaymentPlanUiModel> = listOf(),
)
