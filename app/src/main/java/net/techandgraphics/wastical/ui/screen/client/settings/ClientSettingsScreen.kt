package net.techandgraphics.wastical.ui.screen.client.settings

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.BuildConfig
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.ui.HorizontalRuleView
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

          item {
            Text(
              text = state.account.username,
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
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
              ),
              modifier = Modifier.padding(4.dp)
            ) {
              Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                  painterResource(R.drawable.ic_developer),
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
            HorizontalRuleView({})
            Row(modifier = Modifier.padding(16.dp)) {
              Icon(
                painterResource(R.drawable.ic_info),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
              Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = "App Info")
                Text(
                  text = "Version ${BuildConfig.VERSION_NAME}",
                  style = MaterialTheme.typography.bodySmall
                )
              }
            }
            HorizontalRuleView({})
            Row(modifier = Modifier.padding(16.dp)) {
              Icon(
                painterResource(R.drawable.ic_payment),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
              Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = "Payment Plan")
                Text(
                  text = state.plan.fee.toAmount(),
                  style = MaterialTheme.typography.bodySmall
                )
              }
            }
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
                  Switch(
                    checked = state.dynamicColor,
                    onCheckedChange = { isEnabled ->
                      onEvent(ClientSettingsEvent.Button.DynamicColor(isEnabled))
                    }
                  )
                  Spacer(modifier = Modifier.width(16.dp))
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
        contacts = listOf(accountContact4Preview),
      ),
      onEvent = {}
    )
  }
}
