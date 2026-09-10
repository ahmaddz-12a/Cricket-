package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CricketMatch
import com.example.ui.components.AppScreen
import com.example.ui.theme.ColorGold
import com.example.ui.theme.CricketGreen
import com.example.ui.theme.CricketGreenDark
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun MatchSummaryScreen(
  match: CricketMatch,
  onNavigate: (AppScreen) -> Unit,
  onViewScorecard: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))

      // TROPHY WINNER HERO BANNER
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(2.dp, ColorGold.copy(alpha = 0.6f))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                listOf(
                  ColorGold.copy(alpha = 0.25f),
                  MaterialTheme.colorScheme.surfaceVariant
                )
              )
            )
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(ColorGold.copy(alpha = 0.2f))
                .border(2.dp, ColorGold, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text("🏆", fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "MATCH RESULT",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = ColorGold,
              letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = match.resultMessage.ifBlank { "${match.matchName} Concluded" },
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "${match.venue} • ${match.dateStr}",
              fontSize = 12.sp,
              color = TextMuted
            )
          }
        }
      }
    }

    // MATCH INNINGS SUMMARY CARD
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "MATCH RECAP",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
          )

          // Innings 1
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = match.innings1.battingTeamName,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${match.innings1.totalRuns}/${match.innings1.wickets} (${match.innings1.oversString} ov)",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = CricketGreen
            )
          }

          // Innings 2 (if played)
          if (match.innings2 != null) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = match.innings2.battingTeamName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${match.innings2.totalRuns}/${match.innings2.wickets} (${match.innings2.oversString} ov)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = ElectricBlue
              )
            }
          }

          Button(
            onClick = onViewScorecard,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Text("View Full Scorecard", fontSize = 13.sp, color = CricketGreen, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // PLAYER OF THE MATCH (MVP)
    if (match.playerOfTheMatchName != null) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          border = androidx.compose.foundation.BorderStroke(1.dp, ColorGold.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("🏅", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "PLAYER OF THE MATCH",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ColorGold,
                letterSpacing = 1.sp
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(50.dp)
                  .clip(CircleShape)
                  .background(Brush.linearGradient(listOf(ColorGold, Color(0xFFD97706)))),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = match.playerOfTheMatchName.take(1),
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Black,
                  color = TextWhite
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column {
                Text(
                  text = match.playerOfTheMatchName,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Black,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = match.playerOfTheMatchStats ?: "Match Winning Performance",
                  fontSize = 13.sp,
                  color = ColorGold,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }
    }

    // ACTIONS: SHARE, DOWNLOAD SCORECARD, NEW MATCH, BACK TO HOME
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Share Match Button
          Button(
            onClick = {
              val shareText = buildString {
                appendLine("🏏 FX Cricket Score")
                appendLine(match.matchName)
                appendLine(match.resultMessage)
                appendLine("${match.innings1.battingTeamName}: ${match.innings1.totalRuns}/${match.innings1.wickets} (${match.innings1.oversString} ov)")
                if (match.innings2 != null) {
                  appendLine("${match.innings2.battingTeamName}: ${match.innings2.totalRuns}/${match.innings2.wickets} (${match.innings2.oversString} ov)")
                }
                if (!match.playerOfTheMatchName.isNullOrBlank()) {
                  appendLine("POTM: ${match.playerOfTheMatchName} (${match.playerOfTheMatchStats})")
                }
              }
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("FX Cricket Match Summary", shareText))
              Toast.makeText(context, "Match Summary copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
          ) {
            Icon(Icons.Filled.Share, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share Match", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }

          // Download Scorecard Button
          OutlinedButton(
            onClick = {
              val reportText = buildString {
                appendLine("===============================")
                appendLine("🏏 FX CRICKET SCORECARD")
                appendLine("===============================")
                appendLine("Match: ${match.matchName}")
                appendLine("Venue: ${match.venue} | ${match.dateStr}")
                appendLine("Result: ${match.resultMessage}")
                appendLine("-------------------------------")
                appendLine("1st Innings: ${match.innings1.battingTeamName}")
                appendLine("Score: ${match.innings1.totalRuns}/${match.innings1.wickets} in ${match.innings1.oversString} overs")
                if (match.innings2 != null) {
                  appendLine("-------------------------------")
                  appendLine("2nd Innings: ${match.innings2.battingTeamName}")
                  appendLine("Score: ${match.innings2.totalRuns}/${match.innings2.wickets} in ${match.innings2.oversString} overs")
                }
                appendLine("===============================")
              }
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("Scorecard Report", reportText))
              Toast.makeText(context, "Scorecard downloaded/copied!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Scorecard", fontSize = 13.sp)
          }
        }

        // New Match Button
        Button(
          onClick = { onNavigate(AppScreen.CREATE_MATCH) },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = CricketGreen)
        ) {
          Icon(Icons.Filled.Add, contentDescription = null, tint = Color(0xFF003816))
          Spacer(modifier = Modifier.width(6.dp))
          Text("START NEW MATCH", color = Color(0xFF003816), fontWeight = FontWeight.Black, fontSize = 14.sp)
        }

        // Back to Home Button
        OutlinedButton(
          onClick = { onNavigate(AppScreen.HOME) },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Filled.Home, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Back to Home", fontSize = 13.sp)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
