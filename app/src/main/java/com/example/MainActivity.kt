package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.CricketRepository
import com.example.model.ExtraType
import com.example.model.MatchType
import com.example.model.MatchStatus
import com.example.model.TossDecision
import com.example.model.WicketType
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.AppHeader
import com.example.ui.components.AppScreen
import com.example.ui.components.MilestoneAnimationOverlay
import com.example.ui.components.NotificationSheetDialog
import com.example.ui.screens.CreateMatchScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveScoringScreen
import com.example.ui.screens.MatchSummaryScreen
import com.example.ui.screens.MatchesHistoryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ScorecardScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TeamsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val repository = CricketRepository(applicationContext)

    setContent {
      val userProfile by repository.userProfile.collectAsState()
      val matches by repository.matches.collectAsState()
      val currentMatch by repository.currentMatch.collectAsState()
      val teams by repository.teams.collectAsState()
      val milestoneAlert by repository.milestoneAlert.collectAsState()
      val notifications by repository.notifications.collectAsState()

      var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
      var showNotificationSheet by remember { mutableStateOf(false) }

      // Handle back press
      BackHandler(enabled = currentScreen != AppScreen.HOME) {
        currentScreen = AppScreen.HOME
      }

      MyApplicationTheme(darkTheme = userProfile.isDarkMode) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
          Scaffold(
            topBar = {
              if (currentScreen != AppScreen.CREATE_MATCH && currentScreen != AppScreen.SCORECARD_DETAILS) {
                AppHeader(
                  title = "FX Cricket Score",
                  notificationCount = if (userProfile.isNotificationsEnabled) notifications.size else 0,
                  onNotificationsClick = { showNotificationSheet = true },
                  onProfileClick = { currentScreen = AppScreen.PROFILE }
                )
              }
            },
            bottomBar = {
              if (currentScreen != AppScreen.CREATE_MATCH && currentScreen != AppScreen.SCORECARD_DETAILS) {
                AppBottomNavigation(
                  currentScreen = currentScreen,
                  onNavigate = { currentScreen = it }
                )
              }
            },
            containerColor = MaterialTheme.colorScheme.background
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              when (currentScreen) {
                AppScreen.HOME -> {
                  HomeScreen(
                    matches = matches,
                    currentMatch = currentMatch,
                    onNavigate = { currentScreen = it },
                    onSelectMatch = { repository.selectMatch(it.id) }
                  )
                }

                AppScreen.LIVE_SCORING -> {
                  LiveScoringScreen(
                    match = currentMatch,
                    onRecordBall = { runs, extraType, extraRuns, isWicket, wicketType, disId, fielder, nextBId ->
                      repository.recordBall(
                        runsScored = runs,
                        extraType = extraType,
                        extraRuns = extraRuns,
                        isWicket = isWicket,
                        wicketType = wicketType,
                        dismissedBatterId = disId,
                        fielderName = fielder,
                        nextBatterId = nextBId
                      )
                    },
                    onSwapStrike = { repository.swapStrike() },
                    onChangeBowler = { newBId -> repository.changeBowler(newBId) },
                    onUndo = { repository.undoLastBall() },
                    onEndInnings = { repository.endCurrentInnings() },
                    onCreateMatchClick = { currentScreen = AppScreen.CREATE_MATCH },
                    onViewSummaryClick = { currentScreen = AppScreen.MATCH_SUMMARY },
                    onViewScorecardClick = { currentScreen = AppScreen.SCORECARD_DETAILS }
                  )
                }

                AppScreen.MATCHES -> {
                  MatchesHistoryScreen(
                    matches = matches,
                    onSelectMatch = { repository.selectMatch(it.id) },
                    onDeleteMatch = { repository.deleteMatch(it) },
                    onNavigate = { currentScreen = it }
                  )
                }

                AppScreen.STATISTICS -> {
                  StatsScreen(matches = matches)
                }

                AppScreen.TEAMS -> {
                  TeamsScreen(
                    teams = teams,
                    onAddNewTeam = { name, shortName, color, playerList ->
                      repository.addNewTeam(name, shortName, color, playerList)
                    }
                  )
                }

                AppScreen.PROFILE -> {
                  ProfileScreen(
                    userProfile = userProfile,
                    onToggleDarkMode = { repository.toggleDarkMode(it) },
                    onToggleSound = { repository.toggleSound(it) },
                    onToggleNotifications = { repository.toggleNotifications(it) },
                    onResetSampleData = { repository.resetToSampleData() }
                  )
                }

                AppScreen.CREATE_MATCH -> {
                  CreateMatchScreen(
                    onBack = { currentScreen = AppScreen.HOME },
                    onStartMatch = { name, type, overs, t1Name, t2Name, t1Squad, t2Squad, venue, date, tossIdx, tossDec ->
                      repository.createNewMatch(
                        matchName = name,
                        matchType = type,
                        totalOvers = overs,
                        team1Name = t1Name,
                        team2Name = t2Name,
                        team1PlayerNames = t1Squad,
                        team2PlayerNames = t2Squad,
                        venue = venue,
                        date = date,
                        tossWinnerIndex = tossIdx,
                        tossDecision = tossDec
                      )
                      currentScreen = AppScreen.LIVE_SCORING
                    }
                  )
                }

                AppScreen.MATCH_SUMMARY -> {
                  if (currentMatch != null) {
                    MatchSummaryScreen(
                      match = currentMatch!!,
                      onNavigate = { currentScreen = it },
                      onViewScorecard = { currentScreen = AppScreen.SCORECARD_DETAILS }
                    )
                  } else {
                    HomeScreen(
                      matches = matches,
                      currentMatch = null,
                      onNavigate = { currentScreen = it },
                      onSelectMatch = { repository.selectMatch(it.id) }
                    )
                  }
                }

                AppScreen.SCORECARD_DETAILS -> {
                  if (currentMatch != null) {
                    ScorecardScreen(
                      match = currentMatch!!,
                      onBack = {
                        currentScreen = if (currentMatch!!.status == MatchStatus.COMPLETED) {
                          AppScreen.MATCH_SUMMARY
                        } else {
                          AppScreen.LIVE_SCORING
                        }
                      }
                    )
                  } else {
                    HomeScreen(
                      matches = matches,
                      currentMatch = null,
                      onNavigate = { currentScreen = it },
                      onSelectMatch = { repository.selectMatch(it.id) }
                    )
                  }
                }
              }
            }
          }

          // Dynamic Milestone Celebration Overlay (Four, Six, Wicket, 50, 100, Victory)
          MilestoneAnimationOverlay(
            alert = milestoneAlert,
            onDismiss = { repository.clearMilestoneAlert() }
          )

          // In-App Notification Sheet
          if (showNotificationSheet) {
            NotificationSheetDialog(
              notifications = notifications,
              onDismiss = { showNotificationSheet = false },
              onClearAll = { repository.clearAllNotifications() },
              onRemoveItem = { repository.dismissNotification(it) }
            )
          }
        }
      }
    }
  }
}
