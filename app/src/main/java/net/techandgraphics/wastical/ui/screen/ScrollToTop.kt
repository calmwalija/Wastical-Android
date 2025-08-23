package net.techandgraphics.wastical.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun ScrollToTopView(
  listState: LazyListState,
  coroutineScope: CoroutineScope,
  showScrollToTop: Boolean,
  modifier: Modifier = Modifier,
) {
  if (showScrollToTop) {
    Box(modifier = modifier.padding(16.dp)) {
      IconButton(
        onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } },
        colors = IconButtonDefaults.iconButtonColors(
          containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.scale(1.3f)
      ) {
        Icon(
          imageVector = Icons.Rounded.KeyboardArrowUp,
          contentDescription = "Scroll to top"
        )
      }
    }
  }
}
