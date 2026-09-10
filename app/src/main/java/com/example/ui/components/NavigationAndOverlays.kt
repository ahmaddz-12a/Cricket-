package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsCricket
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.MilestoneAlert
import com.example.model.MilestoneType
import com.example.ui.theme.ColorFour
import com.example.ui.theme.ColorSix
import com.example.ui.theme.ColorWicket
import com.example.ui.theme.CricketGreen
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay

enum class AppScreen {
  HOME,
  LIVE_SCORING,
  MATCHES,
  STATISTICS,
  TEAMS,
  PROFILE,
  CREATE_MATCH,
  MATCH_SUMMARY,
  SCORECARD_DETAILS
}

@Composable
fun AppHeader(
  title: String = "FX Cricket Score",
  notificationCount: Int = 0,
  onNotificationsClick: () -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 4.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Logo Badge
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
              Brush.linearGradient(
                listOf(CricketGreen, CricketGreenDark, ElectricBlue)
              )
            )
            .border(1.dp, CricketGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "🏏",
            fontSize = 20.sp
          )
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "FX ",
              fontSize = 19.sp,
              fontWeight = FontWeight.Black,
              color = CricketGreen
            )
            Text(
              text = "Cricket Score",
              fontSize = 19.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Text(
            text = "PRO LIVE SCORING",
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = ElectricBlue,
            letterSpacing = 1.sp
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Notification Icon with Badge
        IconButton(
          onClick = onNotificationsClick,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
          BadgedBox(
            badge = {
              if (notificationCount > 0) {
                Badge(
                  containerColor = ColorWicket,
                  contentColor = TextWhite
                ) {
                  Text(
                    text = if (notificationCount > 9) "9+" else notificationCount.toString(),
                    fontSize = 10.sp
                  )
                }
              }
            }
          ) {
            Icon(
              imageVector = if (notificationCount > 0) Icons.Filled.Notifications else Icons.Outlined.Notifications,
              contentDescription = "Notifications",
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        // Profile Avatar Icon
        IconButton(
          onClick = onProfileClick,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.5.dp, CricketGreen.copy(alpha = 0.4f), CircleShape)
        ) {
          Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Profile",
            tint = CricketGreen,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
fun AppBottomNavigation(
  currentScreen: AppScreen,
  onNavigate: (AppScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val items = listOf(
    NavigationItem(AppScreen.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    NavigationItem(AppScreen.LIVE_SCORING, "Live Match", Icons.Filled.SportsCricket, Icons.Outlined.SportsCricket),
    NavigationItem(AppScreen.MATCHES, "Matches", Icons.Outlined.Event, Icons.Outlined.Event),
    NavigationItem(AppScreen.STATISTICS, "Stats", Icons.Outlined.BarChart, Icons.Outlined.BarChart),
    NavigationItem(AppScreen.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      items.forEach { item ->
        val isSelected = currentScreen == item.screen
        val activeBg = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else Color.Transparent
        val activeColor = if (isSelected) CricketGreen else TextMuted

        Column(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(activeBg)
            .clickable { onNavigate(item.screen) }
            .padding(horizontal = 12.dp, vertical = 6.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            tint = activeColor,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = item.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = activeColor
          )
        }
      }
    }
  }
}

data class NavigationItem(
  val screen: AppScreen,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
)

@Composable
fun MilestoneAnimationOverlay(
  alert: MilestoneAlert?,
  onDismiss: () -> Unit
) {
  if (alert == null) return

  LaunchedEffect(alert.id) {
    delay(2400)
    onDismiss()
  }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale"
  )

  val (bgColor, accentColor, iconText) = when (alert.type) {
    MilestoneType.SIX -> Triple(ColorSix, Color(0xFFFEF08A), "🔥")
    MilestoneType.FOUR -> Triple(ColorFour, Color(0xFFBBF7D0), "⚡")
    MilestoneType.WICKET -> Triple(ColorWicket, Color(0xFFFECACA), "🏏")
    MilestoneType.FIFTY -> Triple(Color(0xFF3B82F6), Color(0xFFBFDBFE), "⭐")
    MilestoneType.CENTURY -> Triple(Color(0xFF8B5CF6), Color(0xFFDDD6FE), "💯")
    MilestoneType.HAT_TRICK -> Triple(Color(0xFFD97706), Color(0xFFFDE68A), "🎩")
    MilestoneType.VICTORY -> Triple(CricketGreen, Color(0xFFDCFCE7), "🏆")
  }

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .scale(pulseScale)
        .clip(RoundedCornerShape(24.dp))
        .background(
          Brush.verticalGradient(
            listOf(
              bgColor.copy(alpha = 0.95f),
              DarkCard.copy(alpha = 0.98f)
            )
          )
        )
        .border(2.dp, accentColor.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = iconText,
          fontSize = 52.sp,
          modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
          text = alert.title,
          fontSize = 24.sp,
          fontWeight = FontWeight.Black,
          color = TextWhite,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = alert.subtitle,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
          color = accentColor,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp
        )
      }
    }
  }
}

@Composable
fun NotificationSheetDialog(
  notifications: List<String>,
  onDismiss: () -> Unit,
  onClearAll: () -> Unit,
  onRemoveItem: (Int) -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(450.dp),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.Notifications,
              contentDescription = null,
              tint = CricketGreen,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Match Notifications",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (notifications.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No match alerts yet.\nEvents like wickets, boundaries, and milestones will appear here!",
              textAlign = TextAlign.Center,
              color = TextMuted,
              fontSize = 13.sp
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            itemsIndexed(notifications) { index, msg ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant)
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = msg,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.weight(1f)
                )
                IconButton(
                  onClick = { onRemoveItem(index) },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(
                    Icons.Filled.Close,
                    contentDescription = "Remove",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = onClearAll,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Text("Clear All Notifications", color = TextMuted, fontSize = 13.sp)
          }
        }
      }
    }
  }
}
