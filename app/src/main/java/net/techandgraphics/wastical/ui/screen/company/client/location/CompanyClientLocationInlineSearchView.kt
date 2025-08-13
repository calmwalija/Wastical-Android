package net.techandgraphics.wastical.ui.screen.company.client.location

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable
fun CompanyClientLocationInlineSearchView(
  query: String,
  onQueryChange: (String) -> Unit,
) {
  Row(modifier = Modifier.padding(horizontal = 8.dp)) {
    OutlinedTextField(
      value = query,
      onValueChange = onQueryChange,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      singleLine = true,
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp))
      },
      trailingIcon = {
        if (query.isNotEmpty()) {
          IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Clear, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          }
        }
      },
      placeholder = { Text(text = "Search locations") },
      textStyle = MaterialTheme.typography.bodyMedium
    )
  }
}

@Preview
@Composable
private fun CompanyClientLocationInlineSearchViewPreview() {
  WasticalTheme {
    CompanyClientLocationInlineSearchView(query = "", onQueryChange = {})
  }
}
