package net.techandgraphics.wastemanagement.account

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import net.techandgraphics.wastemanagement.ui.activity.MainActivity

open class BaseAccountAuthenticator(private val context: Context) :
  AbstractAccountAuthenticator(context) {

  override fun addAccount(
    response: AccountAuthenticatorResponse,
    accountType: String,
    authTokenType: String?,
    requiredFeatures: Array<String>?,
    options: Bundle?,
  ): Bundle {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
    val bundle = Bundle()
    bundle.putParcelable(AccountManager.KEY_INTENT, intent)
    return bundle
  }

  override fun getAuthToken(
    response: AccountAuthenticatorResponse,
    account: Account,
    authTokenType: String?,
    options: Bundle?,
  ) = bundleOf()

  override fun updateCredentials(
    response: AccountAuthenticatorResponse,
    account: Account,
    authTokenType: String?,
    options: Bundle?,
  ) = bundleOf()

  override fun getAuthTokenLabel(authTokenType: String?) = null

  override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?) =
    bundleOf()

  override fun hasFeatures(
    response: AccountAuthenticatorResponse?,
    account: Account?,
    features: Array<out String?>?,
  ) = bundleOf()

  override fun confirmCredentials(
    response: AccountAuthenticatorResponse,
    account: Account,
    options: Bundle?,
  ) = bundleOf()
}
