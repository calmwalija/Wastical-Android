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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.Status
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.ReportAccountItem
import net.techandgraphics.wastical.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.wastical.data.local.database.dashboard.payment.AgingRawItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.GatewaySuccessItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.LocationCollectionItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.dashboard.payment.OutstandingBalanceItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.OverpaymentItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.PaymentMethodBreakdownItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.PlanPerformanceItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.RevenueSummaryItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.UnPaidAccount
import net.techandgraphics.wastical.data.local.database.dashboard.payment.UpfrontPaymentDetailItem
import net.techandgraphics.wastical.data.local.database.relations.toEntity
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.getAccountTitle
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
import java.time.temporal.ChronoUnit
import java.util.Locale
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

  private val paint = Paint().apply {
    textSize = PDF_TEXT_SIZE
    typeface = light(application)
  }

  private val createdAtWidth = paint.measureText(ZonedDateTime.now().defaultDate()).padding()
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

  private fun Float.padding() = plus(16f)
  private fun String.mills() = this + " - " + System.currentTimeMillis().toString().drop(5)

  private fun toFullname(title: String, name: String) =
    title.getAccountTitle().plus(" ${name.trim()}")

  private fun String.toContact() = if (isDigitsOnly()) this.takeLast(8) else ""
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

    // Dashboard metrics
    val activeAccounts = accounts.count { it.status == Status.Active }
    val totalAccounts = accounts.size
    val now = ZonedDateTime.now()
    val currentMonth = now.month.value
    val currentYear = now.year
    val startOfMonth = now.withDayOfMonth(1).toEpochSecond()
    val endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).toEpochSecond()
    val newAccountsThisMonth = database.accountIndicatorDao.qRange(
      start = startOfMonth,
      end = endOfMonth,
    ).size

    val payment4CurrentMonth: Payment4CurrentMonth =
      database.accountIndicatorDao.getPayment4CurrentMonth(currentMonth, currentYear)

    val expectedAmountThisMonth = database.paymentIndicatorDao.getExpectedAmountToCollect()
    val totalAmountReceivedAllTime = database.accountIndicatorDao.getTotalAmountReceived() ?: 0
    val unpaidAccountsThisMonth = database.accountIndicatorDao.getTotalUnpaidAccountsThisMonth()

    val recentPayments = database.paymentDao
      .qPaymentWithAccountAndMethodWithGatewayLimit(limit = 4)
      .map { p0 ->
        p0.map {
          it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
        }
      }.first()

    _state.value = CompanyReportState.Success(
      company = company,
      accounts = accounts,
      demographics = demographics,
      allMonthPayments = allMonthPayments,
      monthAccountsCreated = monthAccountsCreated,
      totalAccounts = totalAccounts,
      activeAccounts = activeAccounts,
      newAccountsThisMonth = newAccountsThisMonth,
      expectedAmountThisMonth = expectedAmountThisMonth,
      paidAccountsThisMonth = payment4CurrentMonth.totalPaidAccounts,
      paidAmountThisMonth = payment4CurrentMonth.totalPaidAmount,
      unpaidAccountsThisMonth = unpaidAccountsThisMonth,
      recentPayments = recentPayments,
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

      val fullNameWidth =
        dataset.maxOfOrNull { paint.measureText(it.account.toAccountUiModel().toFullName()) }
          ?.padding()
          ?: 120f

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
            item.demographicArea.plus(", ${item.demographicStreet}"),
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
      val fullNameWidth =
        dataset.maxOfOrNull { paint.measureText(it.title.plus(it.lastname)) }
          ?.padding()
          ?: 120f

      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
      )
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
        locationWidth,
      )

      val filename = "New Clients Report - ${startMonthDate.toDate()}"

      BaseExportKlass<ReportAccountItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf(
          "#",
          "Name",
          "Contact",
          "Amount",
          "Location",
        ),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            toFullname(item.title, item.lastname),
            item.username.toContact(),
            item.fee.toAmount(),
            item.demographicArea.plus(", ${item.demographicStreet}"),
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

      val fullNameWidth =
        dataset.maxOfOrNull { paint.measureText(it.title.plus(it.lastname)) }
          ?.padding()
          ?: 120f

      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
      )
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
        locationWidth,
      )

      val filename = "Active Clients Report - ${ZonedDateTime.now().toDate()}"

      BaseExportKlass<ReportAccountItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf(
          "#",
          "Name",
          "Phone",
          "Amount",
          "Location",
        ),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            toFullname(item.title, item.lastname),
            item.username.toContact(),
            item.fee.toAmount(),
            item.demographicArea.plus(", ${item.demographicStreet}"),
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
        maxYearMonth = state.filters.maxByOrNull { it.year * 100 + it.month }?.let {
          String.format(
            locale = Locale.getDefault(),
            "%04d-%02d",
            it.year, it.month,
          )
        } ?: "9999-12",
      )

      val fullNameWidth =
        dataset.maxOfOrNull { paint.measureText(it.title.plus(it.lastname)) }
          ?.padding()
          ?: 120f

      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(countWidth, fullNameWidth, contactWidth, amountWidth, 20f, 20f)
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths =
        listOf(countWidth, fullNameWidth, contactWidth, amountWidth, locationWidth, 20f, 20f)

      val filename = "Missed Payment Report - ${state.filters.first().toZonedDateTime().toDate()}"

      BaseExportKlass<UnPaidAccount>(application).toPdf(
        company = state.company,
        columnHeaders = listOf("#", "Name", "Contact", "Amount", "Location", "", ""),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            toFullname(item.title, item.lastname),
            item.contact.toContact(),
            item.amount.toAmount(),
            item.demographicArea.plus(", ${item.demographicStreet}"),
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
        maxYearMonth = state.filters.maxByOrNull { it.year * 100 + it.month }?.let {
          String.format(
            locale = Locale.getDefault(),
            "%04d-%02d",
            it.year, it.month,
          )
        } ?: "9999-12",
      )

      val fullNameWidth =
        dataset.maxOfOrNull { paint.measureText(it.title.plus(it.lastname)) }
          ?.padding()
          ?: 120f
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
        valueExtractor = { item ->
          listOf(
            toFullname(item.title, item.lastname),
            item.contact.toContact(),
            item.amount.toAmount(),
            item.paidOn.toZonedDateTime().defaultDate(),
            item.demographicArea.plus(", ${item.demographicStreet}"),
          )
        },
        onEvent = ::onEventPdf,
      )
    }
  }

  private fun onReportLocationBased() = viewModelScope.launch(Dispatchers.IO) {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)

      val areas = state.demographicFilters.map { it.theAreaId }.distinct()
      val streets = state.demographicFilters.map { it.theStreetId }.distinct()

      val dataset = database.accountIndicatorDao.qLocationBased(areas, streets)
      val fullNameWidth =
        dataset.maxOfOrNull { paint.measureText(it.title.plus(it.lastname)) }
          ?.padding()
          ?: 120f

      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
      )
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        amountWidth,
        locationWidth,
      )

      val filename = "Location Based Clients Report - ${ZonedDateTime.now().toDate()}"

      BaseExportKlass<ReportAccountItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf(
          "#",
          "Name",
          "Phone",
          "Amount",
          "Location",
        ),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            toFullname(item.title, item.lastname),
            item.username.toContact(),
            item.fee.toAmount(),
            item.demographicArea.plus(", ${item.demographicStreet}"),
          )
        },
        onEvent = ::onEventPdf,
      )
    }
  }

  private fun onReportClientDisengagement() = viewModelScope.launch(Dispatchers.IO) {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)

      val dataset = database.accountIndicatorDao.qActiveAccounts(status = Status.Inactive.name)

      val fullNameWidth =
        dataset.maxOfOrNull { paint.measureText(it.title.plus(it.lastname)) }?.padding()
          ?: 120f

      val leavingReasonWidth =
        dataset.maxOfOrNull { item -> paint.measureText(item.leavingReason ?: "") }?.padding()
          ?: 120f

      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        leavingReasonWidth,
        createdAtWidth,
      )
      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        leavingReasonWidth,
        createdAtWidth,
        locationWidth,
      )

      val filename = "Client Disengagement Report - ${ZonedDateTime.now().toDate()}"

      BaseExportKlass<ReportAccountItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf(
          "#",
          "Name",
          "Phone",
          "Reason",
          "Date",
          "Location",
        ),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            toFullname(item.title, item.lastname),
            item.username.toContact(),
            item.leavingReason ?: "",
            item.updatedAt.toZonedDateTime().defaultDate(),
            item.demographicArea.plus(", ${item.demographicStreet}"),
          )
        },
        onEvent = ::onEventPdf,
      )
    }
  }

  private fun onReportOutstandingBalance() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val dataset = database.paymentIndicatorDao.qOutstandingBalance().filter { item ->
        val duration = monthsBetween(
          item.account.createdAt
            .toZonedDateTime()
            .withDayOfMonth(1)
            .minusMonths(1),
        )
        item.monthCovered < duration
      }

      val fullNameWidth =
        dataset.maxOfOrNull { paint.measureText(it.account.toAccountUiModel().toFullName()) }
          ?.padding()
          ?: 120f

      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        createdAtWidth,
      )

      val locationWidth = pdfMaxWidth.minus(pdfWidths.sum())
      val columnWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        createdAtWidth,
        locationWidth,
      )

      val filename = "Outstanding Balance Report - ${ZonedDateTime.now().toDate()}"

      BaseExportKlass<OutstandingBalanceItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf(
          "#",
          "Name",
          "Phone",
          "Balance",
          "Location",
        ),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          val duration = monthsBetween(
            item.account.createdAt
              .toZonedDateTime()
              .withDayOfMonth(1)
              .minusMonths(1),
          )
          listOf(
            item.account.toAccountUiModel().toFullName(),
            item.account.username.toContact(),
            duration.times(item.feePlan)
              .minus(item.monthCovered.times(item.feePlan))
              .toAmount(),
            item.demographicArea.plus(", ${item.demographicStreet}"),
          )
        },
        onEvent = ::onEventPdf,
      )
    }
  }

  private fun onReportAgingRaw() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)

      val dataset = database.paymentIndicatorDao.qAgingRaw()

      val fullNameWidth =
        dataset.maxOfOrNull { paint.measureText(it.account.toAccountUiModel().toFullName()) }
          ?.padding()
          ?: 120f

      val countWidth = paint.measureText(dataset.size.toString()).padding()

      val pdfWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        contactWidth,
        contactWidth,
      )

      val columnWidths = pdfWidths + pdfMaxWidth.minus(pdfWidths.sum())

      val filename = "Payment Aging Report - ${ZonedDateTime.now().toDate()}"

      BaseExportKlass<AgingRawItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf("#", "Name", "Contact", "Months", "Fee", "Created"),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            item.account.toAccountUiModel().toFullName(),
            item.account.username.toContact(),
            item.monthCovered.toString(),
            item.feePlan.toAmount(),
            item.createdAt.toZonedDateTime().defaultDateTime(),
          )
        },
        onEvent = ::onEventPdf,
      )
    }
  }

  private fun onReportUpfrontPaymentsDetail() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val dataset = database.paymentIndicatorDao.qUpfrontPaymentsDetail(
        months = state.filters.map { it.month },
        years = state.filters.map { it.year }.distinct(),
      )

      val fullNameWidth =
        dataset.maxOfOrNull { paint.measureText(it.account.toAccountUiModel().toFullName()) }
          ?.padding()
          ?: 120f

      val countWidth = paint.measureText(dataset.size.toString()).padding()

      val pdfWidth = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        contactWidth,
      )

      val columnWidths = pdfMaxWidth.minus(pdfWidth.sum()).div(2)

      val pdfWidths = listOf(
        countWidth,
        fullNameWidth,
        contactWidth,
        columnWidths,
        columnWidths,
        contactWidth,
      )

      val filename = "Upfront Payments - ${ZonedDateTime.now().toDate()}"
      BaseExportKlass<UpfrontPaymentDetailItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf("#", "Name", "Contact", "From", "To", "Months"),
        columnWidths = pdfWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            item.account.toAccountUiModel().toFullName(),
            item.account.username.toContact(),
            item.minMonth.toMonthName().plus(" ${item.minYear}"),
            item.maxMonth.toMonthName().plus(" ${item.maxYear}"),
            item.monthsCoveredThisPayment.toString(),
          )
        },
        onEvent = ::onEventPdf,
      )
    }
  }

  private fun onReportPlanPerformance() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val dataset = database.paymentIndicatorDao.qPlanPerformance(
        months = state.filters.map { it.month },
        years = state.filters.map { it.year }.distinct(),
      )

      val planNameWidth =
        dataset.maxOfOrNull { paint.measureText(it.planName) }
          ?.padding()
          ?: 120f

      val countWidth = paint.measureText(dataset.size.toString()).padding()
      val pdfWidths = listOf(
        countWidth,
        planNameWidth,
        contactWidth,
        createdAtWidth,
      )

      val lastItemWidth = pdfMaxWidth.minus(pdfWidths.sum())

      val columnWidths = listOf(
        countWidth,
        planNameWidth,
        contactWidth,
        contactWidth,
        lastItemWidth,
      )

      val filename = "Plan Performance Report- ${ZonedDateTime.now().toDate()}"

      BaseExportKlass<PlanPerformanceItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf("#", "Plan", "Fee", "Accounts", "Collected"),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            item.planName,
            item.fee.toAmount(),
            item.accounts.toString(),
            item.collectedTotal.toAmount(),
          )
        },
        onEvent = ::onEventPdf,
      )
    }
  }

  private fun onReportRevenueSummary() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val dataset = database.paymentIndicatorDao.qRevenueSummary(
        months = state.filters.map { it.month },
        years = state.filters.map { it.year }.distinct(),
      )

      val countWidth = paint.measureText(dataset.size.toString()).padding()

      val pdfWidth = listOf(
        countWidth,
      )

      val lastWidths = pdfMaxWidth.minus(pdfWidth.sum()).div(3)

      val columnWidths = listOf(
        countWidth,
        lastWidths,
        lastWidths,
        lastWidths,
      )

      val filename = "Revenue Summary Report- ${ZonedDateTime.now().toDate()}"

      BaseExportKlass<RevenueSummaryItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf("#", "Month", "Expected", "Collected"),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            MonthYear(item.month, item.year).toZonedDateTime().toDate(),
            item.expectedTotal.toAmount(),
            item.collectedTotal.toAmount(),
          )
        },
        onEvent = ::onEventPdf,
      )
    }
  }

  private fun onReportLocationCollection() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val dataset = database.paymentIndicatorDao.qLocationCollection(
        months = state.filters.map { it.month },
        years = state.filters.map { it.year }.distinct(),
      )

      val collectedWidth =
        dataset.maxOfOrNull { paint.measureText(it.collectedTotal.toAmount()) }
          ?.padding()
          ?: 120f

      val countWidth = paint.measureText(dataset.size.toString()).padding()

      val pdfWidths = listOf(
        countWidth,
        contactWidth,
        collectedWidth,
      )

      val columnWidths = pdfWidths + pdfMaxWidth.minus(pdfWidths.sum())

      val filename = "Location Collection Report - ${ZonedDateTime.now().toDate()}"
      BaseExportKlass<LocationCollectionItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf("#", "Accounts", "Collected", "Location"),
        columnWidths = columnWidths,
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            item.totalAccounts.toString(),
            item.collectedTotal.toAmount(),
            item.demographicArea.plus(", ${item.demographicStreet}"),
          )
        },
        onEvent = ::onEventPdf,
      )
    }
  }

  private fun monthsBetween(start: ZonedDateTime, end: ZonedDateTime = ZonedDateTime.now()): Long {
    return ChronoUnit.MONTHS.between(start, end)
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
      CompanyReportEvent.Button.Report.OutstandingBalance -> onReportOutstandingBalance()
      CompanyReportEvent.Button.Report.LocationBased -> onReportLocationBased()
      CompanyReportEvent.Button.Report.ClientDisengagement -> onReportClientDisengagement()
      CompanyReportEvent.Button.Report.RevenueSummary -> onReportRevenueSummary()
      CompanyReportEvent.Button.Report.PaymentMethodBreakdown -> onReportPaymentMethodBreakdown()
      CompanyReportEvent.Button.Report.PlanPerformance -> onReportPlanPerformance()
      CompanyReportEvent.Button.Report.LocationCollection -> onReportLocationCollection()
      CompanyReportEvent.Button.Report.GatewaySuccess -> onReportGatewaySuccess()
      CompanyReportEvent.Button.Report.UpfrontPaymentsDetail -> onReportUpfrontPaymentsDetail()
      CompanyReportEvent.Button.Report.PaymentAging -> onReportAgingRaw()
    }
  }

  private fun onReportPaymentMethodBreakdown() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val dataset = database.paymentIndicatorDao.qPaymentMethodBreakdown(
        months = state.filters.map { it.month },
        years = state.filters.map { it.year }.distinct(),
      )

      val filename = "Payment Method Breakdown - ${ZonedDateTime.now().toDate()}"

      BaseExportKlass<PaymentMethodBreakdownItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf("#", "Gateway", "Payments", "Months", "Total"),
        columnWidths = listOf(
          160f,
          80f,
          80f,
          120f,
          pdfMaxWidth - (24f + 160f + 80f + 80f + 120f),
        ),
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            item.gatewayName,
            item.payments.toString(),
            item.monthsCovered.toString(),
            item.totalAmount.toAmount(),
          )
        },
        onEvent = ::onEventPdf,
      )
    }
  }

  private fun onReportGatewaySuccess() = viewModelScope.launch {
    if (_state.value is CompanyReportState.Success) {
      val state = (_state.value as CompanyReportState.Success)
      val dataset = database.paymentIndicatorDao.qGatewaySuccess(
        months = state.filters.map { it.month },
        years = state.filters.map { it.year }.distinct(),
      )
      val filename = "Gateway Success - ${ZonedDateTime.now().toDate()}"
      BaseExportKlass<GatewaySuccessItem>(application).toPdf(
        company = state.company,
        columnHeaders = listOf("#", "Gateway", "Approved", "Total"),
        columnWidths = listOf(160f, 80f, 80f, pdfMaxWidth - (24f + 160f + 80f + 80f)),
        filename = filename.mills(),
        pageTitle = filename,
        items = dataset,
        valueExtractor = { item ->
          listOf(
            item.gatewayName,
            item.approvedCount.toString(),
            item.totalCount.toString(),
          )
        },
        onEvent = ::onEventPdf,
      )
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
