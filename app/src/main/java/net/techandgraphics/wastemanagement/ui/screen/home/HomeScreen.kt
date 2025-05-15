package net.techandgraphics.wastemanagement.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  state: HomeState,
  channel: Flow<HomeChannel>,
  onEvent: (HomeEvent) -> Unit
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
        title = {
        },
        actions = {
          IconButton(onClick = { }) {
            BadgedBox(badge = { Badge() }) {
              Icon(Icons.Outlined.Notifications, null)
            }
          }

          IconButton(onClick = {}) {
            Icon(Icons.Default.MoreVert, null)
          }

        },
        modifier = Modifier.shadow(0.dp),
        colors = TopAppBarDefaults.topAppBarColors()
      )
    }
  ) {
    Column(
      modifier = Modifier
        .padding(it)
        .padding(horizontal = 16.dp)
        .fillMaxWidth(),
    ) {


      Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
          painterResource(R.drawable.ic_launcher_background), null,
          modifier = Modifier
            .clip(CircleShape)
            .size(42.dp)
        )
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
          Text(
            text = "Good Afternoon",
            style = MaterialTheme.typography.bodySmall,
          )
          Text(
            text = "Dr. James Mike Jn",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
          )
        }
      }


      Spacer(modifier = Modifier.height(24.dp))

      HomePaymentView(state) { }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Quick Actions",
        modifier = Modifier.padding(8.dp),
        fontWeight = FontWeight.Bold,
      )

      LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(count = 4) {
          HomeQuickActionView(onEvent = onEvent)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Recent Transactions",
        modifier = Modifier.padding(8.dp),
        fontWeight = FontWeight.Bold,
      )

      HomeTransactionView { }


    }
  }


}


@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
  WasteManagementTheme {
    HomeScreen(
      state = HomeState(),
      channel = flow { },
      onEvent = {}
    )
  }
}
