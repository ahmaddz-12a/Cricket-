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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CricketMatch
import com.example.model.MatchStatus
import com.example.ui.components.AppScreen
import com.example.ui.theme.ColorGold
import com.example.ui.theme.ColorWicket
import com.example.ui.theme.CricketGreen
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun HomeScreen(
  matches: List<CricketMatch>,
  currentMatch: CricketMatch?,
  onNavigate: (AppScreen) -> Unit,
  onSelectMatch: (CricketMatch) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))

      // HERO LIVE MATCH PREVIEW BANNER (If there is a match in progress)
      if (currentMatch != null && currentMatch.status == MatchStatus.LIVE) {
        val inn = currentMatch.currentInnings
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable {
              onSelectMatch(currentMatch)
              onNavigate(AppScreen.LIVE_SCORING)
            },
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          border = androidx.compose.foundation.BorderStroke(1.dp, CricketGreen.copy(alpha = 0.5f))
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  listOf(
                    CricketGreenDark.copy(alpha = 0.2f),
                    ElectricBlue.copy(alpha = 0.15f)
                  )
                )
              )
              .padding(18.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(ColorWicket)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "LIVE MATCH IN PROGRESS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorWicket,
                    letterSpacing = 1.sp
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Resume",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CricketGreen
                  )
                  Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = CricketGreen,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = inn.battingTeamName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "vs ${inn.bowlingTeamName}",
                    fontSize = 12.sp,
                    color = TextMuted
                  )
                }

                Column(horizontalAlignment = Alignment.End) {
                  Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                      text = "${inn.totalRuns}/${inn.wickets}",
                      fontSize = 24.sp,
                      fontWeight = FontWeight.Black,
                      color = CricketGreen
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "(${inn.oversString} ov)",
                      fontSize = 13.sp,
                      color = TextMuted,
                      modifier = Modifier.padding(bottom = 2.dp)
                    )
                  }
                  Text(
                    text = "CRR: ${String.format("%.2f", inn.runRate)}",
                    fontSize = 11.sp,
                    color = ElectricBlue
                  )
                }
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
      }
    }

    // MAIN ACTIONS GRID (Create Match, Live Score, My Matches, Statistics)
    item {
      Text(
        text = "QUICK ACTIONS",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Create Match Action
        ActionCard(
          title = "Create Match",
          subtitle = "New fixture",
          icon = Icons.Filled.Add,
          accentColor = CricketGreen,
          gradientColors = listOf(Color(0xFF00E676), Color(0xFF059669)),
          modifier = Modifier.weight(1f),
          onClick = { onNavigate(AppScreen.CREATE_MATCH) }
        )

        // Live Score Action
        ActionCard(
          title = "Live Score",
          subtitle = "Ball-by-ball",
          icon = Icons.Filled.SportsCricket,
          accentColor = ElectricBlue,
          gradientColors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
          modifier = Modifier.weight(1f),
          onClick = { onNavigate(AppScreen.LIVE_SCORING) }
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // My Matches Action
        ActionCard(
          title = "My Matches",
          subtitle = "History & saves",
          icon = Icons.Filled.Event,
          accentColor = Color(0xFFA855F7),
          gradientColors = listOf(Color(0xFFA855F7), Color(0xFF7E22CE)),
          modifier = Modifier.weight(1f),
          onClick = { onNavigate(AppScreen.MATCHES) }
        )

        // Statistics Action
        ActionCard(
          title = "Statistics",
          subtitle = "Performance analytics",
          icon = Icons.Filled.BarChart,
          accentColor = ColorGold,
          gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
          modifier = Modifier.weight(1f),
          onClick = { onNavigate(AppScreen.STATISTICS) }
        )
      }
    }

    // RECENT MATCHES SECTION
    item {
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "RECENT MATCHES",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = TextMuted,
          letterSpacing = 1.sp
        )
        Text(
          text = "See all (${matches.size})",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = ElectricBlue,
          modifier = Modifier.clickable { onNavigate(AppScreen.MATCHES) }
        )
      }
    }

    if (matches.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No matches yet. Tap 'Create Match' to get started!",
              color = TextMuted,
              fontSize = 13.sp
            )
          }
        }
      }
    } else {
      items(matches) { match ->
        RecentMatchCard(
          match = match,
          onClick = {
            onSelectMatch(match)
            if (match.status == MatchStatus.COMPLETED) {
              onNavigate(AppScreen.MATCH_SUMMARY)
            } else {
              onNavigate(AppScreen.LIVE_SCORING)
            }
          }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun ActionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  gradientColors: List<Color>,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Brush.linearGradient(gradientColors)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = TextWhite,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = TextMuted
      )
    }
  }
}

@Composable
fun RecentMatchCard(
  match: CricketMatch,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top row: Match Name & Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = match.matchName,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextMuted
        )

        val statusColor = when (match.status) {
          MatchStatus.LIVE -> ColorWicket
          MatchStatus.COMPLETED -> CricketGreen
          else -> ElectricBlue
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(statusColor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = match.status.displayName.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = statusColor
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Team 1 Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Color(match.team1.colorHex)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = match.team1.name.take(1),
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = TextWhite
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = match.team1.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        val inn1 = match.innings1
        val isTeam1BattingIn1 = inn1.battingTeamId == match.team1.id
        val team1Runs = if (isTeam1BattingIn1) inn1.totalRuns else (match.innings2?.totalRuns ?: 0)
        val team1Wkts = if (isTeam1BattingIn1) inn1.wickets else (match.innings2?.wickets ?: 0)
        val team1Ovs = if (isTeam1BattingIn1) inn1.oversString else (match.innings2?.oversString ?: "0.0")

        Text(
          text = "$team1Runs/$team1Wkts ($team1Ovs)",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // VS Separator
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "VS",
          fontSize = 10.sp,
          fontWeight = FontWeight.Black,
          color = TextMuted
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Team 2 Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Color(match.team2.colorHex)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = match.team2.name.take(1),
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = TextWhite
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = match.team2.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        val isTeam2BattingIn1 = match.innings1.battingTeamId == match.team2.id
        val team2Runs = if (isTeam2BattingIn1) match.innings1.totalRuns else (match.innings2?.totalRuns ?: 0)
        val team2Wkts = if (isTeam2BattingIn1) match.innings1.wickets else (match.innings2?.wickets ?: 0)
        val team2Ovs = if (isTeam2BattingIn1) match.innings1.oversString else (match.innings2?.oversString ?: "0.0")

        Text(
          text = if (match.innings2 != null || isTeam2BattingIn1) "$team2Runs/$team2Wkts ($team2Ovs)" else "Yet to bat",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (match.innings2 != null || isTeam2BattingIn1) MaterialTheme.colorScheme.onSurface else TextMuted
        )
      }

      // Result Text
      if (match.resultMessage.isNotBlank()) {
        Spacer(modifier = Modifier.height(12.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CricketGreen.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = match.resultMessage,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CricketGreen
          )
        }
      }
    }
  }
}
