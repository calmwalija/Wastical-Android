package net.techandgraphics.wastical.ui.screen.company.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.domain.model.NotificationUiModel
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SearchInputItemView
import net.techandgraphics.wastical.ui.screen.SearchInputItemViewEvent
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.notification4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CompanyNotificationScreen(
  state: CompanyNotificationState,
  onEvent: (CompanyNotificationEvent) -> Unit,
) {
  when (state) {
    CompanyNotificationState.Loading -> LoadingIndicatorView()
    is CompanyNotificationState.Success -> Scaffold(
      topBar = {
        CompanyInfoTopAppBarView(state.company) {
          onEvent(CompanyNotificationEvent.Button.BackHandler)
        }
      },
    ) {
      var selectedFilter by remember { mutableStateOf(NotificationFilter.All) }

      val filtered = remember(state.notifications, selectedFilter) {
        when (selectedFilter) {
          NotificationFilter.All -> state.notifications
          NotificationFilter.Unread -> state.notifications.filter { it.isRead.not() }
          NotificationFilter.Payment -> state.notifications.filter { it.type.isPaymentRelated() }
          NotificationFilter.Reminder -> state.notifications.filter { it.type.isReminder() }
          NotificationFilter.Alert -> state.notifications.filter { it.type.isAlert() }
        }
      }

      val grouped = remember(filtered) {
        filtered.groupBy { it.createdAt.localDateLabel() }
          .toSortedMap(compareByDescending { it.dateOrderKey() })
      }

      LazyColumn(
        contentPadding = it,
        modifier = Modifier.padding(16.dp)
      ) {


        item {
          Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
          )
        }

        item {
          SearchInputItemView(
            query = state.query,
            trailingView = {
              var showSortBy by remember { mutableStateOf(false) }

              Row {
                IconButton(
                  onClick = { showSortBy = true },
                  colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(.2f)
                  ),
                ) {
                  Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = null,
                  )
                  DropdownMenu(
                    expanded = showSortBy,
                    onDismissRequest = { showSortBy = false }) {
                    DropdownMenuItem(
                      text = { Text("Newest") },
                      enabled = !state.sortDesc,
                      trailingIcon = {
                        if (state.sortDesc)
                          Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                          )
                      },
                      onClick = {
                        showSortBy = false
                      })

                    DropdownMenuItem(
                      text = { Text("Oldest") },
                      trailingIcon = {
                        if (!state.sortDesc)
                          Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                          )
                      },
                      enabled = state.sortDesc,
                      onClick = {
                        showSortBy = false
                      })
                  }
                }
              }

            },
            onEvent = { event ->
              when (event) {
                is SearchInputItemViewEvent.InputSearch -> {

                }
              }
            }
          )
        }

        grouped.forEach { (section, itemsInSection) ->
          item(key = "header_${section}") {
            SectionHeader(title = section)
          }

          items(itemsInSection, key = { key -> key.id }) { notification ->
            CompanyNotificationItem(
              modifier = Modifier.animateItem(),
              notification = notification
            )
          }
        }

      }
    }
  }
}

private enum class NotificationFilter { All, Unread, Payment, Reminder, Alert }

@Composable
private fun FilterChipsRow(
  selected: NotificationFilter,
  onSelect: (NotificationFilter) -> Unit,
) {
  val scroll = rememberScrollState()
  Row(
    modifier = Modifier
      .horizontalScroll(scroll),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    listOf(
      NotificationFilter.All to "All",
      NotificationFilter.Unread to "Unread",
      NotificationFilter.Payment to "Payments",
      NotificationFilter.Reminder to "Reminders",
      NotificationFilter.Alert to "Alerts",
    ).forEach { (value, label) ->
      AssistChip(
        onClick = { onSelect(value) },
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
          containerColor = if (selected == value) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(
            alpha = 0.5f
          ),
          labelColor = if (selected == value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        ),
        border = null
      )
    }
  }
}

