package net.techandgraphics.wastical.ui.screen.company.client.browse

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompanyBrowseClientSearchHistoryView(
  state: CompanyBrowseClientState.Success,
  onEvent: (CompanyBrowseClientListEvent) -> Unit,
) {
  Row(
    modifier = Modifier
      .padding(horizontal = 8.dp)
      .horizontalScroll(rememberScrollState())
      .fillMaxWidth()
  ) {
    state.searchHistoryTags.forEach { tag ->
      AssistChip(
        onClick = { onEvent(CompanyBrowseClientListEvent.Button.Tag(tag)) },
        label = { Text(tag.tag) },
        modifier = Modifier.padding(horizontal = 4.dp),
        colors = AssistChipDefaults.assistChipColors(
          containerColor = MaterialTheme.colorScheme.primary.copy(.05f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(.5f))
      )
    }
  }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CompanyBrowseClientSearchHistoryPreview() {
  WasticalTheme {
    CompanyBrowseClientSearchHistoryView(
      state = CompanyBrowseClientState.Success(
        company = company4Preview,
      )
    ) {}
  }
}
