package net.techandgraphics.wastical.ui.screen.client.payment

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toPaymentMethodEntity
import net.techandgraphics.wastical.data.local.database.toPaymentRequestEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.payment.PaymentRequest
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.getProofFile
import net.techandgraphics.wastical.getProofFileWithExtension
import net.techandgraphics.wastical.getProofTargetFile
import net.techandgraphics.wastical.getReference
import net.techandgraphics.wastical.getUCropFile
import net.techandgraphics.wastical.worker.client.payment.scheduleClientPaymentRequestWorker
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel class ClientPaymentViewModel @Inject constructor(
  private val application: Application,
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientPaymentState>(ClientPaymentState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<ClientPaymentChannel>()
  val channel = _channel.receiveAsFlow()
  private var recordPaymentJob: Job? = null

  private fun onLoad(event: ClientPaymentEvent.Load) = viewModelScope.launch {
    database.accountDao.flowById(event.id).mapNotNull { it?.toAccountUiModel() }
      .collectLatest { account ->
        val company = database.companyDao.query().first().toCompanyUiModel()
        val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
        val paymentPlan =
          database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
        database.paymentMethodDao.flowOfWithGatewayAndPlan()
          .map { p0 -> p0.map { it.toPaymentMethodWithGatewayAndPlanUiModel() } }
          .collectLatest { paymentMethods ->
            _state.value = ClientPaymentState.Success(
              company = company,
              account = account,
              paymentMethods = paymentMethods,
              paymentPlan = paymentPlan,
            )
          }
      }
  }

  private fun onSubmit() {
    recordPaymentJob?.cancel()
    recordPaymentJob = viewModelScope.launch {
      delay(5_00)
      if (_state.value is ClientPaymentState.Success) {
        val state = (_state.value as ClientPaymentState.Success)
        val preparedTimestamp =
          if (state.timestamp > 0) state.timestamp else System.currentTimeMillis()
        state.imageUri?.let { saveCroppedImage(preparedTimestamp, it) }
        val paymentMethods = state.paymentMethods.map { it.method }
        val paymentMethod = paymentMethods.firstOrNull { it.isSelected } ?: paymentMethods.last()
        val cachedPayment = PaymentRequest(
          paymentMethodId = paymentMethod.id,
          accountId = state.account.id,
          months = state.monthsCovered,
          companyId = state.company.id,
          executedById = state.account.id,
          status = PaymentStatus.Waiting,
          httpOperation = HttpOperation.Post.name,
          paymentReference = getReference(),
        ).toPaymentRequestEntity()
        val ext = when (application.contentResolver.getType(state.imageUri ?: Uri.EMPTY)) {
          "application/pdf" -> "pdf"
          else -> "jpg"
        }
        database.paymentRequestDao.upsert(cachedPayment.copy(proofExt = ext))
        val newPayment = database.paymentRequestDao.getLast()
        val oldFile =
          application.getProofFile(preparedTimestamp) ?: application.getUCropFile(preparedTimestamp)
        if (oldFile.exists()) {
          val ext = if (oldFile.name.endsWith(".pdf", ignoreCase = true)) "pdf" else "jpg"
          oldFile.renameTo(application.getProofFileWithExtension(newPayment.createdAt, ext))
        }
        application.scheduleClientPaymentRequestWorker()
        _state.value = state.copy(imageUri = null)
        _channel.send(ClientPaymentChannel.Pay.Success)
      }
    }
  }

  private fun saveCroppedImage(timestamp: Long, src: Uri) {
    try {
      val type = application.contentResolver.getType(src) ?: "image/*"
      if (type.startsWith("application/pdf")) {
        application.contentResolver.openInputStream(src)?.use { input ->
          FileOutputStream(application.getProofTargetFile(timestamp, type)).use { output ->
            input.copyTo(output)
          }
        }
      } else {
        application.contentResolver.openInputStream(src)?.use { input ->
          val originalBitmap = BitmapFactory.decodeStream(input) ?: return
          var workingBitmap = originalBitmap

          val targetMaxBytes = 500 * 1024 // 500KB
          var jpegQuality = 90

          fun compress(bitmap: Bitmap, quality: Int): ByteArray {
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
            return bos.toByteArray()
          }

          var compressed = compress(workingBitmap, jpegQuality)
          var currentWidth = workingBitmap.width
          var currentHeight = workingBitmap.height

          while (compressed.size > targetMaxBytes) {
            if (jpegQuality > 50) {
              jpegQuality -= 10
            } else {
              val newWidth = (currentWidth * 0.85f).toInt().coerceAtLeast(512)
              val newHeight = (currentHeight * 0.85f).toInt().coerceAtLeast(512)
              if (newWidth == currentWidth && newHeight == currentHeight) break
              workingBitmap = workingBitmap.scale(newWidth, newHeight)
              currentWidth = newWidth
              currentHeight = newHeight
              jpegQuality = 90
            }
            compressed = compress(workingBitmap, jpegQuality)
          }

          FileOutputStream(application.getProofTargetFile(timestamp, type)).use { output ->
            output.write(compressed)
          }

          if (workingBitmap !== originalBitmap) workingBitmap.recycle()
          originalBitmap.recycle()
        }
      }
    } catch (_: Exception) {
    }
  }

  private fun onScreenshotAttached() {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      val uri = state.imageUri
      if (uri != null) {
        val ts = if (state.timestamp <= 0) System.currentTimeMillis() else state.timestamp
        saveCroppedImage(ts, uri)
        _state.value = state.copy(screenshotAttached = true, timestamp = ts)
      } else {
        _state.value = state.copy(screenshotAttached = true)
      }
    }
  }

  private fun onMonthCovered(event: ClientPaymentEvent.Button.MonthCovered) {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      val monthsCovered = when {
        event.isAdd -> state.monthsCovered.plus(1).coerceAtMost(12)
        else -> state.monthsCovered.minus(1).coerceAtLeast(1)
      }
      _state.value = state.copy(monthsCovered = monthsCovered)
    }
  }

  private fun onPaymentMethod(event: ClientPaymentEvent.Button.PaymentMethod) =
    viewModelScope.launch {
      if (_state.value is ClientPaymentState.Success) {
        val state = (_state.value as ClientPaymentState.Success)
        state.paymentMethods.map { it.method }
          .map { it.toPaymentMethodEntity().copy(isSelected = false) }
          .also { database.paymentMethodDao.update(it) }
        event.item.method.copy(isSelected = true).toPaymentMethodEntity()
          .also { database.paymentMethodDao.update(it) }
      }
    }

  private fun onImageUri(event: ClientPaymentEvent.Button.ImageUri) {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      _state.value = state.copy(imageUri = event.uri)
    }
  }

  private fun onShowCropView(event: ClientPaymentEvent.Button.ShowCropView) {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      _state.value = state.copy(showCropView = event.show)
    }
  }

  fun onEvent(event: ClientPaymentEvent) {
    when (event) {
      ClientPaymentEvent.Button.Submit -> onSubmit()
      is ClientPaymentEvent.Button.MonthCovered -> onMonthCovered(event)
      is ClientPaymentEvent.Button.ImageUri -> onImageUri(event)
      is ClientPaymentEvent.Button.ShowCropView -> onShowCropView(event)
      ClientPaymentEvent.Button.ScreenshotAttached -> onScreenshotAttached()
      is ClientPaymentEvent.Load -> onLoad(event)
      is ClientPaymentEvent.Button.PaymentMethod -> onPaymentMethod(event)
      else -> Unit
    }
  }
}
