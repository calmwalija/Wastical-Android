package net.techandgraphics.wastemanagement.ui.screen.company.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastemanagement.data.remote.account.ACCOUNT_ID
import net.techandgraphics.wastemanagement.data.remote.toAccountPaymentPlanResponse
import net.techandgraphics.wastemanagement.data.remote.toPaymentResponse
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentRequestUiModel
import net.techandgraphics.wastemanagement.getFile
import net.techandgraphics.wastemanagement.getToday
import net.techandgraphics.wastemanagement.hash
import net.techandgraphics.wastemanagement.write
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CompanyHomeViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
  private val accountSession: AccountSessionRepository,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyHomeState>(CompanyHomeState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<CompanyHomeChannel>()
  val channel = _channel.receiveAsFlow()

  init {
    viewModelScope.launch {
      database.accountDao.flow().collectLatest {
        if (it.isNotEmpty()) {
          onEvent(CompanyHomeEvent.Load)
        }
      }
    }
  }

  fun dateFormat(timestamp: Long, patten: String = "d-MMMM-yyyy"): String {
    val simpleDateFormat = SimpleDateFormat(patten, Locale.getDefault())
    val currentTimeMillis = Date(timestamp)
    return simpleDateFormat.format(currentTimeMillis)
  }

  private fun onExportMetadata() = viewModelScope.launch(Dispatchers.IO) {
    if (_state.value is CompanyHomeState.Success) {
      val state = (_state.value as CompanyHomeState.Success)
      val currentTimeMillis = System.currentTimeMillis()
      val fileName = "${state.company.name}-BackUp-${dateFormat(currentTimeMillis)}.json"
      val payments = database.paymentRequestDao.query()
        .map { it.toPaymentResponse() }
      val plans = database.accountPaymentPlanRequestDao.query()
        .map { it.toAccountPaymentPlanResponse() }
      val toExportData = CompanyMetaData(
        payments = payments,
        plans = plans,
      )
      val hashable = currentTimeMillis.hash(toExportData.toHash())
      val jsonToExport = Gson().toJson(toExportData.copy(hashable = hashable))
      val file = application.write(jsonToExport, fileName)
      _channel.send(CompanyHomeChannel.Export(file))
    }
  }

  private fun onLoad() = viewModelScope.launch(Dispatchers.IO) {
    val (_, month, year) = getToday()
    val payment4CurrentLocationMonth =
      database.streetIndicatorDao.getPayment4CurrentLocationMonth(month, year)
    val payment4CurrentMonth = database.accountIndicatorDao.getPayment4CurrentMonth(month, year)
    val account = database.accountDao.get(ACCOUNT_ID).toAccountUiModel()
    val pending = database.paymentRequestDao.query().map { it.toPaymentRequestUiModel() }
    val company = database.companyDao.query().first().toCompanyUiModel()
    val companyContact = database.companyContactDao.query().first().toCompanyContactUiModel()
    val accountsSize = database.accountDao.getSize()
    val expectedAmountToCollect = database.paymentIndicatorDao.getExpectedAmountToCollect()
    val paymentPlanAgainstAccounts = database.paymentIndicatorDao.getPaymentPlanAgainstAccounts()
    _state.value = CompanyHomeState.Success(
      payment4CurrentMonth = payment4CurrentMonth,
      pending = pending,
      accountsSize = accountsSize,
      payment4CurrentLocationMonth = payment4CurrentLocationMonth,
      company = company,
      account = account,
      companyContact = companyContact,
      expectedAmountToCollect = expectedAmountToCollect,
      paymentPlanAgainstAccounts = paymentPlanAgainstAccounts,
    )
  }

  private fun onImportMetadata(event: CompanyHomeEvent.Button.Import) = viewModelScope.launch {
    _channel.send(CompanyHomeChannel.Import.Data(CompanyHomeChannel.Import.Status.Wait))
    runCatching {
      val jsonString = application.getFile(event.uri).bufferedReader().use { it.readText() }
      Gson().fromJson(jsonString, CompanyMetaData::class.java)
    }.onSuccess { metadata ->

      if (metadata == null || metadata.currentTimeMillis.hash(metadata.toHash()) != metadata.hashable) {
        _channel.send(CompanyHomeChannel.Import.Data(CompanyHomeChannel.Import.Status.Invalid))
        return@launch
      }
      when (metadata.ofType) {
        MetaType.Request -> {
        }

        MetaType.Response -> {
          var current = 0
          runCatching {
            database.withTransaction {
//              database.clearAllTables()
              accountSession.purseData(metadata.serverResponse!!) { total, done ->
                current += done
                _channel.send(CompanyHomeChannel.Import.Progress(total, current))
              }
            }
          }.onFailure { _channel.send(CompanyHomeChannel.Import.Data(CompanyHomeChannel.Import.Status.Error)) }
        }
      }
    }
      .onFailure { _channel.send(CompanyHomeChannel.Import.Data(CompanyHomeChannel.Import.Status.Error)) }
  }

  fun onEvent(event: CompanyHomeEvent) {
    when (event) {
      is CompanyHomeEvent.Load -> onLoad()
      is CompanyHomeEvent.Button.Import -> onImportMetadata(event)
      CompanyHomeEvent.Button.Export -> onExportMetadata()
      else -> Unit
    }
  }
}
