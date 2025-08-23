package net.techandgraphics.wastical.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun VerticalScrollbar(
  listState: LazyListState,
  modifier: Modifier = Modifier,
  autoHideMillis: Long = 2000,
) {

  val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }
  val totalItems = layoutInfo.totalItemsCount
  if (totalItems <= 0) return

  val density = LocalDensity.current
  val viewportHeightPx = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset).toFloat()
    .coerceAtLeast(1f)
  val visibleItems = layoutInfo.visibleItemsInfo
  val avgItemSizePx = if (visibleItems.isNotEmpty())
    visibleItems.sumOf { it.size }.toFloat() / visibleItems.size
  else 1f
  val totalContentHeightPx = (avgItemSizePx * totalItems).coerceAtLeast(viewportHeightPx)

  val firstIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
  val firstOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
  val scrolledPx = firstIndex * avgItemSizePx + firstOffset

  val minThumbPx = with(density) { 24.dp.toPx() }
  val verticalPaddingPx = with(density) { 8.dp.toPx() }
  val effectiveTrackHeightPx = (viewportHeightPx - (verticalPaddingPx * 2)).coerceAtLeast(1f)
  val thumbHeightPx = (effectiveTrackHeightPx * (viewportHeightPx / totalContentHeightPx))
    .coerceIn(minThumbPx, effectiveTrackHeightPx)
  val maxThumbOffset = (effectiveTrackHeightPx - thumbHeightPx).coerceAtLeast(0f)
  val scrollablePx = (totalContentHeightPx - viewportHeightPx).coerceAtLeast(1f)
  val thumbOffsetPx = (scrolledPx / scrollablePx) * maxThumbOffset

  val scope = rememberCoroutineScope()
  var dragYPx by remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
  var isVisible by remember { mutableStateOf(false) }
  var hideJob by remember { mutableStateOf<Job?>(null) }

  val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }

  LaunchedEffect(isScrolling) {
    if (isScrolling) {
      isVisible = true
      hideJob?.cancel()
    } else {
      hideJob?.cancel()
      hideJob = scope.launch {
        delay(autoHideMillis)
        isVisible = false
      }
    }
  }

  fun scrollToByY(yPx: Float, animate: Boolean) {
    val progress = (yPx / effectiveTrackHeightPx).coerceIn(0f, 1f)
    val targetIndex = (progress * (totalItems - 1)).roundToInt().coerceAtLeast(0)
    scope.launch {
      if (animate) listState.animateScrollToItem(targetIndex) else listState.scrollToItem(
        targetIndex
      )
    }
  }

  Box(
    modifier = modifier
      .padding(end = 8.dp)
      .fillMaxHeight()
  ) {
    if (isVisible) {
      Box(
        modifier = Modifier
          .width(8.dp)
          .fillMaxHeight()
          .padding(vertical = 8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f))
          .pointerInput(totalItems) {
            detectTapGestures { offset ->
              isVisible = true
              hideJob?.cancel()
              scrollToByY(offset.y, animate = true)
              hideJob = scope.launch {
                delay(autoHideMillis)
                isVisible = false
              }
            }
          }
          .pointerInput(totalItems, effectiveTrackHeightPx) {
            detectDragGestures(
              onDragStart = { offset ->
                isVisible = true
                hideJob?.cancel()
                dragYPx = offset.y
                scrollToByY(dragYPx, animate = false)
              },
              onDrag = { _, dragAmount ->
                dragYPx += dragAmount.y
                scrollToByY(dragYPx, animate = false)
              },
              onDragEnd = {
                scrollToByY(dragYPx, animate = true)
                hideJob?.cancel()
                hideJob = scope.launch {
                  delay(autoHideMillis)
                  isVisible = false
                }
              }
            )
          }
      ) {
        Box(
          modifier = Modifier
            .align(Alignment.TopCenter)
            .width(6.dp)
            .offset(y = with(density) { thumbOffsetPx.toDp() })
            .height(with(density) { thumbHeightPx.toDp() })
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
        )
      }
    }
  }
}
