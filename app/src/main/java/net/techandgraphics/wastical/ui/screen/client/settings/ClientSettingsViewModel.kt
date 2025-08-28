package net.techandgraphics.wastical.ui.screen.client.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.Preferences
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.domain.toAccountContactUiModel
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyContactUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.worker.client.notification.cancelClientBinCollectionReminderWorker
import net.techandgraphics.wastical.worker.client.notification.scheduleClientBinCollectionReminderWorker
import net.techandgraphics.wastical.worker.client.payment.cancelClientPaymentDueReminderWorker
import net.techandgraphics.wastical.worker.client.payment.scheduleClientPaymentDueReminderWorker
import javax.inject.Inject

@HiltViewModel
class ClientSettingsViewModel @Inject constructor(
  private val database: AppDatabase,
  private val preferences: Preferences,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientSettingsState>(ClientSettingsState.Loading)
  val state = _state.asStateFlow()

  private fun onLoad(event: ClientSettingsEvent.Load) = viewModelScope.launch {
    val account = database.accountDao.get(event.id).toAccountUiModel()
    val company = database.companyDao.query().first().toCompanyUiModel()
    val contacts = database.accountContactDao.query().map { it.toAccountContactUiModel() }
    val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
    val paymentPlan = database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
    val companyLocation = database.companyLocationDao.get(account.companyLocationId)
    val street = database.demographicStreetDao.get(companyLocation.demographicStreetId)
    val area = database.demographicAreaDao.get(companyLocation.demographicAreaId)
    val companyContacts = database.companyContactDao.query().map { it.toCompanyContactUiModel() }
    preferences.flowOf<Boolean>(Preferences.DYNAMIC_COLOR, false)
      .collectLatest { dynamicColor ->
        val reminderPayment = preferences.get(Preferences.CLIENT_REMINDER_PAYMENT, true)
        val reminderBin = preferences.get(Preferences.CLIENT_REMINDER_BIN, true)
        _state.value = ClientSettingsState.Success(
          company = company,
          account = account,
          contacts = contacts,
          dynamicColor = dynamicColor,
          plan = paymentPlan,
          streetName = street.name,
          areaName = area.name,
          companyContacts = companyContacts,
          reminderPayment = reminderPayment,
          reminderBin = reminderBin,
        )
      }
  }

  private fun onButtonDynamicColor(event: ClientSettingsEvent.Button.DynamicColor) =
    viewModelScope.launch {
      if (_state.value is ClientSettingsState.Success) {
        preferences.put<Boolean>(Preferences.DYNAMIC_COLOR, event.isEnabled)
        _state.value = (_state.value as ClientSettingsState.Success)
          .copy(dynamicColor = event.isEnabled)
      }
    }

  private fun onDarkTheme(event: ClientSettingsEvent.Button.DarkTheme) = viewModelScope.launch {
    if (_state.value is ClientSettingsState.Success) {
      preferences.put<Boolean>(Preferences.DARK_THEME, event.isEnabled)
      _state.value = (_state.value as ClientSettingsState.Success)
        .copy(darkTheme = event.isEnabled)
    }
  }

  private fun onReminderPayment(event: ClientSettingsEvent.Button.ReminderPayment) =
    viewModelScope.launch {
      if (_state.value is ClientSettingsState.Success) {
        preferences.put<Boolean>(Preferences.CLIENT_REMINDER_PAYMENT, event.isEnabled)
        if (event.isEnabled) {
          // schedule; worker will no-op if not due
          preferences.context.scheduleClientPaymentDueReminderWorker()
        } else {
          preferences.context.cancelClientPaymentDueReminderWorker()
        }
        _state.value = (_state.value as ClientSettingsState.Success)
          .copy(reminderPayment = event.isEnabled)
      }
    }

  private fun onReminderBin(event: ClientSettingsEvent.Button.ReminderBin) =
    viewModelScope.launch {
      if (_state.value is ClientSettingsState.Success) {
        preferences.put<Boolean>(Preferences.CLIENT_REMINDER_BIN, event.isEnabled)
        if (event.isEnabled) preferences.context.scheduleClientBinCollectionReminderWorker() else preferences.context.cancelClientBinCollectionReminderWorker()
        _state.value = (_state.value as ClientSettingsState.Success)
          .copy(reminderBin = event.isEnabled)
      }
    }

  fun onEvent(event: ClientSettingsEvent) {
    when (event) {
      is ClientSettingsEvent.Load -> onLoad(event)
      is ClientSettingsEvent.Button.DynamicColor -> onButtonDynamicColor(event)
      is ClientSettingsEvent.Button.DarkTheme -> onDarkTheme(event)
      is ClientSettingsEvent.Button.ReminderPayment -> onReminderPayment(event)
      is ClientSettingsEvent.Button.ReminderBin -> onReminderBin(event)
      else -> Unit
    }
  }
}
