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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Player
import com.example.model.PlayerRole
import com.example.model.Team
import com.example.ui.theme.ColorGold
import com.example.ui.theme.ColorWicket
import com.example.ui.theme.CricketGreen
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun TeamsScreen(
  teams: List<Team>,
  onAddNewTeam: (name: String, shortName: String, colorHex: Long, playerNames: List<String>) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTeamId by remember { mutableStateOf(teams.firstOrNull()?.id ?: "") }
  var showCreateTeamModal by remember { mutableStateOf(false) }

  val activeTeam = teams.find { it.id == selectedTeamId } ?: teams.firstOrNull()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
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
        Column {
          Text(
            text = "Teams & Players",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Manage squads, captains & player roles",
            fontSize = 12.sp,
            color = TextMuted
          )
        }

        Button(
          onClick = { showCreateTeamModal = true },
          colors = ButtonDefaults.buttonColors(containerColor = CricketGreen),
          shape = RoundedCornerShape(10.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Filled.Add, contentDescription = null, tint = Color(0xFF003816), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Team", color = Color(0xFF003816), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }

    // Team Selector Horizontal Pills
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        teams.forEach { team ->
          val isSel = team.id == activeTeam?.id
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSel) CricketGreen else MaterialTheme.colorScheme.surfaceVariant)
              .clickable { selectedTeamId = team.id }
              .padding(vertical = 10.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = team.name,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSel) Color(0xFF003816) else MaterialTheme.colorScheme.onSurface,
              maxLines = 1
            )
          }
        }
      }
    }

    // Active Team Hero Card
    if (activeTeam != null) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(activeTeam.colorHex).copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(activeTeam.colorHex)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = activeTeam.shortName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
              Text(
                text = activeTeam.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${activeTeam.players.size} Players Registered",
                fontSize = 12.sp,
                color = TextMuted
              )
            }
          }
        }
      }

      // Squad Roster List
      item {
        Text(
          text = "PLAYING SQUAD & ROLES",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = TextMuted,
          letterSpacing = 1.sp
        )
      }

      items(activeTeam.players) { player ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
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
              // Avatar
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = player.name.take(1),
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = CricketGreen
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = player.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  if (player.isCaptain) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ColorGold)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                      Text("C", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF451A03))
                    }
                  }
                  if (player.isViceCaptain) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ElectricBlue)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                      Text("VC", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF003258))
                    }
                  }
                }

                Text(
                  text = "${player.role.displayName} • ${player.careerRuns} career runs, ${player.careerWickets} wkts",
                  fontSize = 11.sp,
                  color = TextMuted
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CricketGreen.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = player.role.displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CricketGreen
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // CREATE TEAM MODAL
  if (showCreateTeamModal) {
    CreateTeamDialog(
      onDismiss = { showCreateTeamModal = false },
      onConfirm = { name, shortName, color, playerList ->
        showCreateTeamModal = false
        onAddNewTeam(name, shortName, color, playerList)
      }
    )
  }
}

@Composable
fun CreateTeamDialog(
  onDismiss: () -> Unit,
  onConfirm: (name: String, shortName: String, colorHex: Long, playerNames: List<String>) -> Unit
) {
  var teamName by remember { mutableStateOf("") }
  var shortName by remember { mutableStateOf("") }
  var playersText by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Create New Team", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        OutlinedTextField(
          value = teamName,
          onValueChange = { teamName = it },
          label = { Text("Team Name") },
          placeholder = { Text("e.g. Dhaka Dynamites") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = shortName,
          onValueChange = { shortName = it },
          label = { Text("Short Code (3 letters)") },
          placeholder = { Text("DHD") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = playersText,
          onValueChange = { playersText = it },
          label = { Text("Player Names (Comma separated)") },
          placeholder = { Text("Player 1, Player 2, Player 3...") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 3
        )

        Button(
          onClick = {
            if (teamName.isNotBlank()) {
              val players = playersText.split(",").map { it.trim() }.filter { it.isNotBlank() }
              onConfirm(teamName, shortName.ifBlank { teamName.take(3).uppercase() }, 0xFF10B981, players)
            }
          },
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = CricketGreen)
        ) {
          Text("SAVE TEAM", color = Color(0xFF003816), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
