package net.techandgraphics.quantcal.ui.screen.company

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyInfoTopAppBarView(
  company: CompanyUiModel,
  navActions: @Composable () -> Unit = {},
  onBackHandler: () -> Unit,
) {
  TopAppBar(
    title = {
      Row(
        modifier = Modifier
          .padding(end = 16.dp)
          .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Image(
          painterResource(R.drawable.im_placeholder),
          contentDescription = null,
          modifier = Modifier
            .padding(end = 8.dp)
            .clip(CircleShape)
            .size(32.dp)
        )
        Text(
          text = company.name,
          maxLines = 1,
          style = MaterialTheme.typography.titleMedium,
          overflow = TextOverflow.Ellipsis,
        )
      }
    },
    navigationIcon = {
      IconButton(onClick = onBackHandler) {
        Icon(
          Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = null
        )
      }
    },
    actions = { navActions() },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
  )

}
