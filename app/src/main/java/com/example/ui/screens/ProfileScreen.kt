package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.theme.ColorGold
import com.example.ui.theme.ColorWicket
import com.example.ui.theme.CricketGreen
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ProfileScreen(
  userProfile: UserProfile,
  onToggleDarkMode: (Boolean) -> Unit,
  onToggleSound: (Boolean) -> Unit,
  onToggleNotifications: (Boolean) -> Unit,
  onResetSampleData: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showResetDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))

      // PROFILE HERO CARD
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, CricketGreen.copy(alpha = 0.4f))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                listOf(
                  CricketGreenDark.copy(alpha = 0.25f),
                  MaterialTheme.colorScheme.surfaceVariant
                )
              )
            )
            .padding(20.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Avatar
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    listOf(CricketGreen, ElectricBlue)
                  )
                )
                .border(2.dp, TextWhite.copy(alpha = 0.8f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text("🏏", fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
              Text(
                text = userProfile.username,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Favorite: ${userProfile.favoriteTeam}",
                fontSize = 13.sp,
                color = CricketGreen,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Lead Match Official & Scorer",
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }
        }
      }
    }

    // STATS METRICS ROW
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ProfileStatTile(
          label = "Scored",
          value = "${userProfile.totalMatchesScored}",
          unit = "Matches",
          color = CricketGreen,
          modifier = Modifier.weight(1f)
        )
        ProfileStatTile(
          label = "Created",
          value = "${userProfile.teamsCreated}",
          unit = "Teams",
          color = ElectricBlue,
          modifier = Modifier.weight(1f)
        )
        ProfileStatTile(
          label = "Recorded",
          value = "${userProfile.matchesWon}",
          unit = "Wins",
          color = ColorGold,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // APP SETTINGS
    item {
      Text(
        text = "PREFERENCES & SETTINGS",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Dark Mode Toggle
          SettingToggleRow(
            icon = Icons.Filled.DarkMode,
            title = "Dark Theme",
            subtitle = "Modern deep cricket charcoal theme",
            isChecked = userProfile.isDarkMode,
            onCheckedChange = onToggleDarkMode
          )

          HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

          // Notifications Toggle
          SettingToggleRow(
            icon = Icons.Filled.Notifications,
            title = "Milestone Notifications",
            subtitle = "Alerts for wickets, 50s, 100s, and victories",
            isChecked = userProfile.isNotificationsEnabled,
            onCheckedChange = onToggleNotifications
          )

          HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

          // Sound Toggle
          SettingToggleRow(
            icon = Icons.Filled.VolumeUp,
            title = "Sound & Vibrations",
            subtitle = "Audio feedback on boundaries and milestones",
            isChecked = userProfile.isSoundEnabled,
            onCheckedChange = onToggleSound
          )
        }
      }
    }

    // DATA MANAGEMENT CARD
    item {
      Text(
        text = "DATA MANAGEMENT",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Button(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Filled.Refresh, contentDescription = null, tint = CricketGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset Demo Matches & Data", color = CricketGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // BRANDING FOOTER
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "🏏 FX Cricket Score v2.4",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Engineered for local matches, clubs & tournaments",
          fontSize = 11.sp,
          color = TextMuted
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      title = { Text("Reset Match Data?", fontWeight = FontWeight.Bold) },
      text = { Text("This will restore default exciting live & completed sample matches.") },
      confirmButton = {
        Button(
          onClick = {
            showResetDialog = false
            onResetSampleData()
          },
          colors = ButtonDefaults.buttonColors(containerColor = CricketGreen)
        ) {
          Text("Reset", color = Color(0xFF003816), fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetDialog = false }) {
          Text("Cancel", color = TextMuted)
        }
      }
    )
  }
}

@Composable
fun ProfileStatTile(
  label: String,
  value: String,
  unit: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
      Text(text = unit, fontSize = 10.sp, color = TextMuted)
    }
  }
}

@Composable
fun SettingToggleRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  isChecked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = CricketGreen, modifier = Modifier.size(18.dp))
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(subtitle, fontSize = 11.sp, color = TextMuted)
      }
    }

    Switch(
      checked = isChecked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color(0xFF003816),
        checkedTrackColor = CricketGreen
      )
    )
  }
}
