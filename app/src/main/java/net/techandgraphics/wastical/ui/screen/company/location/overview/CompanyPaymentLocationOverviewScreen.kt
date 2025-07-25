package net.techandgraphics.wastical.ui.screen.company.location.overview


import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.capitalize
import net.techandgraphics.wastical.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.wastical.data.local.database.dashboard.payment.AccountSortOrder
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.getToday
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toKwacha
import net.techandgraphics.wastical.toWords
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.accountWithPaymentStatus4Preview
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company.home.AnimatedNumberCounter
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocation4Preview
import net.techandgraphics.wastical.ui.screen.demographicArea4Preview
import net.techandgraphics.wastical.ui.screen.demographicStreet4Preview
import net.techandgraphics.wastical.ui.theme.Muted
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import java.time.Month


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPaymentLocationOverviewScreen(
  state: CompanyPaymentLocationOverviewState,
  onEvent: (CompanyPaymentLocationOverviewEvent) -> Unit,
) {

  when (state) {
    CompanyPaymentLocationOverviewState.Loading -> LoadingIndicatorView()
    is CompanyPaymentLocationOverviewState.Success -> {

      var showSortBy by remember { mutableStateOf(false) }

      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyPaymentLocationOverviewEvent.Button.BackHandler)
          }
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 8.dp)
        ) {

          item {
            Row(
              modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(
                modifier = Modifier
                  .padding(end = 18.dp)
                  .weight(1f)
              ) {
                Text(
                  text = state.demographicStreet.name,
                  style = MaterialTheme.typography.titleLarge,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = state.demographicArea.name,
                  style = MaterialTheme.typography.bodyMedium,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  color = Muted
                )
              }

              FilledIconButton(
                onClick = { onEvent(CompanyPaymentLocationOverviewEvent.Button.ClientCreate(state.companyLocation.id)) },
                shape = RoundedCornerShape(16),
              ) {
                Icon(
                  painter = painterResource(R.drawable.ic_person_add),
                  contentDescription = null,
                  modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp)
                )
              }

              FilledIconButton(
                onClick = { showSortBy = true },
                shape = RoundedCornerShape(16),
                colors = IconButtonDefaults.iconButtonColors(
                  containerColor = CardDefaults.elevatedCardColors().containerColor
                )
              ) {
                Icon(
                  painterResource(R.drawable.ic_filter),
                  contentDescription = null,
                  modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
                )
                DropdownMenu(showSortBy, onDismissRequest = { showSortBy = false }) {
                  AccountSortOrder.entries.forEach { sortBy ->
                    DropdownMenuItem(
                      text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = sortBy.name,
                            modifier = Modifier.padding(end = 16.dp),
                            color = if (state.sortBy == sortBy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                          )
                          if (state.sortBy == sortBy)
                            Icon(
                              Icons.Rounded.CheckCircle,
                              contentDescription = null,
                              tint = MaterialTheme.colorScheme.primary,
                              modifier = Modifier.size(18.dp)
                            )
                        }
                      },
                      enabled = state.sortBy != sortBy,
                      onClick = {
                        onEvent(CompanyPaymentLocationOverviewEvent.Button.SortBy(sortBy))
                        showSortBy = false
                      })
                  }

                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          item { CompanyLocationOverviewItem(state = state) }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          items(state.accounts, key = { it.account.id }) { entity ->
            CompanyPaymentLocationClientView(
              entity = entity,
              modifier = Modifier.animateItem(),
              onEvent
            )
          }
        }

      }
    }
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyLocationOverviewItem(
  state: CompanyPaymentLocationOverviewState.Success,
) {

  var targetValue by remember { mutableFloatStateOf(0f) }
  val monthName = Month.of(state.monthYear.month).name.capitalize()
  var showMenuItems by remember { mutableStateOf(false) }

  val animateAsFloat by animateFloatAsState(
    targetValue = targetValue,
    animationSpec = tween(durationMillis = 7_000)
  )

  LaunchedEffect(state.payment4CurrentMonth.totalPaidAccounts) {
    showMenuItems = false
    targetValue = state.payment4CurrentMonth.totalPaidAccounts
      .toFloat()
      .div(state.accounts.size)
      .coerceIn(0f, 1f)
  }

  Card(
    modifier = Modifier
      .padding(8.dp)
      .fillMaxWidth(),
    colors = CardDefaults.elevatedCardColors()
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = monthName,
            style = MaterialTheme.typography.titleSmall,
          )
          AnimatedNumberCounter(
            theNumber = state.payment4CurrentMonth.totalPaidAmount,
            theStyle = MaterialTheme.typography.headlineMedium,
            theColor = MaterialTheme.colorScheme.onSurfaceVariant
          )
          AnimatedContent(
            state.payment4CurrentMonth.totalPaidAmount.toWords().toKwacha().capitalize(),
            transitionSpec = {
              (slideInVertically { height -> height } + fadeIn())
                .togetherWith(slideOutVertically { height -> -height } + fadeOut())
            }
          ) {
            Text(
              text = it,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.MiddleEllipsis
            )
          }
        }
      }


      LinearProgressIndicator(
        progress = { animateAsFloat },
        modifier = Modifier
          .padding(vertical = 8.dp)
          .fillMaxWidth()
          .height(8.dp)
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "${(animateAsFloat * 100).toInt()}% of expected",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.align(Alignment.End)
      )

      Spacer(modifier = Modifier.height(32.dp))

      FlowRow(
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.Absolute.Center
      ) {
        Column(
          modifier = Modifier
            .weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Expected",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = state.expectedAmountToCollect.toAmount(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
          )
        }
        Column(
          modifier = Modifier
            .weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Outstanding",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = state.expectedAmountToCollect
              .minus(state.payment4CurrentMonth.totalPaidAmount)
              .toAmount(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }

        Column(
          modifier = Modifier
            .weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Clients",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text(
            text = state.accounts.size.toString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }
  }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyPaymentLocationOverviewScreenPreview() {
  WasticalTheme {
    CompanyPaymentLocationOverviewScreen(
      state = companyPaymentLocationOverviewStateSuccess(),
      onEvent = {}
    )
  }
}

fun companyPaymentLocationOverviewStateSuccess() =
  CompanyPaymentLocationOverviewState.Success(
    company = company4Preview,
    demographicStreet = demographicStreet4Preview,
    demographicArea = demographicArea4Preview,
    accounts = listOf(accountWithPaymentStatus4Preview),
    payment4CurrentMonth = Payment4CurrentMonth(120, 935_000),
    expectedAmountToCollect = 3_000,
    companyLocation = companyLocation4Preview,
    monthYear = MonthYear(getToday().month, getToday().year)
  )
