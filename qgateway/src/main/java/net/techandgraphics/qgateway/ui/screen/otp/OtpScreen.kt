package net.techandgraphics.qgateway.ui.screen.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.qgateway.domain.model.AccountUiModel
import net.techandgraphics.qgateway.domain.model.AccountWithOtpUiModel
import net.techandgraphics.qgateway.domain.model.OtpUiModel
import net.techandgraphics.qgateway.ui.LoadingIndicatorView
import net.techandgraphics.qgateway.ui.theme.QuantcalTheme
import java.time.ZonedDateTime

@Composable
fun OtpScreen(
  state: OtpState,
) {

  when (state) {
    OtpState.Loading -> LoadingIndicatorView()
    is OtpState.Success -> {
      Scaffold { contentPadding ->
        LazyColumn(
          contentPadding = contentPadding,
          verticalArrangement = Arrangement.spacedBy(16.dp),
          modifier = Modifier.padding(vertical = 32.dp)
        ) {

          if (state.accountWithOtps.isEmpty()) item { LoadingIndicatorView() }

          items(state.accountWithOtps, key = { it.otp.id }) { accountWithOtp ->
            OtpItem(
              modifier = Modifier.animateItem(),
              accountWithOtp = accountWithOtp
            )
          }
        }
      }
    }
  }

}


@Preview
@Composable
private fun OtpScreenPreview() {
  QuantcalTheme {
    OtpScreen(state = otpStateSuccess())
  }
}


fun otpStateSuccess() = OtpState.Success(
  accountWithOtps =
    (1..3)
      .map { AccountWithOtpUiModel(account, opt.copy(id = it.toLong())) }
)


private val opt = OtpUiModel(
  id = 1,
  otp = 1234,
  sent = true,
  contact = "999112233",
  accountId = 1,
  createdAt = ZonedDateTime.now().toEpochSecond(),
  updatedAt = ZonedDateTime.now().toEpochSecond(),
  sentAt = ZonedDateTime.now().toEpochSecond(),
)

private val account = AccountUiModel(
  id = 1L,
  uuid = "uuid",
  title = "Mr",
  firstname = "John",
  lastname = "Doe",
  username = "999112233",
  email = "example@mail.com",
  updatedAt = ZonedDateTime.now().toEpochSecond(),
  createdAt = ZonedDateTime.now().toEpochSecond()
)
