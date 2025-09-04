package net.techandgraphics.wastical.ui.screen.auth.phone.load

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable
fun LoadScreen(
  state: LoadState,
  channel: Flow<LoadChannel>,
  onEvent: (LoadEvent) -> Unit,
) {
  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
  var errorMessage by remember { mutableStateOf<String?>(null) }
  if (state is LoadState.Success) {
    LaunchedEffect(key1 = channel) {
      lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        channel.collect { event ->
          when (event) {
            is LoadChannel.Error -> {
              val msg = event.error.message ?: "Network error. Please check your connection."
              errorMessage = msg
            }

            is LoadChannel.Success -> onEvent(LoadEvent.Success(state.account!!))
            LoadChannel.NoAccount -> onEvent(LoadEvent.NoAccount)
            is LoadChannel.NoToken -> onEvent(LoadEvent.NoToken(event.contact))
            is LoadChannel.Otp -> onEvent(LoadEvent.Goto.Otp(event.contact))
          }
        }
      }
    }
  }
  if (errorMessage == null) LoadingIndicatorView() else {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
          imageVector = Icons.Outlined.Close,
          contentDescription = null,
          modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.error)
            .size(72.dp)
            .padding(16.dp),
          tint = MaterialTheme.colorScheme.errorContainer,
        )
        Text(
          text = "Connection issue",
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = errorMessage!!,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          Button(
            onClick = {
              errorMessage = null
              onEvent(LoadEvent.Load)
            },
            modifier = Modifier.fillMaxWidth(.7f)
          ) { Text("Retry") }
        }
      }
    }
  }
}


@Preview
@Composable
private fun LoadScreenPreview() {
  WasticalTheme {
    LoadScreen(
      state = LoadState.Loading,
      channel = flow { }
    ) {}
  }
}
