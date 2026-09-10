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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MatchType
import com.example.model.TossDecision
import com.example.ui.theme.CricketGreen
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun CreateMatchScreen(
  onBack: () -> Unit,
  onStartMatch: (
    name: String,
    type: MatchType,
    overs: Int,
    team1Name: String,
    team2Name: String,
    team1Players: List<String>,
    team2Players: List<String>,
    venue: String,
    date: String,
    tossWinnerIndex: Int,
    tossDecision: TossDecision
  ) -> Unit,
  modifier: Modifier = Modifier
) {
  var matchName by remember { mutableStateOf("Club Championship Final") }
  var matchType by remember { mutableStateOf(MatchType.T20) }
  var oversInput by remember { mutableStateOf("20") }
  var team1Name by remember { mutableStateOf("FX Warriors") }
  var team2Name by remember { mutableStateOf("Cricket Kings") }
  var venue by remember { mutableStateOf("FX Cricket Ground") }
  var date by remember { mutableStateOf("Today") }

  var tossWinnerIndex by remember { mutableStateOf(0) } // 0 for Team 1, 1 for Team 2
  var tossDecision by remember { mutableStateOf(TossDecision.BAT) }

  var team1RosterText by remember {
    mutableStateOf("Ahmed, Zaky, Tariq Khan, Bilal Asif, Hamza Ali, Imran Nazir, Shahid Afridi, Rashid Khan, Wasim Akram, Shoaib Akhtar, Jasprit Bumrah")
  }
  var team2RosterText by remember {
    mutableStateOf("Rahul, Sam Curran, Arjun Verma, Vikram Rathore, David Warner, Glenn Maxwell, Rishabh Pant, Mitchell Starc, Pat Cummins, Kagiso Rabada, Trent Boult")
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Create New Match",
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    // 1. MATCH DETAILS CARD
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "MATCH DETAILS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
          )

          OutlinedTextField(
            value = matchName,
            onValueChange = { matchName = it },
            label = { Text("Match Name") },
            placeholder = { Text("e.g. Weekend Cup T20") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Text("Match Format", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)

          // Match Type Buttons (T20, T10, ODI, Custom)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            MatchType.values().forEach { type ->
              val isSel = matchType == type
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSel) CricketGreen else MaterialTheme.colorScheme.surface)
                  .clickable {
                    matchType = type
                    oversInput = type.defaultOvers.toString()
                  }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = type.displayName,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSel) Color(0xFF003816) else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = oversInput,
              onValueChange = { oversInput = it.filter { ch -> ch.isDigit() } },
              label = { Text("Overs per Innings") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f),
              singleLine = true
            )

            OutlinedTextField(
              value = venue,
              onValueChange = { venue = it },
              label = { Text("Venue / Location") },
              modifier = Modifier.weight(1.3f),
              singleLine = true
            )
          }
        }
      }
    }

    // 2. TEAMS SETUP CARD
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "TEAMS & SQUADS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
          )

          // Team 1 Name
          OutlinedTextField(
            value = team1Name,
            onValueChange = { team1Name = it },
            label = { Text("Team 1 Name") },
            leadingIcon = {
              Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(CricketGreen))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          // Team 2 Name
          OutlinedTextField(
            value = team2Name,
            onValueChange = { team2Name = it },
            label = { Text("Team 2 Name") },
            leadingIcon = {
              Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(ElectricBlue))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Text("Team 1 Players (Comma separated)", fontSize = 12.sp, color = TextMuted)
          OutlinedTextField(
            value = team1RosterText,
            onValueChange = { team1RosterText = it },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
          )

          Text("Team 2 Players (Comma separated)", fontSize = 12.sp, color = TextMuted)
          OutlinedTextField(
            value = team2RosterText,
            onValueChange = { team2RosterText = it },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
          )
        }
      }
    }

    // 3. TOSS CARD
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "TOSS OUTCOME",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
          )

          Text("Who Won the Toss?", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            listOf(0 to team1Name.ifBlank { "Team 1" }, 1 to team2Name.ifBlank { "Team 2" }).forEach { (idx, tName) ->
              val isSel = tossWinnerIndex == idx
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSel) ElectricBlue else MaterialTheme.colorScheme.surface)
                  .clickable { tossWinnerIndex = idx }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = tName,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSel) Color(0xFF003258) else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          Text("Elected To:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            TossDecision.values().forEach { dec ->
              val isSel = tossDecision == dec
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSel) CricketGreen else MaterialTheme.colorScheme.surface)
                  .clickable { tossDecision = dec }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${dec.displayName} First",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSel) Color(0xFF003816) else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }
    }

    // 4. START MATCH BUTTON
    item {
      Button(
        onClick = {
          val oversNum = oversInput.toIntOrNull()?.coerceIn(1, 100) ?: 20
          val t1Players = team1RosterText.split(",").map { it.trim() }.filter { it.isNotBlank() }
          val t2Players = team2RosterText.split(",").map { it.trim() }.filter { it.isNotBlank() }

          onStartMatch(
            matchName.ifBlank { "Cricket Match" },
            matchType,
            oversNum,
            team1Name.ifBlank { "FX Warriors" },
            team2Name.ifBlank { "Cricket Kings" },
            t1Players,
            t2Players,
            venue.ifBlank { "FX Arena" },
            date.ifBlank { "Today" },
            tossWinnerIndex,
            tossDecision
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CricketGreen),
        shape = RoundedCornerShape(14.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Filled.SportsCricket, contentDescription = null, tint = Color(0xFF003816))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "START MATCH",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF003816),
            letterSpacing = 1.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
