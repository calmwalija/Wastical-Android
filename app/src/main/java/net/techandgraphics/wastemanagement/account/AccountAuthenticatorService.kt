package net.techandgraphics.wastemanagement.account

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AccountAuthenticatorService : Service() {

  override fun onBind(intent: Intent?): IBinder? {
    val authenticator = BaseAccountAuthenticator(this)
    return authenticator.iBinder
  }
}
