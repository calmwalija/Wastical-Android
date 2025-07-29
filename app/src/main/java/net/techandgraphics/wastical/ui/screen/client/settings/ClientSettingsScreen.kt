package net.techandgraphics.wastical.ui.screen.client.settings

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toPhoneFormat
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.accountContact4Preview
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ClientSettingsScreen(
  state: ClientSettingsState,
  onEvent: (ClientSettingsEvent) -> Unit,
) {

  when (state) {
    ClientSettingsState.Loading -> LoadingIndicatorView()
    is ClientSettingsState.Success -> {

      LocalContext.current

      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(ClientSettingsEvent.Button.BackHandler)
          }
        },
      ) {

        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp),
        ) {

          item {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
              Image(
                painterResource(R.drawable.im_placeholder),
                contentDescription = null,
                modifier = Modifier
                  .padding(bottom = 24.dp)
                  .clip(CircleShape)
                  .size(160.dp)
              )
            }
          }

          item {
            Text(
              text = state.account.toFullName(),
              style = MaterialTheme.typography.headlineSmall,
              maxLines = 1,
              overflow = TextOverflow.MiddleEllipsis,
              color = MaterialTheme.colorScheme.primary,
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth()
            )
          }

          state.account.email?.let { email ->
            item {
              Text(
                text = email,
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )
            }
          }

          items(state.contacts) { contact ->
            Text(
              text = contact.contact.toPhoneFormat(),
              maxLines = 1,
              overflow = TextOverflow.MiddleEllipsis,
              style = MaterialTheme.typography.bodyMedium,
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth()
            )
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Card(
              onClick = { onEvent(ClientSettingsEvent.Goto.Settings) },
              shape = CircleShape,
              modifier = Modifier.padding(4.dp)
            ) {
              Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                  painterResource(R.drawable.ic_edit_note),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = "Edit Info",
                  modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                )
                Icon(
                  Icons.AutoMirrored.Filled.KeyboardArrowRight,
                  contentDescription = null
                )
              }
            }
          }


          item {
            Card(
              shape = CircleShape,
              modifier = Modifier.padding(4.dp)
            ) {
              Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                  painterResource(R.drawable.ic_info),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = "About App",
                  modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                )
                Icon(
                  Icons.AutoMirrored.Filled.KeyboardArrowRight,
                  contentDescription = null
                )
              }
            }
          }

          item {
            Card(
              shape = CircleShape,
              modifier = Modifier.padding(4.dp)
            ) {
              Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                  painterResource(R.drawable.ic_font_face),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = "Font Family",
                  modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                )
                Icon(
                  Icons.AutoMirrored.Filled.KeyboardArrowRight,
                  contentDescription = null
                )
              }
            }
          }

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            item {
              Card(
                shape = CircleShape,
                modifier = Modifier.padding(4.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(4.dp)
                ) {
                  Icon(
                    painter = painterResource(R.drawable.ic_invert_colors),
                    contentDescription = null,
                    modifier = Modifier
                      .padding(horizontal = 8.dp)
                      .padding(start = 4.dp)
                      .size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                  )
                  Text(
                    text = "Theme Color",
                    style = MaterialTheme.typography.titleMedium,

                    modifier = Modifier
                      .padding(horizontal = 8.dp)
                      .weight(1f)
                  )
                  Switch(false, onCheckedChange = {})
                  Spacer(modifier = Modifier.width(16.dp))
                }
              }
            }
          }


          item {
            Card(
              shape = CircleShape,
              modifier = Modifier.padding(4.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Outlined.Notifications,
                  contentDescription = null,
                  modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(start = 4.dp)
                    .size(24.dp),
                  tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                  text = "Notifications",
                  style = MaterialTheme.typography.titleMedium,

                  modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
                )
                Switch(false, onCheckedChange = {})
                Spacer(modifier = Modifier.width(16.dp))
              }
            }

          }

        }
      }
    }
  }
}


@Preview
@Composable
private fun ClientSettingsScreenPreview() {
  WasticalTheme {
    ClientSettingsScreen(
      state = ClientSettingsState.Success(
        company = company4Preview,
        contacts = listOf(accountContact4Preview),
        account = account4Preview,
        plan = paymentPlan4Preview
      ),
      onEvent = {}
    )
  }
}
