package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CricketMatch
import com.example.model.MatchStatus
import com.example.ui.components.AppScreen
import com.example.ui.theme.ColorWicket
import com.example.ui.theme.CricketGreen
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun MatchesHistoryScreen(
  matches: List<CricketMatch>,
  onSelectMatch: (CricketMatch) -> Unit,
  onDeleteMatch: (String) -> Unit,
  onNavigate: (AppScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  var filterStatus by remember { mutableStateOf<MatchStatus?>(null) } // null = ALL

  val filteredMatches = when (filterStatus) {
    null -> matches
    else -> matches.filter { it.status == filterStatus }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "My Matches",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${matches.size} total fixtures recorded",
            fontSize = 12.sp,
            color = TextMuted
          )
        }

        Button(
          onClick = { onNavigate(AppScreen.CREATE_MATCH) },
          colors = ButtonDefaults.buttonColors(containerColor = CricketGreen),
          shape = RoundedCornerShape(10.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Filled.Add, contentDescription = null, tint = Color(0xFF003816), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("New", color = Color(0xFF003816), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }

    // Filter Chips: All, Live, Completed
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(null to "All", MatchStatus.LIVE to "Live", MatchStatus.COMPLETED to "Completed").forEach { (status, label) ->
          val isSel = filterStatus == status
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSel) CricketGreen else MaterialTheme.colorScheme.surfaceVariant)
              .clickable { filterStatus = status }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSel) Color(0xFF003816) else MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    if (filteredMatches.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No matches found in this filter.", color = TextMuted, fontSize = 13.sp)
        }
      }
    } else {
      items(filteredMatches) { match ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
              onSelectMatch(match)
              if (match.status == MatchStatus.COMPLETED) {
                onNavigate(AppScreen.MATCH_SUMMARY)
              } else {
                onNavigate(AppScreen.LIVE_SCORING)
              }
            },
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${match.matchName} • ${match.dateStr}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted
              )

              IconButton(
                onClick = { onDeleteMatch(match.id) },
                modifier = Modifier.size(26.dp)
              ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Teams & Score
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = match.team1.name,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = match.team2.name,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                val inn1 = match.innings1
                val isT1Bat1 = inn1.battingTeamId == match.team1.id
                val t1Score = if (isT1Bat1) "${inn1.totalRuns}/${inn1.wickets}" else "${match.innings2?.totalRuns ?: 0}/${match.innings2?.wickets ?: 0}"
                val t2Score = if (isT1Bat1) "${match.innings2?.totalRuns ?: 0}/${match.innings2?.wickets ?: 0}" else "${inn1.totalRuns}/${inn1.wickets}"

                Text(
                  text = t1Score,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = CricketGreen
                )
                Text(
                  text = t2Score,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = ElectricBlue
                )
              }
            }

            if (match.resultMessage.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = match.resultMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CricketGreen
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = {
                  onSelectMatch(match)
                  onNavigate(AppScreen.SCORECARD_DETAILS)
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text("Scorecard", fontSize = 11.sp)
              }

              Spacer(modifier = Modifier.width(8.dp))

              Button(
                onClick = {
                  onSelectMatch(match)
                  if (match.status == MatchStatus.COMPLETED) {
                    onNavigate(AppScreen.MATCH_SUMMARY)
                  } else {
                    onNavigate(AppScreen.LIVE_SCORING)
                  }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (match.status == MatchStatus.LIVE) CricketGreen else ElectricBlue
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = if (match.status == MatchStatus.LIVE) "Resume" else "Summary",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (match.status == MatchStatus.LIVE) Color(0xFF003816) else TextWhite
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
