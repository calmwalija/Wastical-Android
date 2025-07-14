package net.techandgraphics.quantcal.ui.screen.client.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable fun ClientHomeSearchView(
  state: ClientHomeState.Success,
  onEvent: (ClientHomeEvent) -> Unit,
) {

  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
    Card(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 8.dp),
      shape = RoundedCornerShape(50),
      colors = CardDefaults.elevatedCardColors(),
      elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
      BasicTextField(
        value = TextFieldValue(state.searchQuery, selection = TextRange(state.searchQuery.length)),
        onValueChange = {

        },
        maxLines = 1,
        modifier = Modifier
          .fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.secondary),
        decorationBox = { innerTextField ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .padding(horizontal = 16.dp, vertical = 12.dp)
          ) {
            Icon(Icons.Default.Search, null)
            Box(
              modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
              contentAlignment = Alignment.CenterStart
            ) {
              innerTextField()
              if (state.searchQuery.isEmpty())
                Text(
                  text = "Search",
                  color = LocalContentColor.current.copy(alpha = 0.5f),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
            }
            AnimatedVisibility(visible = state.searchQuery.isNotEmpty()) {
              IconButton(
                onClick = { },
                modifier = Modifier.size(20.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = null,
                  modifier = Modifier.padding(1.dp),
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }

            Spacer(modifier = Modifier.width(2.dp))
          }
        },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
      )
    }
    Spacer(modifier = Modifier.width(8.dp))
  }
}

@Preview(showBackground = true)
@Composable fun ClientHomeSearchViewPreview() {
  QuantcalTheme {
    ClientHomeSearchView(state = clientHomeStateSuccess()) {
    }
  }
}
