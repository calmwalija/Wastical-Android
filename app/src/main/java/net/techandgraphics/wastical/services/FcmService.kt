package net.techandgraphics.wastical.services

import android.accounts.AccountManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.services.client.ClientFcmEvent
import net.techandgraphics.wastical.services.company.CompanyFcmEvent
import net.techandgraphics.wastical.worker.scheduleAccountFcmTokenWorker
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

  @Inject lateinit var database: AppDatabase

  @Inject lateinit var paymentApi: PaymentApi

  @Inject lateinit var authenticatorHelper: AuthenticatorHelper

  @Inject lateinit var accountManager: AccountManager

  private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    authenticatorHelper.getAccount(accountManager)
      ?.let { account ->
        when (AccountRole.valueOf(account.role)) {
          AccountRole.Client ->
            ClientFcmEvent(
              context = this@FcmService,
              coroutineScope = coroutineScope,
              remoteMessage = remoteMessage,
              database = database,
              paymentApi = paymentApi,
            ).onEvent()

          AccountRole.Company ->
            CompanyFcmEvent(
              context = this@FcmService,
              coroutineScope = coroutineScope,
              remoteMessage = remoteMessage,
              database = database,
              paymentApi = paymentApi,
            ).onEvent()
        }
      }
    super.onMessageReceived(remoteMessage)
  }

  override fun onNewToken(token: String) {
    coroutineScope.launch {
      database.accountFcmTokenDao.upsert(AccountFcmTokenEntity(token = token))
      scheduleAccountFcmTokenWorker()
    }
    super.onNewToken(token)
  }
}
