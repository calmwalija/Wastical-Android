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
import net.techandgraphics.wastical.data.local.database.account.ActiveAccountItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.CoverageRaw
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.dashboard.payment.OverpaymentItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.UnPaidAccount
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.getAccountTitle
import net.techandgraphics.wastical.getToday
import net.techandgraphics.wastical.lastDayOfMonth
import net.techandgraphics.wastical.preview
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toMonthName
import net.techandgraphics.wastical.toShortMonthName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.client.invoice.light
import net.techandgraphics.wastical.ui.screen.company.report.BaseExportKlass.Companion.PDF_TEXT_SIZE
import java.io.File
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel class CompanyReportViewModel @Inject constructor(
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
    textSize = PDF_TEXT_SIZE
    typeface = light(application)
  }

  private val createdAtWidth = paint.measureText(ZonedDateTime.now().defaultDateTime()).padding()
  private val contactWidth = paint.measureText("9912345678").padding()
  private val amountWidth = paint.measureText(100_000.toAmount()).padding()

  init {
    onEvent(CompanyReportEvent.Load)
    deleteCachedPdfs()
  }

  private fun deleteCachedPdfs() = viewModelScope.launch(Dispatchers.IO) {
    application.filesDir.listFiles()
      ?.filter { file -> file.isFile && file.name.endsWith(".pdf", ignoreCase = true) }
      ?.forEach { file ->
        runCatching { file.delete() }.onFailure { it.printStackTrace() }
      }
  }

  private fun Float.padding() = plus(24f)
  private fun String.mills() = this + " - " + System.currentTimeMillis().toString().drop(5)

  private fun toFullname(title: String, name: String) =
    title.getAccountTitle().plus(" ${name.trim()}")

  private fun String.toContact() = if (isDigitsOnly()) this else ""
  private fun ZonedDateTime.toDate() = month.value.toMonthName().plus(" ${this.year}")

  private fun onLoad() = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val accounts = database.accountDao.query().map { it.toAccountUiModel() }
    val demographics = database.accountIndicatorDao.qDemographics()

    val monthAccountsCreated: List<MonthYear> =
      database.accountIndicatorDao.qMonthsCreated().map { it.toZonedDateTime().toLocalDate() }
        .map { MonthYear(it.month.value, it.year) }.toSet().sortedWith(
          compareBy<MonthYear> { it.year }.thenBy { it.month },
        )

    val allMonthPayments: List<MonthYear> = database.paymentIndicatorDao.getAllMonthsPayments()
      .sortedWith(
        compareBy<MonthYear> { it.year }.thenBy { it.month },
      )

    fullNameWidth =
      accounts.maxOfOrNull { paint.measureText(it.title.title.plus(it.lastname)) }?.padding()
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
        _state.value = (_state.value as CompanyReportState.Success).copy(filters = updatedFilters)
      }
    }

  private fun onReportOverpayment() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)

      val dataset = database.paymentIndicatorDao.qOverpayment()
      val zonedDateTime = ZonedDateTime.now()

      val createdAtWidth = paint.measureText(zonedDateTime.defaultDate()).padding()
      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(
        countWidth,
        createdAtWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
      )
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())

      val columnWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        createdAtWidth,
        amountWidth,
        locationWidth,
      )

      val filename = "OverPayment Report - ${zonedDateTime.toDate()}"

      BaseExportKlass<OverpaymentItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf(
          "#",
          "Name",
          "Contact",
          "Due",
          "Amount",
          "Location",
        ),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            item.account.toAccountUiModel().toFullName(),
            item.account.username.toContact(),
            item.maxMonth.toShortMonthName().plus(" ${item.maxYear}"),
            item.overpayment.toAmount(),
            item.demographicStreet,
          )
        },
        onEvent = ::onEventPdf,
      )
    }
  }

  private fun onReportNewClient() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val months = state.filters.sortedWith(compareBy<MonthYear> { it.year }.thenBy { it.month })
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
        dataset.maxOfOrNull { item -> paint.measureText(item.demographicArea) }?.padding() ?: 120f
      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
        demographicAreaWidth,
      )
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
        demographicAreaWidth,
        locationWidth,
      )

      val filename = "New Clients Report - ${startMonthDate.toDate()}"

      BaseExportKlass<ActiveAccountItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf(
          "#",
          "Name",
          "Contact",
          "Amount",
          "Area",
          "Location",
        ),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { account ->
          listOf(
            toFullname(account.title, account.lastname),
            account.username.toContact(),
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
        dataset.maxOfOrNull { item -> paint.measureText(item.demographicArea) }?.padding() ?: 120f
      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
        demographicAreaWidth,
      )
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
        demographicAreaWidth,
        locationWidth,
      )

      val filename = "Active Clients Report - ${ZonedDateTime.now().toDate()}"

      BaseExportKlass<ActiveAccountItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf(
          "#",
          "Name",
          "Phone",
          "Amount",
          "Area",
          "Location",
        ),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { account ->
          listOf(
            account.title.getAccountTitle() + account.lastname.trim(),
            if (account.username.isDigitsOnly()) account.username.as9DigitContact() else "",
            account.fee.toAmount(),
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

      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(countWidth, fullNameWidth, contactWidth, amountWidth, 80f, 20f, 20f)
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths =
        listOf(countWidth, fullNameWidth, contactWidth, amountWidth, locationWidth, 80f, 20f, 20f)

      val filename = "Missed Payment Report - ${state.filters.first().toZonedDateTime().toDate()}"

      BaseExportKlass<UnPaidAccount>(application).toPdf(
        company = state.company,
        columnHeaders = listOf("#", "Name", "Contact", "Amount", "Location", "", "", ""),
        columnWidths = columnWidths,
        filename = filename.mills(),
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

      val createdAtWidth = paint.measureText(ZonedDateTime.now().defaultDate()).padding()
      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(countWidth, fullNameWidth, createdAtWidth, contactWidth, amountWidth)
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths =
        listOf(countWidth, fullNameWidth, contactWidth, amountWidth, createdAtWidth, locationWidth)

      val filename = "Paid Payment Report - ${state.filters.first().toZonedDateTime().toDate()}"

      BaseExportKlass<UnPaidAccount>(application).toPdf(
        company = state.company,
        columnHeaders = listOf("#", "Name", "Phone", "Amount", "Paid", "Location"),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { account ->
          listOf(
            toFullname(account.title, account.lastname),
            account.contact.toContact(),
            account.amount.toAmount(),
            account.paidOn.toZonedDateTime().defaultDate(),
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
    return rawList.groupBy { it.accountId }.map { (_, rows) ->
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

  private fun onReportLocationBased() = viewModelScope.launch(Dispatchers.IO) {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)

      val areas = state.demographicFilters.map { it.theAreaId }.distinct()
      val streets = state.demographicFilters.map { it.theStreetId }.distinct()

      val dataset = database.accountIndicatorDao.qLocationBased(areas, streets)

      val demographicAreaWidth =
        dataset.maxOfOrNull { item -> paint.measureText(item.demographicArea) }?.padding() ?: 120f
      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
        demographicAreaWidth,
      )
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
        demographicAreaWidth,
        locationWidth,
      )

      val filename = "Location Based Clients Report - ${ZonedDateTime.now().toDate()}"

      BaseExportKlass<ActiveAccountItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf(
          "#",
          "Name",
          "Phone",
          "Amount",
          "Area",
          "Location",
        ),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { account ->
          listOf(
            toFullname(account.title, account.lastname),
            account.username.toContact(),
            account.fee.toAmount(),
            account.demographicArea,
            account.demographicStreet,
          )
        },
        onEvent = ::onEventPdf,
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
      BaseExportKlass<PaymentCoverageRow>(application).toCoveragePdf(
        company = state.company,
        pdfHeaders = listOf("#", "Name", "Phone"),
        pdfWidths = listOf(40f, 160f, 100f),
        filename = "Payment Coverage",
        onEvent = { file -> file?.preview(application) },
        items = theData,
        months = months,
      )
    }
  }

  private fun onLocationDialogPickMonth(event: CompanyReportEvent.Button.LocationDialog.Pick) =
    viewModelScope.launch {
      if (_state.value is CompanyReportState.Success) {
        val state = (_state.value as CompanyReportState.Success)
        val updatedFilters = state.demographicFilters.toMutableSet().apply {
          if (contains(event.item)) remove(event.item) else add(event.item)
        }
        _state.value =
          (_state.value as CompanyReportState.Success).copy(demographicFilters = updatedFilters)
      }
    }

  private fun onButtonReport(event: CompanyReportEvent.Button.Report) {
    when (event) {
      CompanyReportEvent.Button.Report.ActiveClient -> onReportActiveClient()
      CompanyReportEvent.Button.Report.MissedPayment -> onReportMissedPayment()
      CompanyReportEvent.Button.Report.PaidPayment -> onReportPaidPayment()
      CompanyReportEvent.Button.Report.NewClient -> onReportNewClient()

      CompanyReportEvent.Button.Report.Overpayment -> onReportOverpayment()
      CompanyReportEvent.Button.Report.PaymentCoverage -> Unit
      CompanyReportEvent.Button.Report.LocationBased -> onReportLocationBased()
      CompanyReportEvent.Button.Report.ClientDisengagement -> Unit
    }
  }

  fun onEvent(event: CompanyReportEvent) {
    when (event) {
      is CompanyReportEvent.Button.Report -> onButtonReport(event)
      CompanyReportEvent.Load -> onLoad()
      is CompanyReportEvent.Button.MonthDialog.PickMonth -> onMonthDialogPickMonth(event)
      is CompanyReportEvent.Button.LocationDialog.Pick -> onLocationDialogPickMonth(event)
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
