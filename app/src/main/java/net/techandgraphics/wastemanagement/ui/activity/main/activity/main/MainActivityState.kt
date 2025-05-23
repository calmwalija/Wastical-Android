package net.techandgraphics.wastemanagement.ui.activity.main.activity.main

import coil.ImageLoader
import net.techandgraphics.wastemanagement.domain.model.account.AccountContactUiModel
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

data class MainActivityState(
  val imageLoader: ImageLoader? = null,
  val accounts: List<AccountUiModel> = listOf(),
  val accountContacts: List<AccountContactUiModel> = listOf(),
  val companies: List<CompanyUiModel> = listOf(),
  val companyContacts: List<CompanyContactUiModel> = listOf(),
  val payments: List<PaymentUiModel> = listOf(),
  val invoices: List<PaymentUiModel> = listOf(),
  val paymentPlans: List<PaymentPlanUiModel> = listOf(),
  val paymentMethods: List<PaymentMethodUiModel> = listOf(),
)
