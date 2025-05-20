package net.techandgraphics.wastemanagement.data.remote.dto

import net.techandgraphics.wastemanagement.data.remote.account.AccountResponse
import net.techandgraphics.wastemanagement.data.remote.account.contact.AccountContactResponse

data class AccountWithContacts(
  val account: AccountResponse,
  val contacts: List<AccountContactResponse>,
)
