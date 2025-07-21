package net.techandgraphics.quantcal.ui.screen.auth.phone.load

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.worker.scheduleAccountSessionWorker
import javax.inject.Inject

@HiltViewModel
class LoadViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _channel = Channel<LoadChannel>()
  val channel = _channel.receiveAsFlow()
  private val _state = MutableStateFlow(LoadState())
  val state = _state.asStateFlow()

  private fun onLoad() = viewModelScope.launch {
    database.companyDao.flow()
      .map { p0 -> p0.map { it.toCompanyUiModel() } }
      .collectLatest { companies ->
        _state.update { it.copy(companies = companies) }
        if (companies.isNotEmpty()) {
          _channel.send(LoadChannel.Success)
        } else {
          application.scheduleAccountSessionWorker()
        }
      }
  }

  fun onEvent(event: LoadEvent) {
    when (event) {
      LoadEvent.Load -> onLoad()
    }
  }
}
