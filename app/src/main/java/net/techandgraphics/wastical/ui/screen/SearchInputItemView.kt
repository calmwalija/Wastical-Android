package net.techandgraphics.wastical.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.ui.theme.WasticalTheme


sealed interface SearchInputItemViewEvent {
  data class InputSearch(val query: String) : SearchInputItemViewEvent
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchInputItemView(
  query: String,
  placeholder: String = "Input search keyword",
  trailingView: @Composable () -> Unit,
  onEvent: (SearchInputItemViewEvent) -> Unit,
) {

  var showLoading by remember { mutableStateOf(false) }
  var showLoadingJob: Job? = Job()

  LaunchedEffect(query) {
    showLoadingJob?.cancel()
    showLoading = true
    showLoadingJob = this.launch {
      delay(1000)
      showLoading = false
    }
  }


  Row(verticalAlignment = Alignment.CenterVertically) {
    Card(
      modifier = Modifier
        .weight(1f)
        .padding(vertical = 8.dp),
      shape = CircleShape
    ) {
      BasicTextField(
        value = query,
        onValueChange = { onEvent(SearchInputItemViewEvent.InputSearch(it)) },
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.secondary),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Text,
          autoCorrectEnabled = false,
          capitalization = KeyboardCapitalization.Sentences,
          imeAction = ImeAction.Done
        ),
        decorationBox = { innerTextField ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .padding(start = 16.dp, end = 4.dp)
              .padding(vertical = 4.dp)
          ) {
            Box(modifier = Modifier.size(24.dp)) {
              if (showLoading) LoadingIndicator(modifier = Modifier.scale(1.4f)) else {
                Icon(
                  painter = painterResource(R.drawable.ic_outline_search),
                  contentDescription = null,
                  modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
              contentAlignment = Alignment.CenterStart
            ) {
              innerTextField()
              if (query.isEmpty())
                Text(
                  text = placeholder,
                  color = LocalContentColor.current.copy(alpha = 0.5f),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.padding(vertical = 1.dp)
                )
            }
            AnimatedVisibility(visible = query.isNotEmpty()) {
              IconButton(
                onClick = { onEvent(SearchInputItemViewEvent.InputSearch("")) },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }
            trailingView()
          }
        },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
      )
    }
  }
}

@Preview
@Composable
fun SearchInputItemPreview() {
  WasticalTheme {
    SearchInputItemView(
      query = "",
      trailingView = {},
      onEvent = {}
    )
  }
}
