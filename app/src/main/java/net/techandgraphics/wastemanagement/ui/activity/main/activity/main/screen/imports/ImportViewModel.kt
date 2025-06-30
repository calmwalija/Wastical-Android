package net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.imports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentRequestEntity
import net.techandgraphics.wastemanagement.data.remote.toPaymentRequest
import net.techandgraphics.wastemanagement.domain.toPaymentUiModel
import net.techandgraphics.wastemanagement.ui.screen.company.home.CompanyMetaData
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow(ImportState())
  val state = _state.asStateFlow()

  private val _channel = Channel<ImportChannel>()
  val channel = _channel.receiveAsFlow()

  private suspend fun payments(data: CompanyMetaData) {
    data.payments
      .map { it.toPaymentEntity() }
      .forEach { payment ->
        val shouldInsertPayment =
          database.paymentDao.getByCreatedAt(payment.accountId, payment.createdAt) == null
        println("❌❌❌❌ shouldInsertPayment $shouldInsertPayment")
        if (shouldInsertPayment) database.paymentDao.insert(payment)
        database.paymentRequestDao.delete(
          payment
            .toPaymentUiModel()
            .toPaymentRequest()
            .toPaymentRequestEntity(),
        )
      }
    monthCovered(data)
  }

  private suspend fun monthCovered(data: CompanyMetaData) {
    data.monthCovered.forEach { monthCover ->
      val shouldInsertMonthCovered =
        database.paymentMonthCoveredDao.getByCreatedAt(
          monthCover.accountId,
          monthCover.month,
          monthCover.createdAt,
        ) == null

      println("❌❌❌❌❌❌ shouldInsertMonthCovered $shouldInsertMonthCovered")

      if (shouldInsertMonthCovered) {
        database.paymentMonthCoveredDao.insert(monthCover.toPaymentMonthCoveredEntity())
      }
    }
  }

  private fun onImport(event: ImportEvent.Import) = viewModelScope.launch {
    runCatching {
      database.withTransaction {
        Gson().fromJson(event.jsonString, CompanyMetaData::class.java).also {
          payments(it)
        }
      }
    }.onSuccess {
      _channel.send(ImportChannel.Success)
      delay(2_000)
      _channel.send(ImportChannel.Done)
    }
      .onFailure {
        _channel.send(ImportChannel.Error)
        delay(2_000)
        _channel.send(ImportChannel.Done)
      }
  }

  fun onEvent(event: ImportEvent) {
    when (event) {
      is ImportEvent.Import -> onImport(event)
    }
  }
}
