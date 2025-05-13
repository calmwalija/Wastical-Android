package net.techandgraphics.wastemanagement.account

import android.accounts.Account
import android.accounts.AccountManager
import androidx.core.os.bundleOf
import com.google.gson.Gson
import net.techandgraphics.wastemanagement.keycloak.AccessTokenResponse
import net.techandgraphics.wastemanagement.keycloak.JwtAccount
import net.techandgraphics.wastemanagement.keycloak.JwtManager
import javax.inject.Inject

class AuthenticatorHelper @Inject constructor(private val accountManager: AccountManager) {

  companion object {
    private const val ACCOUNT_TYPE = "net.techandgraphics.wastemanagement"
    private const val JWT_ACCOUNT = "jwtAccount"
    const val ACCESS_TOKEN: String = "access_token"
  }

  fun addAccount(accessToken: AccessTokenResponse, jwtManager: JwtManager): Account? {
    val jwtAccount = jwtManager.toJwtAccount(accessToken.accessToken) ?: return null
    val account = Account(jwtAccount.name, ACCOUNT_TYPE)
    get()?.let {
      accountManager.setUserData(it, ACCESS_TOKEN, Gson().toJson(accessToken))
      return it
    }
    return try {
      accountManager.addAccountExplicitly(
        account,
        null,
        bundleOf(
          ACCESS_TOKEN to Gson().toJson(accessToken),
          JWT_ACCOUNT to Gson().toJson(jwtAccount),
        ),
      )
      account
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  private fun get() = accountManager.accounts.firstOrNull { it.type == ACCOUNT_TYPE }

  fun getJwtAccount(): JwtAccount? {
    return get()
      ?.let { account -> accountManager.getUserData(account, JWT_ACCOUNT) }
      ?.run { Gson().fromJson(this, JwtAccount::class.java) }
  }

  fun getAccessToken(): AccessTokenResponse? {
    return get()
      ?.let { account -> accountManager.getUserData(account, ACCESS_TOKEN) }
      ?.run { Gson().fromJson(this, AccessTokenResponse::class.java) }
  }

  fun deleteAccounts() =
    accountManager.accounts.forEach { accountManager.removeAccountExplicitly(it) }
}
