package net.techandgraphics.wastemanagement.ui.screen.company.client.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import androidx.compose.runtime.LaunchedEffect

@Composable
fun CompanyClientLocationScreen(
  state: CompanyClientLocationState,
  channel: Flow<CompanyClientLocationChannel>,
  onEvent: (CompanyClientLocationEvent) -> Unit,
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


}


@Preview
@Composable
private fun CompanyClientLocationScreenPreview() {
  CompanyClientLocationScreen(
    state = CompanyClientLocationState(),
    channel = flow { },
    onEvent = {}
  )
}
