package net.techandgraphics.wastemanagement.ui.screen.company.client.browse

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.accountWithStreetAndArea4Preview
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.theme.Muted
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyBrowseClientScreen(
  state: CompanyBrowseClientState,
  channel: Flow<CompanyBrowseClientChannel>,
  onEvent: (CompanyBrowseClientListEvent) -> Unit,
) {

  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        when (event) {
          else -> {
            TODO()
          }
        }
      }
    }
  }



  when (state) {
    CompanyBrowseClientState.Loading -> LoadingIndicatorView()
    is CompanyBrowseClientState.Success -> {

      var showFilters by remember { mutableStateOf(false) }


      if (showFilters) ModalBottomSheet(onDismissRequest = { showFilters = false }) {
        CompanyBrowseClientSearchFilterView(state, onEvent)
      }


      Scaffold(
        topBar = {
          TopAppBar(
            title = {
              Row(
                modifier = Modifier
                  .padding(end = 16.dp)
                  .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Image(
                  painterResource(R.drawable.im_placeholder),
                  contentDescription = null,
                  modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .size(32.dp)
                )
                Text(
                  text = state.company.name,
                  maxLines = 1,
                  style = MaterialTheme.typography.titleMedium,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            },
            navigationIcon = {
              IconButton(onClick = { onEvent(CompanyBrowseClientListEvent.Goto.BackHandler) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
              }
            },
            actions = {

              if (state.accountRequests.isNotEmpty()) {
                BadgedBox(badge = {
                  Badge {
                    Text(text = state.accountRequests.size.toString())
                  }
                }) {
                  IconButton(onClick = { onEvent(CompanyBrowseClientListEvent.Button.ScheduleUpload) }) {
                    Icon(painterResource(R.drawable.ic_upload_ready), null)
                  }
                }
              }

              IconButton(onClick = {}) {
                Icon(Icons.Default.MoreVert, null)
              }
              Spacer(modifier = Modifier.width(4.dp))
            },
            modifier = Modifier.shadow(0.dp),
            colors = TopAppBarDefaults.topAppBarColors()
          )
        },
      ) {
        Column(modifier = Modifier.padding(it)) {

          Row(
            modifier = Modifier.padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Browse Clients",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp)
              )
              Text(
                text = "Which client are you looking for ?",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                modifier = Modifier.padding(start = 16.dp),
                overflow = TextOverflow.Ellipsis,
                color = Muted
              )
            }

            IconButton(onClick = { onEvent(CompanyBrowseClientListEvent.Goto.Create) }) {
              Icon(
                Icons.Rounded.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }


          Spacer(modifier = Modifier.height(16.dp))

          CompanyBrowseClientSearchView(state) { event ->
            when (event) {
              CompanyBrowseClientListEvent.Button.Filter -> showFilters = true
              else -> onEvent(event)
            }
          }

          if (state.searchHistoryTags.size > 3)
            CompanyBrowseClientSearchHistoryView(state, onEvent)

          LazyColumn {
            items(state.accounts, key = { it.accountId }) { account ->
              CompanyBrowseClientView(account, modifier = Modifier.animateItem(), onEvent)
            }
          }

        }
      }
    }
  }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyBrowseClientScreenPreview() {
  WasteManagementTheme {
    CompanyBrowseClientScreen(
      state = CompanyBrowseClientState.Success(
        accounts = (1..6)
          .map { listOf(accountWithStreetAndArea4Preview) }
          .toList()
          .flatten(),
        company = company4Preview
      ),
      channel = flow { },
      onEvent = {}
    )
  }
}
