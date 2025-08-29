package net.techandgraphics.wastical.ui.screen.client.settings

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import net.techandgraphics.wastical.BuildConfig
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.openDialer
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toast
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.accountContact4Preview
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.demographicArea4Preview
import net.techandgraphics.wastical.ui.screen.demographicStreet4Preview
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme


@Composable
private fun SectionTitle(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 8.dp)
  )
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
  Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(4.dp)) {
    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) { content() }
  }
}

@Composable
private fun SettingToggleRow(
  iconRes: Int,
  title: String,
  subtitle: String,
  checked: Boolean,
  onToggle: (Boolean) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp)
  ) {
    Icon(
      painter = painterResource(iconRes),
      contentDescription = null,
      modifier = Modifier
        .padding(horizontal = 8.dp)
        .size(24.dp),
      tint = MaterialTheme.colorScheme.primary,
    )
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 8.dp)
    ) {
      Text(text = title, style = MaterialTheme.typography.titleSmall)
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
    Switch(checked = checked, onCheckedChange = onToggle)
    Spacer(modifier = Modifier.width(8.dp))
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSettingsScreen(
  state: ClientSettingsState,
  onEvent: (ClientSettingsEvent) -> Unit,
) {

  when (state) {
    ClientSettingsState.Loading -> LoadingIndicatorView()
    is ClientSettingsState.Success -> {

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
            SectionTitle(text = "About")
            SectionCard {
              Row(modifier = Modifier.padding(8.dp)) {
                Icon(
                  painterResource(R.drawable.ic_info),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                  Text(text = "App Info")
                  Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
              HorizontalDivider()
              Row(modifier = Modifier.padding(8.dp)) {
                Icon(
                  painterResource(R.drawable.ic_code),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                  Text(text = "Developer")
                  Text(
                    text = stringResource(R.string.developer),
                    style = MaterialTheme.typography.bodySmall
                  )
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            SectionTitle(text = "Account")
            SectionCard {
              Row(
                modifier = Modifier
                  .clickable { onEvent(ClientSettingsEvent.Goto.Settings) }
                  .fillMaxWidth()
                  .padding(8.dp)) {
                Icon(
                  painterResource(R.drawable.ic_edit_note),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Column(
                  modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
                ) {
                  Text(text = "Edit Info")
                  Text(
                    text = "Update your name, contact and other account details",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
              if (state.account.username.isDigitsOnly()) {
                HorizontalDivider()
                Row(modifier = Modifier.padding(8.dp)) {
                  Icon(
                    Icons.Rounded.Call,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                  )
                  Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(text = "Phone")
                    Text(
                      text = state.account.username,
                      style = MaterialTheme.typography.bodySmall
                    )
                  }
                }
              }
              HorizontalDivider()
              Row(modifier = Modifier.padding(8.dp)) {
                Icon(
                  painterResource(R.drawable.ic_payment),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                  Text(text = "Payment Plan")
                  Text(
                    text = state.plan.fee.toAmount(),
                    style = MaterialTheme.typography.bodySmall
                  )
                }
              }
              HorizontalDivider()
              Row(modifier = Modifier.padding(8.dp)) {
                Icon(
                  painterResource(R.drawable.ic_location),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                  Text(text = "Location")
                  Text(
                    text = "${state.streetName}, ${state.areaName}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
              SectionTitle(text = "Appearance")
              SectionCard {
                SettingToggleRow(
                  iconRes = R.drawable.ic_invert_colors,
                  title = "Dynamic color",
                  subtitle = "Match app colors with your device wallpaper",
                  checked = state.dynamicColor,
                  onToggle = { onEvent(ClientSettingsEvent.Button.DynamicColor(it)) }
                )
              }
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            SectionTitle(text = "Notifications")
            SectionCard {
              SettingToggleRow(
                iconRes = R.drawable.ic_balance,
                title = "Payment reminders",
                subtitle = "Daily reminder when balance is outstanding",
                checked = state.reminderPayment,
                onToggle = { onEvent(ClientSettingsEvent.Button.ReminderPayment(it)) }
              )
              HorizontalDivider()
              SettingToggleRow(
                iconRes = R.drawable.ic_cleaning_bucket,
                title = "Bin collection reminders",
                subtitle = "Reminder on collection day around 05:00",
                checked = state.reminderBin,
                onToggle = { onEvent(ClientSettingsEvent.Button.ReminderBin(it)) }
              )
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            SectionTitle(text = "Contact us")
            SectionCard {
              val context = LocalContext.current
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    state.companyContacts.firstOrNull()?.contact?.let { phone ->
                      context.openDialer(phone)
                    }
                  }
                  .padding(8.dp)) {
                Icon(
                  Icons.Rounded.Phone,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                  Text(text = "Helpline")
                  Text(
                    text = state.companyContacts.firstOrNull()?.contact ?: "No contact set",
                    style = MaterialTheme.typography.bodySmall
                  )
                }
              }
              HorizontalDivider()
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    val intent =
                      Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:${state.company.email}".toUri()
                      }
                    runCatching { context.startActivity(intent) }
                      .onFailure { context.toast("Failed to open email app") }
                  }
                  .padding(8.dp)) {
                Icon(
                  Icons.Outlined.Email,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                  Text(text = "Email")
                  Text(
                    text = state.company.email,
                    style = MaterialTheme.typography.bodySmall
                  )
                }
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
        account = account4Preview,
        company = company4Preview,
        plan = paymentPlan4Preview,
        streetName = demographicStreet4Preview.name,
        areaName = demographicArea4Preview.name,
        contacts = listOf(accountContact4Preview),
      ),
      onEvent = {}
    )
  }
}
