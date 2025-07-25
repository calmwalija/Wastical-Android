package net.techandgraphics.wastical.ui.screen.company.location.browse

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompanyPaymentPerLocationSearchView(
  state: CompanyBrowseLocationState.Success,
  onEvent: (CompanyBrowseLocationEvent) -> Unit,
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Card(
      modifier = Modifier
        .weight(1f)
        .padding(8.dp),
      colors = CardDefaults.elevatedCardColors(),
    ) {
      BasicTextField(
        value = state.query,
        onValueChange = { onEvent(CompanyBrowseLocationEvent.Input.Search(it)) },
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.secondary),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Text,
          autoCorrectEnabled = false,
          capitalization = KeyboardCapitalization.Sentences
        ),
        decorationBox = { innerTextField ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .padding(horizontal = 16.dp, vertical = 12.dp)
          ) {

            Box(
              modifier = Modifier.size(20.dp),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                  .size(20.dp)
                  .padding(1.dp)
              )
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
              contentAlignment = Alignment.CenterStart
            ) {
              innerTextField()
              if (state.query.isEmpty())
                Text(
                  text = "Input location keyword",
                  color = LocalContentColor.current.copy(alpha = 0.5f),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.padding(vertical = 1.dp)
                )
            }
            AnimatedVisibility(visible = state.query.isNotEmpty()) {
              IconButton(
                onClick = { onEvent(CompanyBrowseLocationEvent.Button.Clear) },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = null,
                  modifier = Modifier.padding(4.dp),
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

@Preview
@Composable
fun CompanyPaymentPerLocationSearchPreview() {
  WasticalTheme {
    CompanyPaymentPerLocationSearchView(
      state = companyBrowseLocationStateSuccess(),
      onEvent = {}
    )
  }
}
