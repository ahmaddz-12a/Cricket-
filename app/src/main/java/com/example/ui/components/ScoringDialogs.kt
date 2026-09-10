package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.window.Dialog
import com.example.model.ExtraType
import com.example.model.Innings
import com.example.model.Player
import com.example.model.WicketType
import com.example.ui.theme.ColorFour
import com.example.ui.theme.ColorSix
import com.example.ui.theme.ColorWicket
import com.example.ui.theme.CricketGreen
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun WicketModalDialog(
  innings: Innings,
  onDismiss: () -> Unit,
  onConfirmWicket: (
    wicketType: WicketType,
    dismissedBatterId: String,
    fielderName: String?,
    nextBatterId: String?
  ) -> Unit
) {
  val striker = innings.batsmen.find { it.id == innings.currentStrikerId }
  val nonStriker = innings.batsmen.find { it.id == innings.currentNonStrikerId }
  val bowler = innings.bowlers.find { it.id == innings.currentBowlerId }

  var selectedType by remember { mutableStateOf(WicketType.BOWLED) }
  var selectedDismissedId by remember { mutableStateOf(striker?.id ?: "") }
  var fielderText by remember { mutableStateOf("") }

  // Available batsmen who haven't batted or are not out
  val eligibleNextBatsmen = innings.batsmen.filter {
    !it.isOut && it.id != innings.currentStrikerId && it.id != innings.currentNonStrikerId
  }
  var selectedNextBatterId by remember { mutableStateOf(eligibleNextBatsmen.firstOrNull()?.id) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 600.dp),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, ColorWicket.copy(alpha = 0.5f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ColorWicket.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text("🏏", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Record Wicket",
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = ColorWicket
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
          modifier = Modifier.weight(1f, fill = false),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // 1. Wicket Type
          item {
            Text(
              text = "DISMISSAL TYPE",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TextMuted,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf(WicketType.BOWLED, WicketType.CAUGHT, WicketType.LBW, WicketType.RUN_OUT).forEach { type ->
                val isSel = selectedType == type
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSel) ColorWicket else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { selectedType = type }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = type.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSel) TextWhite else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf(WicketType.STUMPED, WicketType.HIT_WICKET, WicketType.RETIRED_HURT).forEach { type ->
                val isSel = selectedType == type
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSel) ColorWicket else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { selectedType = type }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = type.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSel) TextWhite else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }

          // 2. Dismissed Batsman
          item {
            Text(
              text = "DISMISSED BATSMAN",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TextMuted,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              if (striker != null) {
                val isSel = selectedDismissedId == striker.id
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                      1.5.dp,
                      if (isSel) ColorWicket else MaterialTheme.colorScheme.outline,
                      RoundedCornerShape(10.dp)
                    )
                    .background(if (isSel) ColorWicket.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { selectedDismissedId = striker.id }
                    .padding(10.dp)
                ) {
                  Column {
                    Text(
                      text = striker.name,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text("Striker (${striker.runs} off ${striker.ballsFaced})", fontSize = 11.sp, color = TextMuted)
                  }
                }
              }

              if (nonStriker != null) {
                val isSel = selectedDismissedId == nonStriker.id
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                      1.5.dp,
                      if (isSel) ColorWicket else MaterialTheme.colorScheme.outline,
                      RoundedCornerShape(10.dp)
                    )
                    .background(if (isSel) ColorWicket.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { selectedDismissedId = nonStriker.id }
                    .padding(10.dp)
                ) {
                  Column {
                    Text(
                      text = nonStriker.name,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text("Non-Striker (${nonStriker.runs} off ${nonStriker.ballsFaced})", fontSize = 11.sp, color = TextMuted)
                  }
                }
              }
            }
          }

          // 3. Fielder / Catcher (if applicable)
          if (selectedType == WicketType.CAUGHT || selectedType == WicketType.RUN_OUT || selectedType == WicketType.STUMPED) {
            item {
              Text(
                text = if (selectedType == WicketType.CAUGHT) "CATCHER / FIELDER" else "FIELDER INVOLVED",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedTextField(
                value = fielderText,
                onValueChange = { fielderText = it },
                placeholder = { Text("Enter fielder name...", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
              )
            }
          }

          // 4. Next Batsman Selection
          item {
            Text(
              text = "NEXT BATSMAN IN",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TextMuted,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (eligibleNextBatsmen.isEmpty()) {
              Text(
                text = "No more batsmen available (Innings All Out)",
                fontSize = 12.sp,
                color = TextMuted
              )
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                eligibleNextBatsmen.take(4).forEach { nextB ->
                  val isSel = selectedNextBatterId == nextB.id
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSel) CricketGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                      .clickable { selectedNextBatterId = nextB.id }
                      .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = nextB.name,
                      fontSize = 13.sp,
                      fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSel) CricketGreen else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = nextB.role.displayName,
                      fontSize = 11.sp,
                      color = TextMuted
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Button
        Button(
          onClick = {
            onConfirmWicket(
              selectedType,
              selectedDismissedId,
              fielderText.ifBlank { null },
              selectedNextBatterId
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ColorWicket),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("CONFIRM DISMISSAL", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextWhite)
        }
      }
    }
  }
}

@Composable
fun SelectBowlerDialog(
  innings: Innings,
  onDismiss: () -> Unit,
  onSelectBowler: (String) -> Unit
) {
  val eligibleBowlers = innings.bowlers.filter { it.id != innings.currentBowlerId }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 480.dp),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f))
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Select Next Bowler",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Text(
          text = "Choose who will bowl this over (cannot bowl consecutive overs)",
          fontSize = 12.sp,
          color = TextMuted,
          modifier = Modifier.padding(vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
          modifier = Modifier.weight(1f, fill = false),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(eligibleBowlers) { bowler ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onSelectBowler(bowler.id) }
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = bowler.name,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "${bowler.role.displayName} • ${bowler.oversBowledString} ov, ${bowler.wickets} wkt, ${bowler.runsConceded} r",
                  fontSize = 11.sp,
                  color = TextMuted
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(ElectricBlue.copy(alpha = 0.2f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "E: ${String.format("%.1f", bowler.economy)}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = ElectricBlue
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun CustomExtraDialog(
  extraType: ExtraType,
  onDismiss: () -> Unit,
  onConfirm: (additionalRuns: Int) -> Unit
) {
  var selectedRuns by remember { mutableStateOf(0) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Record ${extraType.shortName} (${extraType.name.replace('_', ' ')})",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = if (extraType == ExtraType.WIDE || extraType == ExtraType.NO_BALL)
            "Select additional runs scored off this delivery:"
          else
            "Select runs scored as ${extraType.name.lowercase().replace('_', ' ')}:",
          fontSize = 12.sp,
          color = TextMuted,
          modifier = Modifier.padding(vertical = 8.dp),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          listOf(0, 1, 2, 3, 4).forEach { run ->
            val isSel = selectedRuns == run
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) CricketGreen else MaterialTheme.colorScheme.surfaceVariant)
                .clickable { selectedRuns = run }
                .padding(vertical = 12.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "+$run",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSel) TextWhite else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = { onConfirm(selectedRuns) },
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = CricketGreen),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("APPLY DELIVERY", color = Color(0xFF003816), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
