package net.techandgraphics.wastical.ui.screen.company.payment.pay

import android.accounts.AccountManager
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toPaymentMethodEntity
import net.techandgraphics.wastical.data.local.database.toPaymentRequestEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.payment.PaymentRequest
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.data.remote.payment.PaymentType
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.image2Text
import net.techandgraphics.wastical.toBitmap
import net.techandgraphics.wastical.toSoftwareBitmap
import net.techandgraphics.wastical.worker.company.payment.scheduleCompanyPaymentRequestWorker
import javax.inject.Inject

@HiltViewModel
class CompanyMakePaymentViewModel @Inject constructor(
  private val database: AppDatabase,
  private val imageLoader: ImageLoader,
  private val application: Application,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyMakePaymentState>(CompanyMakePaymentState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<CompanyMakePaymentChannel>()
  val channel = _channel.receiveAsFlow()

  private fun onLoad(event: CompanyMakePaymentEvent.Load) =
    viewModelScope.launch {
      authenticatorHelper.getAccount(accountManager)
        ?.let { executedBy ->
          _state.value = CompanyMakePaymentState.Loading
          val account = database.accountDao.get(event.id).toAccountUiModel()
          val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
          val company = database.companyDao.query().first().toCompanyUiModel()
          val paymentPlan =
            database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
          val paymentMethods = database.paymentMethodDao.qWithGatewayByPaymentPlanId(paymentPlan.id)
            .filter { it.gateway.type == PaymentType.Cash.name }
            .map { it.toPaymentMethodWithGatewayAndPlanUiModel() }
          val demographic =
            database.companyLocationDao.getWithDemographic(account.companyLocationId)
              .toCompanyLocationWithDemographicUiModel()
          _state.value = CompanyMakePaymentState.Success(
            company = company,
            account = account,
            paymentPlan = paymentPlan,
            paymentMethods = paymentMethods,
            imageLoader = imageLoader,
            demographic = demographic,
            executedBy = executedBy,
          )
        }
    }

  private fun onRecordPayment() = viewModelScope.launch {
    with(state.value as CompanyMakePaymentState.Success) {
      val method = paymentMethods.first { it.method.isSelected }
      val cachedPayment = PaymentRequest(
        screenshotText = screenshotText,
        paymentMethodId = method.method.id,
        accountId = account.id,
        months = numberOfMonths,
        companyId = account.companyId,
        executedById = executedBy.id,
        status = PaymentStatus.Approved,
        httpOperation = HttpOperation.Post.name,
      ).toPaymentRequestEntity()
      database.paymentRequestDao.upsert(cachedPayment)
      _channel.send(CompanyMakePaymentChannel.Pay.Success)
      _state.value = getState().copy(imageUri = null)
      application.scheduleCompanyPaymentRequestWorker()
    }
  }

  private fun getState() = (_state.value as CompanyMakePaymentState.Success)

  private fun onPaymentMethod(event: CompanyMakePaymentEvent.Button.PaymentMethod) =
    viewModelScope.launch {
      database.paymentMethodDao.query().map { it.copy(isSelected = false) }
        .also { database.paymentMethodDao.update(it) }
      event.method.toPaymentMethodEntity().copy(isSelected = true)
        .also { database.paymentMethodDao.update(it) }
      val paymentMethods = database.paymentMethodDao
        .qWithGatewayByPaymentPlanId(event.method.paymentPlanId)
        .filter { it.gateway.type == PaymentType.Cash.name }
        .map { it.toPaymentMethodWithGatewayAndPlanUiModel() }
      _state.value = getState().copy(paymentMethods = paymentMethods)
    }

  private fun onNumberOfMonths(event: CompanyMakePaymentEvent.Button.NumberOfMonths) {
    var state = _state.value as CompanyMakePaymentState.Success
    _state.value = if (event.isAdd) {
      state.copy(numberOfMonths = state.numberOfMonths.plus(1))
    } else {
      state.copy(numberOfMonths = state.numberOfMonths.minus(1))
    }
  }

  private fun onScreenshotAttached() {
    var state = _state.value as CompanyMakePaymentState.Success
    _state.value = getState().copy(screenshotAttached = true)
    state.imageUri?.toBitmap(application)?.toSoftwareBitmap()?.run {
      image2Text {
        it.onSuccess { text -> _state.value = getState().copy(screenshotAttached = true) }
        it.onFailure(::println)
      }
    }
  }

  fun onEvent(event: CompanyMakePaymentEvent) {
    when (event) {
      is CompanyMakePaymentEvent.Load -> onLoad(event)
      is CompanyMakePaymentEvent.Button.PaymentMethod -> onPaymentMethod(event)
      is CompanyMakePaymentEvent.Button.NumberOfMonths -> onNumberOfMonths(event)

      is CompanyMakePaymentEvent.Button.RecordPayment -> onRecordPayment()
      CompanyMakePaymentEvent.Button.ScreenshotAttached -> onScreenshotAttached()

      is CompanyMakePaymentEvent.Button.ImageUri -> {
        _state.value =
          (_state.value as CompanyMakePaymentState.Success).copy(imageUri = event.uri)
      }

      is CompanyMakePaymentEvent.Button.ShowCropView -> {
        _state.value =
          (_state.value as CompanyMakePaymentState.Success).copy(showCropView = event.show)
      }

      else -> Unit
    }
  }
}