@Composable
private fun KpiHeader(notifications: List<NotificationUiModel>) {
  val total = notifications.size
  val unread = notifications.count { it.isRead.not() }
  val last7Days = notifications.count { it.createdAt >= sevenDaysAgoEpochSeconds() }
  val payment = notifications.count { it.type.isPaymentRelated() }
  val alerts = notifications.count { it.type.isAlert() }
  val reminders = notifications.count { it.type.isReminder() }

  Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    KpiBadge(
      modifier = Modifier.weight(1f),
      title = "Total",
      value = total
    )
    KpiBadge(
      modifier = Modifier.weight(1f),
      title = "Unread",
      value = unread, highlight = true
    )
    KpiBadge(
      modifier = Modifier.weight(1f),
      title = "Last 7d",
      value = last7Days
    )
  }

  Row(
    modifier = Modifier.padding(top = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    KpiPill(label = "Payments", value = payment, color = MaterialTheme.colorScheme.primary)
    KpiPill(label = "Alerts", value = alerts, color = MaterialTheme.colorScheme.error)
    KpiPill(label = "Reminders", value = reminders, color = MaterialTheme.colorScheme.tertiary)
  }
}

@Composable
private fun KpiBadge(
  modifier: Modifier = Modifier,
  title: String, value: Int, highlight: Boolean = false,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(
        if (highlight) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      )
      .padding(horizontal = 12.dp, vertical = 10.dp),
    verticalArrangement = Arrangement.spacedBy(2.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value.toString(),
      style = MaterialTheme.typography.titleMedium,
      color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    )
  }
}

@Composable
private fun KpiPill(label: String, value: Int, color: Color) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(50))
      .background(color.copy(alpha = 0.12f))
      .padding(horizontal = 10.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(50))
        .background(color)
        .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
      Text(
        text = value.toString(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onPrimary
      )
    }
    Text(text = label, style = MaterialTheme.typography.labelLarge, color = color)
  }
}

@Composable
private fun Trend7Days(notifications: List<NotificationUiModel>) {
  val today = LocalDate.now()
  val counts = (6 downTo 0).map { offset ->
    val day = today.minusDays(offset.toLong())
    val start = day.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
    val end = day.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
    notifications.count { it.createdAt in start until end }
  }
  val max = (counts.maxOrNull() ?: 1).coerceAtLeast(1)

  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    counts.forEach { c ->
      val h = if (max == 0) 4.dp else (8 + (48 * c / max)).dp
      Box(
        modifier = Modifier
          .width(12.dp)
          .height(56.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
      ) {
        Box(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .width(12.dp)
            .height(h)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primary)
        )
      }
    }
  }
}

@Composable
private fun SectionHeader(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.titleSmall,
    color = MaterialTheme.colorScheme.primary,
    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
  )
}

private fun NotificationType.isPaymentRelated(): Boolean = when (this) {
  NotificationType.PROOF_OF_PAYMENT_SUBMITTED,
  NotificationType.PROOF_OF_PAYMENT_RECEIVED,
  NotificationType.PROOF_OF_PAYMENT_APPROVED,
  NotificationType.PROOF_OF_PAYMENT_DECLINED,
  NotificationType.PROOF_OF_PAYMENT_SUBMITTED_BY_COMPANY,
  NotificationType.PROOF_OF_PAYMENT_SUCCESSFUL_BY_COMPANY,
  NotificationType.PROOF_OF_PAYMENT_COMPANY_VERIFY,
    -> true

  else -> false
}

private fun NotificationType.isReminder(): Boolean = when (this) {
  NotificationType.WASTE_COLLECTION_REMINDER -> true
  else -> false
}

private fun NotificationType.isAlert(): Boolean = when (this) {
  NotificationType.SERVICE_DELAY,
  NotificationType.ACCOUNT_SUSPENDED,
  NotificationType.LOGIN_ALERT,
  NotificationType.GROUP_ALERT,
    -> true

  else -> false
}

private fun Long.localDateLabel(): String {
  val date = Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).toLocalDate()
  val today = LocalDate.now()
  val formatter = DateTimeFormatter.ofPattern("EEE, MMM d")
  return when (date) {
    today -> "Today"
    today.minusDays(1) -> "Yesterday"
    else -> formatter.format(date)
  }
}

private fun String.dateOrderKey(): Long {
  return when (this) {
    "Today" -> Long.MAX_VALUE
    "Yesterday" -> Long.MAX_VALUE - 1
    else -> try {
      val parsed = LocalDate.parse(this, DateTimeFormatter.ofPattern("EEE, MMM d"))
      parsed.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
    } catch (_: Throwable) {
      0L
    }
  }
}

private fun sevenDaysAgoEpochSeconds(): Long =
  LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()

@Preview
@Composable
private fun CompanyNotificationScreenPreview() {
  WasticalTheme {
    CompanyNotificationScreen(
      state = CompanyNotificationState.Success(
        company = company4Preview,
        notifications = (1L..3L).map { notification4Preview.copy(id = it) }
      ),
      onEvent = {}
    )
  }
}
