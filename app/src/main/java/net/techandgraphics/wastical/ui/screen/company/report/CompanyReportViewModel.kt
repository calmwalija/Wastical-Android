package net.techandgraphics.wastical.ui.screen.company.report

import android.app.Application
import android.graphics.Paint
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
import java.io.File
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompanyReportViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyReportState>(CompanyReportState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<CompanyReportChannel>()
  val channel = _channel.receiveAsFlow()

  private val pdfMaxWidth = 595f - 18f - 48f
  private var fullNameWidth: Float = -1f

  private val paint = Paint().apply {
    textSize = 7f
    typeface = light(application)
  }

  private val createdAtWidth = paint.measureText(ZonedDateTime.now().defaultDateTime()).padding()
  private val contactWidth = paint.measureText("9912345678").padding()
  private val amountWidth = paint.measureText(100_000.toAmount()).padding()

  init {
    onEvent(CompanyReportEvent.Load)
  }

  private fun Float.padding() = plus(24)

  private fun toFullname(title: String, name: String) =
    title.getAccountTitle().plus(" ${name.trim()}")

  private fun String.toContact() = if (isDigitsOnly()) this else ""

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

    fullNameWidth = accounts.maxOfOrNull { paint.measureText(it.toFullName()) }
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

  private fun onReportNewClient() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val months = state.filters
        .sortedWith(compareBy<MonthYear> { it.year }.thenBy { it.month })
      val startMonthDate = months.first().toZonedDateTime()
      var lastDayOfMonth = startMonthDate.lastDayOfMonth()
      if (months.size > 1) {
        val endMonthDate = months.last().toZonedDateTime()
        lastDayOfMonth = endMonthDate.lastDayOfMonth()
      }
      val start = startMonthDate.toEpochSecond()
      val end = lastDayOfMonth.toEpochSecond()

      val dataset = database.accountIndicatorDao.qRange(start = start, end = end)

      val demographicAreaWidth =
        dataset.maxOfOrNull { item -> paint.measureText(item.demographicArea) }
          ?.padding() ?: 120f
      val createdAtWidth = paint.measureText(ZonedDateTime.now().defaultDate()).padding()
      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths =
        listOf(
          countWidth,
          fullNameWidth,
          contactWidth,
          amountWidth,
          demographicAreaWidth,
          createdAtWidth,
        )
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths =
        listOf(
          countWidth,
          fullNameWidth,
          contactWidth,
          amountWidth,
          createdAtWidth,
          demographicAreaWidth,
          locationWidth,
        )

      val filename = "New Clients Report - ${startMonthDate.defaultDate()}"

      BaseExportKlass<AccountExport>(application)
        .toPdf(
          company = state.company,
          columnHeaders = listOf(
            "#",
            "Full Name",
            "Phone",
            "Amount",
            "Created",
            "Area",
            "Location",
          ),
          columnWidths = columnWidths,
          filename = filename,
          pageTitle = filename,
          items = dataset,
          valueExtractor = { account ->
            listOf(
              account.title.getAccountTitle() + account.lastname.trim(),
              account.createdAt.toZonedDateTime().defaultDate(),
              if (account.username.isDigitsOnly()) account.username.as9DigitContact() else "",
              account.fee.toAmount(),
              account.demographicArea,
              account.demographicStreet,
            )
          },
          onEvent = ::onEventPdf,
        )
      _state.value = (_state.value as CompanyReportState.Success).copy(filters = emptySet())
    }
  }

  private fun onReportActiveClient() = viewModelScope.launch(Dispatchers.IO) {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)

      val dataset = database.accountIndicatorDao.qActiveAccounts()
      val demographicAreaWidth =
        dataset.maxOfOrNull { item -> paint.measureText(item.demographicArea) }
          ?.padding() ?: 120f
      val createdAtWidth = paint.measureText(ZonedDateTime.now().defaultDate()).padding()
      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths =
        listOf(
          countWidth,
          fullNameWidth,
          contactWidth,
          amountWidth,
          demographicAreaWidth,
          createdAtWidth,
        )
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths =
        listOf(
          countWidth,
          fullNameWidth,
          contactWidth,
          amountWidth,
          createdAtWidth,
          demographicAreaWidth,
          locationWidth,
        )

      val filename = "Active Clients Report - ${ZonedDateTime.now().defaultDate()}"

      BaseExportKlass<AccountExport>(application)
        .toPdf(
          company = state.company,
          columnHeaders = listOf(
            "#",
            "Full Name",
            "Phone",
            "Amount",
            "Created",
            "Area",
            "Location",
          ),
          columnWidths = columnWidths,
          filename = filename,
          pageTitle = filename,
          items = dataset,
          valueExtractor = { account ->
            listOf(
              account.title.getAccountTitle() + account.lastname.trim(),
              if (account.username.isDigitsOnly()) account.username.as9DigitContact() else "",
              account.fee.toAmount(),
              account.createdAt.toZonedDateTime().defaultDate(),
              account.demographicArea,
              account.demographicStreet,
            )
          },
          onEvent = ::onEventPdf,
        )
    }
  }

  private fun onEventPdf(file: File?) = viewModelScope.launch {
    file?.let { newFile ->
      newFile.preview(application)
      _channel.send(CompanyReportChannel.Pdf.Success)
    } ?: _channel.send(CompanyReportChannel.Pdf.Error)
  }

  private fun onReportMissedPayment() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)

      val dataset = database.paymentIndicatorDao.qRange(
        months = state.filters.map { it.month },
        years = state.filters.map { it.year }.distinct(),
        hasPaid = false,
      )

      println("🔥🔥🔥🔥🔥🔥🔥🔥🔥 $dataset")
      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(countWidth, fullNameWidth, contactWidth, amountWidth, 60f, 20f, 20f)
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths =
        listOf(countWidth, fullNameWidth, contactWidth, amountWidth, locationWidth, 60f, 20f, 20f)

      val filename = "Missed Payment Report - ${state.filters.first().month.toMonthName()}"

      BaseExportKlass<UnPaidAccount>(application)
        .toPdf(
          company = state.company,
          columnHeaders = listOf("#", "Full Name", "Contact", "Amount", "Location", "", "", ""),
          columnWidths = columnWidths,
          filename = filename,
          pageTitle = filename,
          items = dataset,
          valueExtractor = { account ->
            listOf(
              toFullname(account.title, account.lastname),
              account.contact.toContact(),
              account.amount.toAmount(),
              account.demographicStreet,
              "",
              "",
              "",
            )
          },
          onEvent = ::onEventPdf,
        )
    }
  }

  private fun onReportPaidPayment() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)

      val dataset = database.paymentIndicatorDao.qRange(
        months = state.filters.map { it.month },
        years = state.filters.map { it.year }.distinct(),
        hasPaid = true,
      )

      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(countWidth, fullNameWidth, createdAtWidth, contactWidth, amountWidth)
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths =
        listOf(countWidth, fullNameWidth, contactWidth, amountWidth, createdAtWidth, locationWidth)

      val filename = "Paid Payment Report - ${state.filters.first().month.toMonthName()}"

      BaseExportKlass<UnPaidAccount>(application)
        .toPdf(
          company = state.company,
          columnHeaders = listOf("#", "Full Name", "Phone", "Amount", "Paid", "Location"),
          columnWidths = columnWidths,
          filename = filename,
          pageTitle = filename,
          items = dataset,
          valueExtractor = { account ->
            listOf(
              toFullname(account.title, account.lastname),
              account.contact.toContact(),
              account.amount.toAmount(),
              account.paidOn.toZonedDateTime().defaultDateTime(),
              account.demographicStreet,
            )
          },
          onEvent = ::onEventPdf,
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

  private fun onButtonReport(event: CompanyReportEvent.Button.Report) {
    when (event) {
      CompanyReportEvent.Button.Report.ActiveClient -> onReportActiveClient()
      CompanyReportEvent.Button.Report.MissedPayment -> onReportMissedPayment()
      CompanyReportEvent.Button.Report.PaidPayment -> onReportPaidPayment()
      CompanyReportEvent.Button.Report.NewClient -> onReportNewClient()

      CompanyReportEvent.Button.Report.Overpayment -> Unit
      CompanyReportEvent.Button.Report.PaymentCoverage -> Unit
      CompanyReportEvent.Button.Report.LocationBased -> Unit
      CompanyReportEvent.Button.Report.ClientDisengagement -> Unit
    }
  }

  fun onEvent(event: CompanyReportEvent) {
    when (event) {
      is CompanyReportEvent.Button.Report -> onButtonReport(event)
      CompanyReportEvent.Load -> onLoad()
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
