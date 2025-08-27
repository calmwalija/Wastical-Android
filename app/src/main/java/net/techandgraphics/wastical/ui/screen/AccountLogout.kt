package net.techandgraphics.wastical.ui.screen

import android.accounts.AccountManager
import androidx.room.withTransaction
import com.google.firebase.messaging.FirebaseMessaging
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.getAccount
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountLogout @Inject constructor(
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) {
  suspend operator fun invoke(): Result<Unit> {
    return runCatching {
      val companyUuid = database.companyDao.query().firstOrNull()?.uuid
      val locationUui = database.companyLocationDao.query().firstOrNull()?.uuid
      val accountUuid = authenticatorHelper.getAccount(accountManager)?.uuid
      database.withTransaction { database.clearAllTables() }
      with(FirebaseMessaging.getInstance()) {
        companyUuid?.let { unsubscribeFromTopic(companyUuid) }
        accountUuid?.let { unsubscribeFromTopic(accountUuid) }
        locationUui?.let { unsubscribeFromTopic(locationUui) }
      }
      authenticatorHelper.deleteAccounts()
    }
  }
}
