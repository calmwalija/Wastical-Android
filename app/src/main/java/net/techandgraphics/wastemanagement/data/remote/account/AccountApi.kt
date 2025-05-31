package net.techandgraphics.wastemanagement.data.remote.account

import net.techandgraphics.wastemanagement.data.remote.account.session.AccountSessionResponse
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenRequest
import net.techandgraphics.wastemanagement.data.remote.account.token.AccountFcmTokenResponse

interface AccountApi {
  suspend fun get(): AccountSessionResponse
  suspend fun fcmToken(request: AccountFcmTokenRequest): AccountFcmTokenResponse
}
