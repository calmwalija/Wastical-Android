package net.techandgraphics.wastemanagement.ui.screen.company.client.browse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.ui.screen.accountWithStreetAndArea4Preview
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



  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(onClick = { onEvent(CompanyBrowseClientListEvent.Goto.BackHandler) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
          }
        },
        modifier = Modifier.shadow(0.dp),
        colors = TopAppBarDefaults.topAppBarColors()
      )
    },

    floatingActionButton = {
      FloatingActionButton(
        onClick = { onEvent(CompanyBrowseClientListEvent.Goto.Create) },
        containerColor = MaterialTheme.colorScheme.primary,
      ) {
        Icon(Icons.Rounded.Add, null)
      }
    }
  ) {
    Column(modifier = Modifier.padding(it)) {

      Text(
        text = "Browse Clients",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp)
      )


      CompanyBrowseClientSearchView(state, onEvent)

      LazyColumn {
        items(state.accounts) { account ->
          CompanyBrowseClientView(account, onEvent)
        }
      }

    }
  }

}


@Preview
@Composable
private fun CompanyBrowseClientScreenPreview() {
  WasteManagementTheme {
    CompanyBrowseClientScreen(
      state = CompanyBrowseClientState(
        accounts = (1..6)
          .map { listOf(accountWithStreetAndArea4Preview) }
          .toList()
          .flatten()
      ),
      channel = flow { },
      onEvent = {}
    )
  }
}
