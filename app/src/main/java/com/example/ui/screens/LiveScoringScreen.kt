package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BallDelivery
import com.example.model.CricketMatch
import com.example.model.ExtraType
import com.example.model.MatchStatus
import com.example.model.Player
import com.example.model.WicketType
import com.example.ui.components.CustomExtraDialog
import com.example.ui.components.SelectBowlerDialog
import com.example.ui.components.WicketModalDialog
import com.example.ui.theme.ColorDotBall
import com.example.ui.theme.ColorFour
import com.example.ui.theme.ColorGold
import com.example.ui.theme.ColorSingle
import com.example.ui.theme.ColorSix
import com.example.ui.theme.ColorTwoThree
import com.example.ui.theme.ColorWicket
import com.example.ui.theme.CricketGreen
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun LiveScoringScreen(
  match: CricketMatch?,
  onRecordBall: (runs: Int, extraType: ExtraType, extraRuns: Int, isWicket: Boolean, wicketType: WicketType?, dismissedId: String?, fielder: String?, nextBatterId: String?) -> Unit,
  onSwapStrike: () -> Unit,
  onChangeBowler: (String) -> Unit,
  onUndo: () -> Unit,
  onEndInnings: () -> Unit,
  onCreateMatchClick: () -> Unit,
  onViewSummaryClick: () -> Unit,
  onViewScorecardClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (match == null) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🏏", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          "No Active Match Selected",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          "Create a new match or pick one from My Matches to start live scoring.",
          fontSize = 13.sp,
          color = TextMuted,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
          onClick = onCreateMatchClick,
          colors = ButtonDefaults.buttonColors(containerColor = CricketGreen)
        ) {
          Text("Create New Match", color = Color(0xFF003816), fontWeight = FontWeight.Bold)
        }
      }
    }
    return
  }

  val inn = match.currentInnings
  val striker = inn.batsmen.find { it.id == inn.currentStrikerId }
    ?: inn.batsmen.firstOrNull() ?: Player("p1", "Striker")
  val nonStriker = inn.batsmen.find { it.id == inn.currentNonStrikerId }
    ?: inn.batsmen.getOrNull(1) ?: Player("p2", "Non-Striker")
  val bowler = inn.bowlers.find { it.id == inn.currentBowlerId }
    ?: inn.bowlers.firstOrNull() ?: Player("bw1", "Bowler")

  var showWicketModal by remember { mutableStateOf(false) }
  var showBowlerModal by remember { mutableStateOf(false) }
  var extraTypePending by remember { mutableStateOf<ExtraType?>(null) }

  val isChasing = match.currentInningsIndex == 1 && match.targetRuns != null
  val targetRuns = match.targetRuns ?: 0
  val runsNeeded = (targetRuns - inn.totalRuns).coerceAtLeast(0)
  val ballsRemaining = ((match.totalOvers * 6) - inn.legalBalls).coerceAtLeast(0)

  val requiredRunRate = if (ballsRemaining > 0 && isChasing) {
    (runsNeeded.toDouble() / ballsRemaining) * 6.0
  } else 0.0

  val projectedScore = (inn.runRate * match.totalOvers).toInt().coerceAtLeast(inn.totalRuns)

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 14.dp, vertical = 10.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. MATCH HEADER & STATUS
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = match.matchName,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${match.matchType.displayName} (${match.totalOvers} Ov) • ${match.venue}",
          fontSize = 11.sp,
          color = TextMuted
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedButton(
          onClick = onViewScorecardClick,
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text("Scorecard", fontSize = 11.sp)
        }

        if (match.status == MatchStatus.COMPLETED) {
          Button(
            onClick = onViewSummaryClick,
            colors = ButtonDefaults.buttonColors(containerColor = CricketGreen),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text("Summary", fontSize = 11.sp, color = Color(0xFF003816), fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 2. MAIN SCOREBOARD CARD (Hero Card)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surface
              )
            )
          )
          .padding(18.dp)
      ) {
        Column {
          // Team Name & Innings tag
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(if (match.status == MatchStatus.LIVE) ColorWicket else CricketGreen)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = inn.battingTeamName.uppercase(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = CricketGreen,
                letterSpacing = 1.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(ElectricBlue.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "INNINGS ${inn.inningsNumber}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricBlue
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Main Score & Overs display
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            // Score
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "${inn.totalRuns}",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = " / ${inn.wickets}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ColorWicket,
                modifier = Modifier.padding(bottom = 4.dp)
              )
            }

            // Overs
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "OVERS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
              )
              Text(
                text = "${inn.oversString} / ${match.totalOvers}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Run Rates & Target / Projected row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "CRR: ${String.format("%.2f", inn.runRate)}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = CricketGreen
            )

            if (isChasing) {
              Text(
                text = "Target: $targetRuns",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ColorGold
              )
              Text(
                text = "RRR: ${String.format("%.2f", requiredRunRate)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (requiredRunRate > 12.0) ColorWicket else ElectricBlue
              )
            } else {
              Text(
                text = "Projected: $projectedScore",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted
              )
            }
          }

          // Smart Insight Banner
          if (isChasing) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGold.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = "📢 ${inn.battingTeamName} needs $runsNeeded runs from $ballsRemaining balls",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ColorGold
              )
            }
          }
        }
      }
    }

    // 3. CURRENT BATSMEN SECTION
    Card(
      modifier = Modifier.fillMaxWidth(),
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
            text = "BATSMEN",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
          )
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(MaterialTheme.colorScheme.surface)
              .clickable { onSwapStrike() }
              .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Filled.SwapHoriz, contentDescription = "Swap Strike", tint = CricketGreen, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Swap Strike", fontSize = 11.sp, color = CricketGreen, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Striker Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CricketGreen.copy(alpha = 0.1f))
            .border(1.dp, CricketGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🏏", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = striker.name,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = " *",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Black,
                  color = CricketGreen
                )
              }
              Text(
                text = "SR: ${String.format("%.1f", striker.strikeRate)}",
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "${striker.runs}",
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = CricketGreen
            )
            Text(
              text = " (${striker.ballsFaced})",
              fontSize = 13.sp,
              color = TextMuted
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "${striker.fours}x4 • ${striker.sixes}x6",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Non-Striker Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(22.dp))
            Column {
              Text(
                text = nonStriker.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "SR: ${String.format("%.1f", nonStriker.strikeRate)}",
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "${nonStriker.runs}",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = " (${nonStriker.ballsFaced})",
              fontSize = 13.sp,
              color = TextMuted
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "${nonStriker.fours}x4 • ${nonStriker.sixes}x6",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }
      }
    }

    // 4. CURRENT BOWLER SECTION
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(ElectricBlue.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Text("⚾", fontSize = 16.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = bowler.name,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "O: ${bowler.oversBowledString}  •  R: ${bowler.runsConceded}  •  W: ${bowler.wickets}  •  ECO: ${String.format("%.2f", bowler.economy)}",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }

        OutlinedButton(
          onClick = { showBowlerModal = true },
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text("Change", fontSize = 11.sp)
        }
      }
    }

    // 5. BALL-BY-BALL TIMELINE (This Over)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        val currentOverIndex = inn.legalBalls / 6
        val thisOverBalls = inn.balls.filter { it.overNumber == currentOverIndex }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "THIS OVER (OVER ${currentOverIndex + 1})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
          )
          Text(
            text = "${thisOverBalls.sumOf { it.totalRunsOnBall }} runs",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CricketGreen
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (thisOverBalls.isEmpty()) {
            Text(
              text = "Ready for the first delivery...",
              fontSize = 12.sp,
              color = TextMuted,
              modifier = Modifier.padding(vertical = 4.dp)
            )
          } else {
            thisOverBalls.forEach { ball ->
              BallTimelineBadge(ball = ball)
            }
          }
        }
      }
    }

    // 6. BALL-BY-BALL SCORING BUTTONS
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      border = androidx.compose.foundation.BorderStroke(1.dp, CricketGreen.copy(alpha = 0.3f))
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = "BALL SCORING",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TextMuted,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Large Tap Run Buttons (0, 1, 2, 3, 4, 6)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(0, 1, 2, 3, 4, 6).forEach { run ->
            val bg = when (run) {
              0 -> ColorDotBall
              1 -> ColorSingle
              2, 3 -> ColorTwoThree
              4 -> ColorFour
              6 -> ColorSix
              else -> CricketGreen
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bg)
                .clickable {
                  onRecordBall(run, ExtraType.NONE, 0, false, null, null, null, null)
                },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (run == 0) "•" else run.toString(),
                fontSize = if (run == 0) 30.sp else 22.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Extras Buttons: Wide, No Ball, Bye, Leg Bye
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(
            ExtraType.WIDE to "Wd",
            ExtraType.NO_BALL to "Nb",
            ExtraType.BYE to "Bye",
            ExtraType.LEG_BYE to "Leg Bye"
          ).forEach { (extra, label) ->
            Box(
              modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                .clickable { extraTypePending = extra },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Special Action Buttons: WICKET (Large Red), UNDO, END INNINGS
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Wicket Button
          Button(
            onClick = { showWicketModal = true },
            modifier = Modifier
              .weight(1.8f)
              .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorWicket),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = "🏏 WICKET",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = TextWhite
            )
          }

          // Undo Button
          OutlinedButton(
            onClick = onUndo,
            modifier = Modifier
              .weight(1.2f)
              .height(48.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Filled.Undo, contentDescription = "Undo", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Undo", fontSize = 13.sp)
          }

          // End Innings / Declare
          OutlinedButton(
            onClick = onEndInnings,
            modifier = Modifier
              .weight(1.2f)
              .height(48.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("End Inn", fontSize = 12.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }

  // Modals
  if (showWicketModal) {
    WicketModalDialog(
      innings = inn,
      onDismiss = { showWicketModal = false },
      onConfirmWicket = { wType, disId, fielder, nextBId ->
        showWicketModal = false
        onRecordBall(0, ExtraType.NONE, 0, true, wType, disId, fielder, nextBId)
      }
    )
  }

  if (showBowlerModal) {
    SelectBowlerDialog(
      innings = inn,
      onDismiss = { showBowlerModal = false },
      onSelectBowler = { newBId ->
        showBowlerModal = false
        onChangeBowler(newBId)
      }
    )
  }

  if (extraTypePending != null) {
    CustomExtraDialog(
      extraType = extraTypePending!!,
      onDismiss = { extraTypePending = null },
      onConfirm = { addedRuns ->
        val eType = extraTypePending!!
        extraTypePending = null
        val runsScored = if (eType == ExtraType.NO_BALL) addedRuns else 0
        val extraRuns = if (eType != ExtraType.NO_BALL) addedRuns else 0
        onRecordBall(runsScored, eType, extraRuns, false, null, null, null, null)
      }
    )
  }
}

@Composable
fun BallTimelineBadge(ball: BallDelivery) {
  val (bgColor, textColor, label) = when {
    ball.isWicket -> Triple(ColorWicket, TextWhite, "W")
    ball.extraType == ExtraType.WIDE -> Triple(Color(0xFFDB2777), TextWhite, "Wd${if (ball.extraRuns > 0) "+${ball.extraRuns}" else ""}")
    ball.extraType == ExtraType.NO_BALL -> Triple(Color(0xFF9333EA), TextWhite, "Nb${if (ball.runsScored > 0) "+${ball.runsScored}" else ""}")
    ball.extraType == ExtraType.BYE -> Triple(Color(0xFF64748B), TextWhite, "B${ball.extraRuns}")
    ball.extraType == ExtraType.LEG_BYE -> Triple(Color(0xFF64748B), TextWhite, "Lb${ball.extraRuns}")
    ball.runsScored == 6 -> Triple(ColorSix, TextWhite, "6")
    ball.runsScored == 4 -> Triple(ColorFour, TextWhite, "4")
    ball.runsScored == 0 -> Triple(ColorDotBall, TextWhite, "•")
    else -> Triple(ColorSingle, TextWhite, ball.runsScored.toString())
  }

  Box(
    modifier = Modifier
      .size(34.dp)
      .clip(CircleShape)
      .background(bgColor)
      .border(1.dp, TextWhite.copy(alpha = 0.2f), CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      fontSize = if (label.length > 2) 10.sp else 13.sp,
      fontWeight = FontWeight.Black,
      color = textColor
    )
  }
}
