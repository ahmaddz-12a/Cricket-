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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CricketMatch
import com.example.model.Innings
import com.example.model.Player
import com.example.ui.theme.ColorFour
import com.example.ui.theme.ColorSix
import com.example.ui.theme.ColorWicket
import com.example.ui.theme.CricketGreen
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ScorecardScreen(
  match: CricketMatch,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedInningsTab by remember { mutableStateOf(if (match.currentInningsIndex == 1 && match.innings2 != null) 1 else 0) }

  val inn = if (selectedInningsTab == 0 || match.innings2 == null) match.innings1 else match.innings2!!

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
          }
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = "Full Scorecard",
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = match.matchName,
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }
      }
    }

    // Innings Toggle Tabs
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .padding(4.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selectedInningsTab == 0) CricketGreen else Color.Transparent)
            .clickable { selectedInningsTab = 0 }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${match.innings1.battingTeamName} (1st)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selectedInningsTab == 0) Color(0xFF003816) else MaterialTheme.colorScheme.onSurface
          )
        }

        if (match.innings2 != null) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (selectedInningsTab == 1) CricketGreen else Color.Transparent)
              .clickable { selectedInningsTab = 1 }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${match.innings2.battingTeamName} (2nd)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (selectedInningsTab == 1) Color(0xFF003816) else MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    // Innings Overview Bar
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = inn.battingTeamName,
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = CricketGreen
            )
            Text(
              text = "CRR: ${String.format("%.2f", inn.runRate)}",
              fontSize = 12.sp,
              color = TextMuted
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "${inn.totalRuns} / ${inn.wickets}",
              fontSize = 22.sp,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "(${inn.oversString} / ${match.totalOvers} ov)",
              fontSize = 12.sp,
              color = TextMuted
            )
          }
        }
      }
    }

    // BATTING TABLE
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "BATTING",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Header Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Batter", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(2.2f))
            Text("R", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
            Text("B", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
            Text("4s", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
            Text("6s", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
            Text("SR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

          // Filter batsmen who have batted (faced balls, or are out, or are current striker/nonstriker)
          val battedList = inn.batsmen.filter {
            it.ballsFaced > 0 || it.isOut || it.id == inn.currentStrikerId || it.id == inn.currentNonStrikerId
          }

          if (battedList.isEmpty()) {
            Text("No batting data yet", fontSize = 12.sp, color = TextMuted, modifier = Modifier.padding(vertical = 8.dp))
          } else {
            battedList.forEach { batter ->
              val isCurrentStriker = batter.id == inn.currentStrikerId && !inn.isCompleted
              val isCurrentNonStriker = batter.id == inn.currentNonStrikerId && !inn.isCompleted

              Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(2.2f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = batter.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      if (isCurrentStriker) {
                        Text(" *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CricketGreen)
                      }
                    }
                    Text(
                      text = if (batter.isOut) batter.dismissalText else if (isCurrentStriker || isCurrentNonStriker) "batting" else "not out",
                      fontSize = 10.sp,
                      color = if (batter.isOut) ColorWicket else CricketGreen
                    )
                  }

                  Text("${batter.runs}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CricketGreen, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                  Text("${batter.ballsFaced}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                  Text("${batter.fours}", fontSize = 12.sp, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
                  Text("${batter.sixes}", fontSize = 12.sp, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
                  Text(String.format("%.1f", batter.strikeRate), fontSize = 12.sp, color = ElectricBlue, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
              }
            }
          }

          // Extras Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Extras", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(
              text = "${inn.extras.total} (b ${inn.extras.byes}, lb ${inn.extras.legByes}, w ${inn.extras.wides}, nb ${inn.extras.noBalls})",
              fontSize = 12.sp,
              color = TextMuted
            )
          }

          // Total Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Total", fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Text(
              text = "${inn.totalRuns} (${inn.wickets} wkts, ${inn.oversString} ov)",
              fontSize = 14.sp,
              fontWeight = FontWeight.Black,
              color = CricketGreen
            )
          }
        }
      }
    }

    // BOWLING TABLE
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "BOWLING",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Bowling Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Bowler", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(2.2f))
            Text("O", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
            Text("M", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
            Text("R", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
            Text("W", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
            Text("ECO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

          val bowledList = inn.bowlers.filter { it.ballsBowled > 0 || it.id == inn.currentBowlerId }

          if (bowledList.isEmpty()) {
            Text("No bowling data yet", fontSize = 12.sp, color = TextMuted, modifier = Modifier.padding(vertical = 8.dp))
          } else {
            bowledList.forEach { bowler ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = bowler.name,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.weight(2.2f)
                )
                Text(bowler.oversBowledString, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                Text("${bowler.maidens}", fontSize = 12.sp, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
                Text("${bowler.runsConceded}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                Text("${bowler.wickets}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ColorWicket, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                Text(String.format("%.2f", bowler.economy), fontSize = 12.sp, color = ElectricBlue, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
              }
              HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
          }
        }
      }
    }

    // FALL OF WICKETS
    if (inn.fallOfWickets.isNotEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "FALL OF WICKETS",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TextMuted,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              inn.fallOfWickets.forEach { fow ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "${fow.wicketNumber}-${fow.runs} (${fow.batsmanName})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${fow.overs} ov",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                  )
                }
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
