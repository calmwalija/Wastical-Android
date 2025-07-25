package net.techandgraphics.quantcal.ui.screen.auth.phone.load

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.quantcal.toast
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView

@Composable
fun LoadScreen(
  state: LoadState,
  channel: Flow<LoadChannel>,
  onEvent: (LoadEvent) -> Unit,
) {
  val context = LocalContext.current
  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
  if (state is LoadState.Success) {
    LaunchedEffect(key1 = channel) {
      lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        channel.collect { event ->
          when (event) {
            is LoadChannel.Error -> context.toast(event.error.message)
            is LoadChannel.Success -> onEvent(LoadEvent.Success(state.account!!))
            LoadChannel.NoAccount -> onEvent(LoadEvent.NoAccount)
          }
        }
      }
    }
  }
  LoadingIndicatorView()
}


@Preview
@Composable
private fun LoadScreenPreview() {
  LoadScreen(
    state = LoadState.Loading,
    channel = flow { }
  ) {}
}
