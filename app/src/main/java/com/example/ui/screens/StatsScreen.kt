package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CricketMatch
import com.example.model.Player
import com.example.ui.theme.ColorFour
import com.example.ui.theme.ColorGold
import com.example.ui.theme.ColorSix
import com.example.ui.theme.ColorWicket
import com.example.ui.theme.CricketGreen
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun StatsScreen(
  matches: List<CricketMatch>,
  modifier: Modifier = Modifier
) {
  // Aggregate stats across all matches
  val allBatsmen = matches.flatMap { m ->
    m.innings1.batsmen + (m.innings2?.batsmen ?: emptyList())
  }

  // Top run scorers (summing runs by player name)
  val topScorers = allBatsmen
    .groupBy { it.name }
    .map { (name, players) ->
      val totalRuns = players.sumOf { it.runs }
      val totalBalls = players.sumOf { it.ballsFaced }
      val totalFours = players.sumOf { it.fours }
      val totalSixes = players.sumOf { it.sixes }
      val sr = if (totalBalls > 0) (totalRuns.toDouble() / totalBalls) * 100.0 else 0.0
      Triple(name, totalRuns, "$totalBalls balls, $totalFours x 4s, $totalSixes x 6s (SR: ${String.format("%.1f", sr)})")
    }
    .sortedByDescending { it.second }
    .take(5)

  // Top bowlers
  val allBowlers = matches.flatMap { m ->
    m.innings1.bowlers + (m.innings2?.bowlers ?: emptyList())
  }

  val topBowlers = allBowlers
    .groupBy { it.name }
    .map { (name, bowlers) ->
      val totalWickets = bowlers.sumOf { it.wickets }
      val totalRunsConceded = bowlers.sumOf { it.runsConceded }
      val totalBallsBowled = bowlers.sumOf { it.ballsBowled }
      val overs = "${totalBallsBowled / 6}.${totalBallsBowled % 6}"
      val eco = if (totalBallsBowled > 0) totalRunsConceded / (totalBallsBowled / 6.0) else 0.0
      Triple(name, totalWickets, "$overs ov, $totalRunsConceded runs (ECO: ${String.format("%.2f", eco)})")
    }
    .sortedByDescending { it.second }
    .take(5)

  val totalBoundariesSixes = allBatsmen.sumOf { it.sixes }
  val totalBoundariesFours = allBatsmen.sumOf { it.fours }
  val totalRunsScoredOverall = matches.sumOf { it.innings1.totalRuns + (it.innings2?.totalRuns ?: 0) }
  val totalWicketsOverall = matches.sumOf { it.innings1.wickets + (it.innings2?.wickets ?: 0) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(
          text = "Match Analytics & Stats",
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Aggregated records across all fixtures",
          fontSize = 12.sp,
          color = TextMuted
        )
      }
    }

    // OVERALL HIGHLIGHT STATS TILES
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatsSummaryCard(
          label = "Total Runs",
          value = totalRunsScoredOverall.toString(),
          accentColor = CricketGreen,
          modifier = Modifier.weight(1f)
        )
        StatsSummaryCard(
          label = "Total Wickets",
          value = totalWicketsOverall.toString(),
          accentColor = ColorWicket,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatsSummaryCard(
          label = "Total 6s 🔥",
          value = totalBoundariesSixes.toString(),
          accentColor = ColorSix,
          modifier = Modifier.weight(1f)
        )
        StatsSummaryCard(
          label = "Total 4s ⚡",
          value = totalBoundariesFours.toString(),
          accentColor = ColorFour,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // TOP RUN SCORERS LEADERBOARD
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "TOP RUN SCORERS",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TextMuted,
              letterSpacing = 1.sp
            )
            Text(
              text = "Runs",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = CricketGreen
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          if (topScorers.isEmpty()) {
            Text("No scorers recorded yet", fontSize = 12.sp, color = TextMuted)
          } else {
            topScorers.forEachIndexed { idx, (name, runs, detail) ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(24.dp)
                      .clip(CircleShape)
                      .background(if (idx == 0) ColorGold else MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "${idx + 1}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (idx == 0) TextWhite else TextMuted
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = name,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(text = detail, fontSize = 11.sp, color = TextMuted)
                  }
                }

                Text(
                  text = "$runs",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Black,
                  color = CricketGreen
                )
              }
              if (idx < topScorers.size - 1) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
              }
            }
          }
        }
      }
    }

    // TOP WICKET TAKERS LEADERBOARD
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "TOP WICKET TAKERS",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TextMuted,
              letterSpacing = 1.sp
            )
            Text(
              text = "Wickets",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = ColorWicket
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          if (topBowlers.isEmpty()) {
            Text("No bowling data recorded yet", fontSize = 12.sp, color = TextMuted)
          } else {
            topBowlers.forEachIndexed { idx, (name, wkts, detail) ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(24.dp)
                      .clip(CircleShape)
                      .background(if (idx == 0) ColorWicket else MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "${idx + 1}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (idx == 0) TextWhite else TextMuted
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = name,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(text = detail, fontSize = 11.sp, color = TextMuted)
                  }
                }

                Text(
                  text = "$wkts",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Black,
                  color = ColorWicket
                )
              }
              if (idx < topBowlers.size - 1) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
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

@Composable
fun StatsSummaryCard(
  label: String,
  value: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = accentColor)
    }
  }
}
