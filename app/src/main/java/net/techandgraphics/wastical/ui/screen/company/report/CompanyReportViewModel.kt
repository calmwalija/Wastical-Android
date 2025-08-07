package net.techandgraphics.wastical.ui.screen.company.report

import android.app.Application
import android.graphics.Paint
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.as9DigitContact
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.AccountExport
import net.techandgraphics.wastical.data.local.database.dashboard.payment.CoverageRaw
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.dashboard.payment.UnPaidAccount
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toDemographicStreetUiModel
import net.techandgraphics.wastical.getAccountTitle
import net.techandgraphics.wastical.getToday
import net.techandgraphics.wastical.lastDayOfMonth
import net.techandgraphics.wastical.preview
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toMonthName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.client.invoice.light
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompanyReportViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyReportState>(CompanyReportState.Loading)
  val state = _state.asStateFlow()

  private val pdfMaxWidth = 595f - 18f - 48f
  private var fullNameWidth: Float = -1f

  private val measurePaint = Paint().apply {
    textSize = 7f
    typeface = light(application)
  }

  init {
    onEvent(CompanyReportEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val accounts = database.accountDao.query().map { it.toAccountUiModel() }
    val demographics = database.demographicStreetDao.query().map { it.toDemographicStreetUiModel() }

    val monthAccountsCreated: List<MonthYear> = database.accountIndicatorDao
      .qMonthsCreated()
      .map { it.toZonedDateTime().toLocalDate() }
      .map { MonthYear(it.month.value, it.year) }
      .toSet()
      .sortedWith(
        compareBy<MonthYear> { it.year }
          .thenBy { it.month },
      )

    val allMonthPayments: List<MonthYear> = database.paymentIndicatorDao
      .getAllMonthsPayments()
      .sortedWith(
        compareBy<MonthYear> { it.year }
          .thenBy { it.month },
      )

    fullNameWidth = accounts.maxOfOrNull { measurePaint.measureText(it.toFullName()) }
      ?.plus(24f)
      ?: 120f

    _state.value = CompanyReportState.Success(
      company = company,
      accounts = accounts,
      demographics = demographics,
      allMonthPayments = allMonthPayments,
      monthAccountsCreated = monthAccountsCreated,
    )
  }

  private fun onMonthDialogPickMonth(event: CompanyReportEvent.Button.MonthDialog.PickMonth) =
    viewModelScope.launch {
      if (_state.value is CompanyReportState.Success) {
        val state = (_state.value as CompanyReportState.Success)
        val updatedFilters = state.filters.toMutableSet().apply {
          if (contains(event.monthYear)) remove(event.monthYear) else add(event.monthYear)
        }
        _state.value = (_state.value as CompanyReportState.Success)
          .copy(filters = updatedFilters)
      }
    }

  private fun onButtonExportNewAccount() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val months = state.filters
        .sortedWith(
          compareBy<MonthYear> { it.year }
            .thenBy { it.month },
        )
      val startMonthDate = months.first().toZonedDateTime()
      var lastDayOfMonth = startMonthDate.lastDayOfMonth()
      if (months.size > 1) {
        val endMonthDate = months.last().toZonedDateTime()
        lastDayOfMonth = endMonthDate.lastDayOfMonth()
      }
      val start = startMonthDate.toEpochSecond()
      val end = lastDayOfMonth.toEpochSecond()

      val dataset = database.accountIndicatorDao.qRange(start = start, end = end)

      val pdfWidths = listOf(40f, fullNameWidth, 70f, 60f, 60f)
      val columnWidths =
        listOf(40f, fullNameWidth, 70f, 60f, 60f, pdfMaxWidth.minus(pdfWidths.sum()))

      BaseExportKlass<AccountExport>(application)
        .toPdf(
          company = state.company,
          columnHeaders = listOf("#", "Full Name", "Created", "Phone", "Amount", "Location"),
          columnWidths = columnWidths,
          filename = "New Accounts Report for ${startMonthDate.defaultDate()}",
          pageTitle = "New Accounts Report For ${startMonthDate.defaultDate()}",
          items = dataset,
          valueExtractor = { account ->
            listOf(
              account.title.getAccountTitle() + account.lastname.trim(),
              account.createdAt.toZonedDateTime().defaultDate(),
              if (account.username.isDigitsOnly()) account.username.as9DigitContact() else "",
              account.fee.toAmount(),
              account.demographicStreet,
            )
          },
          onEvent = { file -> file?.preview(application) },
        )
      _state.value = (_state.value as CompanyReportState.Success).copy(filters = emptySet())
    }
  }

  private fun onButtonExportClients() = viewModelScope.launch(Dispatchers.IO) {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)

      val pdfWidths = listOf(40f, fullNameWidth, 70f, 60f, 60f, 20f, 20f)
      val columnWidths =
        listOf(40f, fullNameWidth, 70f, 60f, pdfMaxWidth.minus(pdfWidths.sum()), 60f, 20f, 20f)

      BaseExportKlass<AccountExport>(application)
        .toPdf(
          company = state.company,
          columnHeaders = listOf("#", "Full Name", "Phone", "Amount", "Location", "", "", ""),
          columnWidths = columnWidths,
          filename = "All Accounts Report",
          pageTitle = "All Accounts Report",
          items = database.accountIndicatorDao.qActiveAccounts(),
          valueExtractor = { account ->
            listOf(
              account.title.getAccountTitle() + account.lastname.trim(),
              if (account.username.isDigitsOnly()) account.username.as9DigitContact() else "",
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

      val pdfWidths = listOf(40f, fullNameWidth, 70f, 60f, 60f, 20f, 20f)
      val columnWidths =
        listOf(40f, fullNameWidth, 70f, 60f, pdfMaxWidth.minus(pdfWidths.sum()), 60f, 20f, 20f)

      BaseExportKlass<UnPaidAccount>(application)
        .toPdf(
          company = state.company,
          columnHeaders = listOf("#", "Full Name", "Phone", "Amount", "Location", "", "", ""),
          columnWidths = columnWidths,
          filename = "Outstanding Payment Clients for ${month.toMonthName()}",
          pageTitle = "Outstanding Payment Clients For ${month.toMonthName()}",
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

      val dataset = database.paymentIndicatorDao.qAccounts(month, year, true)
      val paidOnWidth = measurePaint.measureText(ZonedDateTime.now().defaultDateTime()).plus(32)
      val countWidth = measurePaint.measureText(dataset.size.toString()).plus(24)
      val pdfWidths = listOf(countWidth, fullNameWidth, paidOnWidth, 70f, 60f)
      val columnWidths =
        listOf(countWidth, fullNameWidth, 70f, 60f, paidOnWidth, pdfMaxWidth.minus(pdfWidths.sum()))

      BaseExportKlass<UnPaidAccount>(application)
        .toPdf(
          company = state.company,
          columnHeaders = listOf("#", "Full Name", "Phone", "Amount", "Paid On", "Location"),
          columnWidths = columnWidths,
          filename = "Collected Payment Clients in ${month.toMonthName()}",
          pageTitle = "Collected Payment Clients For ${month.toMonthName()}",
          items = dataset,
          valueExtractor = { account ->
            listOf(
              account.title.getAccountTitle().plus(" ${account.lastname.trim()}"),
              if (account.contact.isDigitsOnly()) account.contact else "",
              account.amount.toAmount(),
              account.paidOn.toZonedDateTime().defaultDateTime(),
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
      BaseExportKlass<PaymentCoverageRow>(application)
        .toCoveragePdf(
          company = state.company,
          pdfHeaders = listOf("#", "Full Name", "Phone"),
          pdfWidths = listOf(40f, 160f, 100f),
          filename = "Payment Coverage",
          onEvent = { file -> file?.preview(application) },
          items = theData,
          months = months,
        )
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
      CompanyReportEvent.Button.Export.NewAccount -> onButtonExportNewAccount()
      is CompanyReportEvent.Button.MonthDialog.PickMonth -> onMonthDialogPickMonth(event)
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
