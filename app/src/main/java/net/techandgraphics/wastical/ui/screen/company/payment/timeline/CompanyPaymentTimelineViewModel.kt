package net.techandgraphics.wastical.ui.screen.company.payment.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.relations.toEntity
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.toZonedDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompanyPaymentTimelineViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyPaymentTimelineState>(CompanyPaymentTimelineState.Loading)
  private var searchJob: Job? = null

  val state = _state.asStateFlow()

  private fun onLoad() = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    _state.value = CompanyPaymentTimelineState.Success(company = company)
    loadTimeline()
  }

  private fun List<Long>.groupByDate(): List<PaymentDateTime> {
    return this
      .groupBy { timestamp -> timestamp.toZonedDateTime().toLocalDate() }
      .map {
        PaymentDateTime(
          date = it.key,
          time = it.value,
        )
      }
  }

  @OptIn(ExperimentalPagingApi::class)
  private fun loadTimeline(query: String = "") {
    if (_state.value is CompanyPaymentTimelineState.Success) {
      val state = (_state.value as CompanyPaymentTimelineState.Success)
      val pagingSourceFactory = database.paymentDao.pagingAllWithFilters(
        query = query,
        fromTs = state.fromTs,
        toTs = state.toTs,
        sortDesc = state.sortDesc,
      )
      Pager(
        config = PagingConfig(
          pageSize = 20,
          initialLoadSize = 40,
          prefetchDistance = 10,
        ),
        pagingSourceFactory = { pagingSourceFactory },
      ).flow
        .map { p0 -> p0.map { it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel() } }
        .also { flowOfPayments ->
          _state.value = (_state.value as CompanyPaymentTimelineState.Success).copy(
            payments = flowOfPayments,
          )
        }
    }
  }

  fun onEvent(event: CompanyPaymentTimelineEvent) {
    when (event) {
      CompanyPaymentTimelineEvent.Load -> onLoad()
      is CompanyPaymentTimelineEvent.InputQuery -> onQuery(event.query)
      is CompanyPaymentTimelineEvent.DateFrom -> onDateFrom(event.ts)
      is CompanyPaymentTimelineEvent.DateTo -> onDateTo(event.ts)
      is CompanyPaymentTimelineEvent.SortDesc -> onSortDesc(event.value)
      is CompanyPaymentTimelineEvent.DatePreset -> onPreset(event.preset)
      else -> Unit
    }
  }

  private fun onQuery(query: String) {
    val s = _state.value
    if (s is CompanyPaymentTimelineState.Success) {
      _state.value = s.copy(query = query)
      searchJob?.cancel()
      searchJob = viewModelScope.launch {
        delay(500)
        loadTimeline(query)
      }
    }
  }

  private fun onDateFrom(ts: Long?) {
    val s = _state.value
    if (s is CompanyPaymentTimelineState.Success) {
      _state.value = s.copy(fromTs = ts)
      loadTimeline(s.query)
    }
  }

  private fun onDateTo(ts: Long?) {
    val s = _state.value
    if (s is CompanyPaymentTimelineState.Success) {
      _state.value = s.copy(toTs = ts)
      loadTimeline(s.query)
    }
  }

  private fun onSortDesc(value: Boolean) {
    val s = _state.value
    if (s is CompanyPaymentTimelineState.Success) {
      _state.value = s.copy(sortDesc = value)
      loadTimeline(s.query)
    }
  }

  private fun onPreset(preset: DateRangePreset) {
    val s = _state.value
    if (s is CompanyPaymentTimelineState.Success) {
      val zone = ZoneId.systemDefault()
      val now = ZonedDateTime.now(zone)
      val (fromTs, toTs) = when (preset) {
        DateRangePreset.All -> null to null
        DateRangePreset.Today -> now.toLocalDate().atStartOfDay(zone)
          .toEpochSecond() to now.toEpochSecond()

        DateRangePreset.Last7Days -> now.minusDays(7).toLocalDate().atStartOfDay(zone)
          .toEpochSecond() to now.toEpochSecond()

        DateRangePreset.ThisMonth -> now.withDayOfMonth(1).toLocalDate().atStartOfDay(zone)
          .toEpochSecond() to now.toEpochSecond()

        DateRangePreset.ThisYear -> now.withDayOfYear(1).toLocalDate().atStartOfDay(zone)
          .toEpochSecond() to now.toEpochSecond()
      }
      _state.value = s.copy(fromTs = fromTs, toTs = toTs)
      loadTimeline(s.query)
    }
  }
}
