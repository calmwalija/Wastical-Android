package net.techandgraphics.wastical.ui.screen.client.notification

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.notification4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable
fun ClientNotificationScreen(
  state: ClientNotificationState,
  onEvent: (ClientNotificationEvent) -> Unit,
) {

  when (state) {
    ClientNotificationState.Loading -> LoadingIndicatorView()
    is ClientNotificationState.Success -> Scaffold(
      topBar = {
        CompanyInfoTopAppBarView(state.company) {
          onEvent(ClientNotificationEvent.Button.BackHandler)
        }
      },
    ) {
      LazyColumn(
        contentPadding = it,
        modifier = Modifier.padding(16.dp)
      ) {

        item {
          Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
          )
        }

        items(state.notifications, key = { key -> key.id }) { notification ->
          ClientNotificationItem(
            modifier = Modifier.animateItem(),
            notification = notification
          )
        }

      }
    }
  }
}


@Preview
@Composable
private fun ClientNotificationScreenPreview() {
  WasticalTheme {
    ClientNotificationScreen(
      state = notificationStateSuccess(),
      onEvent = {}
    )
  }
}

fun notificationStateSuccess() = ClientNotificationState.Success(
  company = company4Preview,
  notifications = listOf(notification4Preview)
)
