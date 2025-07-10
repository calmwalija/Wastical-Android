package net.techandgraphics.quantcal.ui.screen.company.report

import android.app.Application
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.account.AccountExport
import net.techandgraphics.quantcal.data.local.database.dashboard.payment.CoverageRaw
import net.techandgraphics.quantcal.data.local.database.dashboard.payment.UnPaidAccount
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toDemographicStreetUiModel
import net.techandgraphics.quantcal.getAccountTitle
import net.techandgraphics.quantcal.getToday
import net.techandgraphics.quantcal.preview
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.toMonthName
import javax.inject.Inject

@HiltViewModel
class CompanyReportViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyReportState>(CompanyReportState.Loading)
  val state = _state.asStateFlow()

  init {
    onEvent(CompanyReportEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val accounts = database.accountDao.query().map { it.toAccountUiModel() }
    val demographics = database.demographicStreetDao.query().map { it.toDemographicStreetUiModel() }
    _state.value = CompanyReportState.Success(
      company = company,
      accounts = accounts,
      demographics = demographics,
    )
  }

  private fun onButtonExportClients() = viewModelScope.launch(Dispatchers.IO) {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      BaseExportKlass<AccountExport>(application)(
        company = state.company,
        pageTitle = "Accounts Report",
        columnHeaders = listOf("#", "Full Name", "Phone", "Amount", "Location"),
        columnWidths = listOf(40f, 160f, 100f, 80f, 130f),
        filename = "accounts_report",
        items = database.accountDao.qAccountExport(),
        valueExtractor = { account ->
          listOf(
            account.title.getAccountTitle() + account.lastname.trim(),
            if (account.username.isDigitsOnly()) account.username else "",
            account.fee.toAmount(),
            account.demographicStreet,
          )
        },
        onEvent = { file -> file?.preview(application) },
      )
    }
  }

  private fun onButtonExportOutstanding() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val (_, month, year) = getToday()
      BaseExportKlass<UnPaidAccount>(application)(
        company = state.company,
        pageTitle = "Outstanding Payment Clients in ${month.toMonthName()} Report",
        columnHeaders = listOf("#", "Full Name", "Phone", "Amount", "Location"),
        columnWidths = listOf(40f, 160f, 100f, 80f, 130f),
        filename = "Outstanding Payment Clients in ${month.toMonthName()}",
        items = database.paymentIndicatorDao.qAccounts(month, year, false),
        valueExtractor = { account ->
          listOf(
            account.title.getAccountTitle().plus(" ${account.lastname.trim()}"),
            if (account.contact.isDigitsOnly()) account.contact else "",
            account.amount.toAmount(),
            account.demographicStreet,
          )
        },
        onEvent = { file -> file?.preview(application) },
      )
    }
  }

  private fun onButtonExportCollected() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val (_, month, year) = getToday()
      BaseExportKlass<UnPaidAccount>(application)(
        company = state.company,
        pageTitle = "Collected Payment Clients in ${month.toMonthName()} Report",
        columnHeaders = listOf("#", "Full Name", "Phone", "Amount", "Location"),
        columnWidths = listOf(40f, 160f, 100f, 80f, 130f),
        filename = "Collected Payment Clients in ${month.toMonthName()}",
        items = database.paymentIndicatorDao.qAccounts(month, year, true),
        valueExtractor = { account ->
          listOf(
            account.title.getAccountTitle().plus(" ${account.lastname.trim()}"),
            if (account.contact.isDigitsOnly()) account.contact else "",
            account.amount.toAmount(),
            account.demographicStreet,
          )
        },
        onEvent = { file -> file?.preview(application) },
      )
    }
  }

  fun mapToCoverageMatrix(
    rawList: List<CoverageRaw>,
    months: List<Int>,
  ): List<PaymentCoverageRow> {
    return rawList
      .groupBy { it.accountId }
      .map { (_, rows) ->
        val first = rows.first()
        val monthsPaid = rows.mapNotNull { it.paidMonth }.toSet()
        val monthMap = months.associateWith { it in monthsPaid }
        PaymentCoverageRow(
          fullName = first.fullName,
          phoneNumber = first.phoneNumber,
          monthStatus = monthMap,
          title = first.title,
        )
      }
  }

  private fun onButtonExportCoverage() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val (_, month, year) = getToday()

      val months = database.paymentMonthCoveredDao.qGroupByMonth().map { it.month }

      val theData: List<PaymentCoverageRow> =
        mapToCoverageMatrix(database.paymentIndicatorDao.getCoverageRaw(year), months)

      exportCoverageToPdf(application, theData, months) { file ->
        file?.preview(application)
      }
    }
  }

  fun onEvent(event: CompanyReportEvent) {
    when (event) {
      CompanyReportEvent.Button.Export.Client -> onButtonExportClients()
      CompanyReportEvent.Button.Export.Outstanding -> onButtonExportOutstanding()
      CompanyReportEvent.Button.Export.Collected -> onButtonExportCollected()

      CompanyReportEvent.Button.Export.Plan -> Unit
      CompanyReportEvent.Button.Export.Coverage -> onButtonExportCoverage()
      CompanyReportEvent.Button.Export.Geographic -> Unit

      CompanyReportEvent.Load -> onLoad()
      else -> Unit
    }
  }
}

data class PaymentCoverageRow(
  val title: String,
  val fullName: String,
  val phoneNumber: String,
  val monthStatus: Map<Int, Boolean>,
)
