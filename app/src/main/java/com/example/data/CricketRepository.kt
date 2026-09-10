package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.BallDelivery
import com.example.model.CricketMatch
import com.example.model.CricketMatchSnapshot
import com.example.model.ExtraType
import com.example.model.ExtrasBreakdown
import com.example.model.FallOfWicket
import com.example.model.Innings
import com.example.model.MatchStatus
import com.example.model.MatchType
import com.example.model.MilestoneAlert
import com.example.model.MilestoneType
import com.example.model.Player
import com.example.model.PlayerRole
import com.example.model.Team
import com.example.model.TossDecision
import com.example.model.UserProfile
import com.example.model.WicketType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class CricketRepository(private val context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("fx_cricket_score_prefs", Context.MODE_PRIVATE)

  private val _matches = MutableStateFlow<List<CricketMatch>>(emptyList())
  val matches: StateFlow<List<CricketMatch>> = _matches.asStateFlow()

  private val _currentMatch = MutableStateFlow<CricketMatch?>(null)
  val currentMatch: StateFlow<CricketMatch?> = _currentMatch.asStateFlow()

  private val _teams = MutableStateFlow<List<Team>>(emptyList())
  val teams: StateFlow<List<Team>> = _teams.asStateFlow()

  private val _userProfile = MutableStateFlow(UserProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _milestoneAlert = MutableStateFlow<MilestoneAlert?>(null)
  val milestoneAlert: StateFlow<MilestoneAlert?> = _milestoneAlert.asStateFlow()

  private val _notifications = MutableStateFlow<List<String>>(emptyList())
  val notifications: StateFlow<List<String>> = _notifications.asStateFlow()

  init {
    loadData()
  }

  fun clearMilestoneAlert() {
    _milestoneAlert.value = null
  }

  fun dismissNotification(index: Int) {
    val list = _notifications.value.toMutableList()
    if (index in list.indices) {
      list.removeAt(index)
      _notifications.value = list
    }
  }

  fun clearAllNotifications() {
    _notifications.value = emptyList()
  }

  private fun addNotification(message: String) {
    if (_userProfile.value.isNotificationsEnabled) {
      _notifications.value = listOf(message) + _notifications.value.take(19)
    }
  }

  private fun loadData() {
    // Initial load from sample if empty
    val savedMatchesJson = prefs.getString("matches_cache", null)
    if (savedMatchesJson != null) {
      try {
        val parsedMatches = parseMatchesJson(savedMatchesJson)
        if (parsedMatches.isNotEmpty()) {
          _matches.value = parsedMatches
          val activeMatchId = prefs.getString("active_match_id", null)
          _currentMatch.value = parsedMatches.find { it.id == activeMatchId }
            ?: parsedMatches.find { it.status == MatchStatus.LIVE }
            ?: parsedMatches.firstOrNull()
        } else {
          loadDefaultSampleData()
        }
      } catch (e: Exception) {
        loadDefaultSampleData()
      }
    } else {
      loadDefaultSampleData()
    }

    // Teams
    val warriors = SampleData.getWarriorsTeam()
    val kings = SampleData.getKingsTeam()
    val thunder = SampleData.getThunderboltsTeam()
    val titans = SampleData.getTitansTeam()
    _teams.value = listOf(warriors, kings, thunder, titans)

    val darkMode = prefs.getBoolean("pref_dark_mode", true)
    val sound = prefs.getBoolean("pref_sound", true)
    val notifs = prefs.getBoolean("pref_notifs", true)
    _userProfile.value = _userProfile.value.copy(
      isDarkMode = darkMode,
      isSoundEnabled = sound,
      isNotificationsEnabled = notifs,
      totalMatchesScored = _matches.value.size
    )
  }

  private fun loadDefaultSampleData() {
    val live = SampleData.createLiveSampleMatch()
    val completed = SampleData.createCompletedSampleMatch()
    _matches.value = listOf(live, completed)
    _currentMatch.value = live
    saveMatchesToPrefs(_matches.value)
  }

  fun resetToSampleData() {
    loadDefaultSampleData()
  }

  fun toggleDarkMode(enabled: Boolean) {
    _userProfile.value = _userProfile.value.copy(isDarkMode = enabled)
    prefs.edit().putBoolean("pref_dark_mode", enabled).apply()
  }

  fun toggleSound(enabled: Boolean) {
    _userProfile.value = _userProfile.value.copy(isSoundEnabled = enabled)
    prefs.edit().putBoolean("pref_sound", enabled).apply()
  }

  fun toggleNotifications(enabled: Boolean) {
    _userProfile.value = _userProfile.value.copy(isNotificationsEnabled = enabled)
    prefs.edit().putBoolean("pref_notifs", enabled).apply()
  }

  fun selectCurrentMatch(match: CricketMatch) {
    _currentMatch.value = match
    prefs.edit().putString("active_match_id", match.id).apply()
  }

  fun selectMatch(matchId: String) {
    val m = _matches.value.find { it.id == matchId } ?: return
    selectCurrentMatch(m)
  }

  fun createNewMatch(
    matchName: String,
    matchType: MatchType,
    totalOvers: Int,
    team1Name: String,
    team2Name: String,
    team1PlayerNames: List<String>,
    team2PlayerNames: List<String>,
    venue: String,
    date: String,
    tossWinnerIndex: Int,
    tossDecision: TossDecision
  ): CricketMatch {
    return createMatch(
      name = matchName,
      type = matchType,
      overs = totalOvers,
      team1Name = team1Name,
      team2Name = team2Name,
      team1PlayersInput = team1PlayerNames,
      team2PlayersInput = team2PlayerNames,
      venue = venue,
      date = date,
      tossWinnerIndex = tossWinnerIndex,
      tossDecision = tossDecision
    )
  }

  fun endCurrentInnings() {
    endCurrentInningsEarly()
  }

  fun createMatch(
    name: String,
    type: MatchType,
    overs: Int,
    team1Name: String,
    team2Name: String,
    team1PlayersInput: List<String>,
    team2PlayersInput: List<String>,
    venue: String,
    date: String,
    tossWinnerIndex: Int, // 0 for team1, 1 for team2
    tossDecision: TossDecision
  ): CricketMatch {
    val t1Players = if (team1PlayersInput.isNotEmpty()) {
      team1PlayersInput.mapIndexed { idx, pName ->
        Player(
          id = "t1_p_$idx",
          name = pName.trim(),
          role = when {
            idx == 0 || idx == 1 -> PlayerRole.BATTER
            idx == 2 || idx == 3 -> PlayerRole.ALL_ROUNDER
            idx == 4 -> PlayerRole.WICKET_KEEPER
            else -> PlayerRole.BOWLER
          },
          isCaptain = idx == 0,
          isViceCaptain = idx == 1,
          battingOrder = idx + 1
        )
      }
    } else {
      SampleData.getWarriorsPlayers()
    }

    val t2Players = if (team2PlayersInput.isNotEmpty()) {
      team2PlayersInput.mapIndexed { idx, pName ->
        Player(
          id = "t2_p_$idx",
          name = pName.trim(),
          role = when {
            idx == 0 || idx == 1 -> PlayerRole.BATTER
            idx == 2 || idx == 3 -> PlayerRole.ALL_ROUNDER
            idx == 4 -> PlayerRole.WICKET_KEEPER
            else -> PlayerRole.BOWLER
          },
          isCaptain = idx == 0,
          isViceCaptain = idx == 1,
          battingOrder = idx + 1
        )
      }
    } else {
      SampleData.getKingsPlayers()
    }

    val team1 = Team(
      id = "team_t1_${System.currentTimeMillis()}",
      name = team1Name.ifBlank { "FX Warriors" },
      shortName = team1Name.take(3).uppercase().ifBlank { "FXW" },
      colorHex = 0xFF00E676,
      players = t1Players
    )

    val team2 = Team(
      id = "team_t2_${System.currentTimeMillis()}",
      name = team2Name.ifBlank { "Cricket Kings" },
      shortName = team2Name.take(3).uppercase().ifBlank { "CKG" },
      colorHex = 0xFF38BDF8,
      players = t2Players
    )

    val tossWinnerId = if (tossWinnerIndex == 0) team1.id else team2.id
    val isTeam1Batting = (tossWinnerId == team1.id && tossDecision == TossDecision.BAT) ||
      (tossWinnerId == team2.id && tossDecision == TossDecision.BOWL)

    val battingTeam = if (isTeam1Batting) team1 else team2
    val bowlingTeam = if (isTeam1Batting) team2 else team1

    val striker = battingTeam.players.getOrNull(0) ?: Player("b1", "Batter 1")
    val nonStriker = battingTeam.players.getOrNull(1) ?: Player("b2", "Batter 2")
    val openingBowler = bowlingTeam.players.find { it.role == PlayerRole.BOWLER || it.role == PlayerRole.ALL_ROUNDER }
      ?: bowlingTeam.players.lastOrNull()
      ?: Player("bw1", "Bowler 1")

    val innings1 = Innings(
      inningsNumber = 1,
      battingTeamId = battingTeam.id,
      battingTeamName = battingTeam.name,
      bowlingTeamId = bowlingTeam.id,
      bowlingTeamName = bowlingTeam.name,
      currentStrikerId = striker.id,
      currentNonStrikerId = nonStriker.id,
      currentBowlerId = openingBowler.id,
      batsmen = battingTeam.players,
      bowlers = bowlingTeam.players
    )

    val match = CricketMatch(
      id = "match_${System.currentTimeMillis()}",
      matchName = name.ifBlank { "Match #${_matches.value.size + 1}" },
      matchType = type,
      totalOvers = overs,
      team1 = team1,
      team2 = team2,
      venue = venue.ifBlank { "FX Cricket Arena" },
      dateStr = date.ifBlank { "Today" },
      tossWinnerTeamId = tossWinnerId,
      tossDecision = tossDecision,
      currentInningsIndex = 0,
      innings1 = innings1,
      status = MatchStatus.LIVE
    )

    val updatedList = listOf(match) + _matches.value
    _matches.value = updatedList
    _currentMatch.value = match
    saveMatchesToPrefs(updatedList)

    addNotification("Match '${match.matchName}' created and started!")
    return match
  }

  fun swapStrike() {
    val match = _currentMatch.value ?: return
    val inn = match.currentInnings
    val updatedInnings = inn.copy(
      currentStrikerId = inn.currentNonStrikerId,
      currentNonStrikerId = inn.currentStrikerId
    )
    updateMatchInnings(match, updatedInnings)
  }

  fun changeBowler(newBowlerId: String) {
    val match = _currentMatch.value ?: return
    val inn = match.currentInnings
    if (newBowlerId == inn.currentBowlerId) return

    val updatedInnings = inn.copy(
      previousBowlerId = inn.currentBowlerId,
      currentBowlerId = newBowlerId
    )
    updateMatchInnings(match, updatedInnings)
  }

  fun recordBall(
    runsScored: Int,
    extraType: ExtraType = ExtraType.NONE,
    extraRuns: Int = 0,
    isWicket: Boolean = false,
    wicketType: WicketType? = null,
    dismissedBatterId: String? = null,
    fielderName: String? = null,
    nextBatterId: String? = null
  ) {
    val current = _currentMatch.value ?: return
    if (current.status == MatchStatus.COMPLETED) return

    // Save snapshot for Undo
    val snapshot = CricketMatchSnapshot(
      innings1 = current.innings1,
      innings2 = current.innings2,
      currentInningsIndex = current.currentInningsIndex,
      status = current.status,
      resultMessage = current.resultMessage
    )
    val updatedSnapshots = (current.historyTimeline + snapshot).takeLast(30)

    val inn = current.currentInnings
    val isLegal = (extraType != ExtraType.WIDE && extraType != ExtraType.NO_BALL)

    val striker = inn.batsmen.find { it.id == inn.currentStrikerId }
      ?: inn.batsmen.firstOrNull() ?: Player("p1", "Striker")
    val nonStriker = inn.batsmen.find { it.id == inn.currentNonStrikerId }
      ?: inn.batsmen.getOrNull(1) ?: Player("p2", "Non-Striker")
    val bowler = inn.bowlers.find { it.id == inn.currentBowlerId }
      ?: inn.bowlers.firstOrNull() ?: Player("bw1", "Bowler")

    val totalBallRuns = when (extraType) {
      ExtraType.NONE -> runsScored
      ExtraType.WIDE -> 1 + extraRuns // 1 penalty + extras
      ExtraType.NO_BALL -> 1 + runsScored + extraRuns // 1 penalty + bat runs + extras
      ExtraType.BYE, ExtraType.LEG_BYE -> extraRuns.coerceAtLeast(1) // Runs scored as byes/leg-byes
    }

    // Update Team Total & Extras
    val newTotalRuns = inn.totalRuns + totalBallRuns
    val updatedExtras = when (extraType) {
      ExtraType.WIDE -> inn.extras.copy(wides = inn.extras.wides + 1 + extraRuns)
      ExtraType.NO_BALL -> inn.extras.copy(noBalls = inn.extras.noBalls + 1 + extraRuns)
      ExtraType.BYE -> inn.extras.copy(byes = inn.extras.byes + extraRuns.coerceAtLeast(1))
      ExtraType.LEG_BYE -> inn.extras.copy(legByes = inn.extras.legByes + extraRuns.coerceAtLeast(1))
      ExtraType.NONE -> inn.extras
    }

    // Update Batsman
    var updatedStriker = striker
    if (extraType != ExtraType.WIDE) {
      // Striker faced a ball (except on a wide)
      val addedRuns = if (extraType == ExtraType.BYE || extraType == ExtraType.LEG_BYE) 0 else runsScored
      val isFour = addedRuns == 4
      val isSix = addedRuns == 6

      updatedStriker = striker.copy(
        runs = striker.runs + addedRuns,
        ballsFaced = striker.ballsFaced + 1,
        fours = striker.fours + (if (isFour) 1 else 0),
        sixes = striker.sixes + (if (isSix) 1 else 0)
      )

      // Milestone check for batsman
      if (striker.runs < 50 && updatedStriker.runs >= 50) {
        _milestoneAlert.value = MilestoneAlert(MilestoneType.FIFTY, "⭐ HALF-CENTURY!", "${updatedStriker.name} reached 50 off ${updatedStriker.ballsFaced} balls!")
        addNotification("⭐ Milestone: ${updatedStriker.name} scored a Half-Century!")
      } else if (striker.runs < 100 && updatedStriker.runs >= 100) {
        _milestoneAlert.value = MilestoneAlert(MilestoneType.CENTURY, "💯 CENTURY!", "Magnificent 100 for ${updatedStriker.name}!")
        addNotification("💯 Milestone: ${updatedStriker.name} scored a Century!")
      } else if (isSix) {
        _milestoneAlert.value = MilestoneAlert(MilestoneType.SIX, "🔥 HUGE SIX!", "${updatedStriker.name} hammers it over the ropes!")
      } else if (isFour) {
        _milestoneAlert.value = MilestoneAlert(MilestoneType.FOUR, "⚡ FOUR!", "Cracking boundary by ${updatedStriker.name}!")
      }
    }

    // Update Bowler
    val bowlerRunsConceded = when (extraType) {
      ExtraType.NONE -> runsScored
      ExtraType.WIDE -> 1 + extraRuns
      ExtraType.NO_BALL -> 1 + runsScored
      ExtraType.BYE, ExtraType.LEG_BYE -> 0 // Byes/leg-byes don't count against bowler
    }

    var updatedBowler = bowler.copy(
      ballsBowled = bowler.ballsBowled + (if (isLegal) 1 else 0),
      runsConceded = bowler.runsConceded + bowlerRunsConceded,
      widesConceded = bowler.widesConceded + (if (extraType == ExtraType.WIDE) 1 + extraRuns else 0),
      noBallsConceded = bowler.noBallsConceded + (if (extraType == ExtraType.NO_BALL) 1 else 0)
    )

    // Wicket handling
    var newWickets = inn.wickets
    val fallOfWickets = inn.fallOfWickets.toMutableList()
    var targetStrikerId = inn.currentStrikerId
    var targetNonStrikerId = inn.currentNonStrikerId

    val updatedBatsmenList = inn.batsmen.toMutableList()
    val updatedBowlersList = inn.bowlers.toMutableList()

    if (isWicket) {
      newWickets += 1
      val dismissedId = dismissedBatterId ?: inn.currentStrikerId
      val dismissedBatter = if (dismissedId == inn.currentStrikerId) updatedStriker else nonStriker

      val isBowlerWicket = wicketType != WicketType.RUN_OUT && wicketType != WicketType.RETIRED_HURT
      if (isBowlerWicket) {
        updatedBowler = updatedBowler.copy(wickets = updatedBowler.wickets + 1)
      }

      val dismissalText = when (wicketType) {
        WicketType.BOWLED -> "b ${updatedBowler.name}"
        WicketType.CAUGHT -> if (!fielderName.isNullOrBlank()) "c $fielderName b ${updatedBowler.name}" else "c & b ${updatedBowler.name}"
        WicketType.LBW -> "lbw b ${updatedBowler.name}"
        WicketType.RUN_OUT -> if (!fielderName.isNullOrBlank()) "run out ($fielderName)" else "run out"
        WicketType.STUMPED -> if (!fielderName.isNullOrBlank()) "st $fielderName b ${updatedBowler.name}" else "stumped b ${updatedBowler.name}"
        WicketType.HIT_WICKET -> "hit wicket b ${updatedBowler.name}"
        WicketType.RETIRED_HURT -> "retired hurt"
        null -> "out"
      }

      val markedDismissedBatter = dismissedBatter.copy(
        isOut = true,
        dismissalText = dismissalText
      )

      val overText = "${inn.legalBalls / 6}.${(inn.legalBalls % 6) + (if (isLegal) 1 else 0)}"
      fallOfWickets.add(
        FallOfWicket(
          wicketNumber = newWickets,
          runs = newTotalRuns,
          overs = overText,
          batsmanName = markedDismissedBatter.name
        )
      )

      _milestoneAlert.value = MilestoneAlert(
        MilestoneType.WICKET,
        "🏏 WICKET!",
        "${markedDismissedBatter.name} is dismissed! ($dismissalText)"
      )
      addNotification("🏏 Wicket: ${markedDismissedBatter.name} dismissed at $newTotalRuns/$newWickets")

      // Replace dismissed batter in list
      val dismissedIndex = updatedBatsmenList.indexOfFirst { it.id == dismissedId }
      if (dismissedIndex != -1) {
        updatedBatsmenList[dismissedIndex] = markedDismissedBatter
      }

      // Assign next batter if provided or find first unbatted player
      val incomingBatter = if (!nextBatterId.isNullOrBlank()) {
        updatedBatsmenList.find { it.id == nextBatterId }
      } else {
        updatedBatsmenList.find { !it.isOut && it.id != targetStrikerId && it.id != targetNonStrikerId }
      }

      if (incomingBatter != null) {
        if (dismissedId == targetStrikerId) {
          targetStrikerId = incomingBatter.id
          updatedStriker = incomingBatter
        } else {
          targetNonStrikerId = incomingBatter.id
        }
      }
    } else {
      // Update striker in batsmen list
      val strikerIndex = updatedBatsmenList.indexOfFirst { it.id == updatedStriker.id }
      if (strikerIndex != -1) {
        updatedBatsmenList[strikerIndex] = updatedStriker
      }
    }

    // Update bowler in bowlers list
    val bowlerIndex = updatedBowlersList.indexOfFirst { it.id == updatedBowler.id }
    if (bowlerIndex != -1) {
      updatedBowlersList[bowlerIndex] = updatedBowler
    } else {
      updatedBowlersList.add(updatedBowler)
    }

    val newLegalBalls = inn.legalBalls + (if (isLegal) 1 else 0)

    // Strike Rotation Logic
    var finalStrikerId = targetStrikerId
    var finalNonStrikerId = targetNonStrikerId

    // Odd runs on legal ball or extras rotate strike
    val effectiveRunsForStrike = when (extraType) {
      ExtraType.NONE, ExtraType.NO_BALL -> runsScored
      ExtraType.BYE, ExtraType.LEG_BYE, ExtraType.WIDE -> extraRuns
    }
    if (effectiveRunsForStrike % 2 != 0) {
      val temp = finalStrikerId
      finalStrikerId = finalNonStrikerId
      finalNonStrikerId = temp
    }

    // Check End of Over (6 legal balls)
    val isOverEnd = isLegal && (newLegalBalls % 6 == 0) && (newLegalBalls > 0)
    if (isOverEnd) {
      // End of over rotates strike again
      val temp = finalStrikerId
      finalStrikerId = finalNonStrikerId
      finalNonStrikerId = temp
    }

    // Delivery record
    val delivery = BallDelivery(
      id = "ball_${System.currentTimeMillis()}",
      ballIndex = inn.balls.size + 1,
      overNumber = (newLegalBalls - (if (isLegal) 1 else 0)) / 6,
      ballInOver = if (isLegal) ((newLegalBalls - 1) % 6) + 1 else (newLegalBalls % 6),
      isLegalBall = isLegal,
      batsmanId = striker.id,
      batsmanName = striker.name,
      bowlerId = bowler.id,
      bowlerName = bowler.name,
      runsScored = runsScored,
      extraType = extraType,
      extraRuns = extraRuns,
      isWicket = isWicket,
      wicketType = wicketType,
      dismissedPlayerId = dismissedBatterId,
      dismissedPlayerName = if (isWicket) updatedBatsmenList.find { it.id == dismissedBatterId }?.name else null,
      fielderName = fielderName,
      totalRunsOnBall = totalBallRuns,
      description = formatBallDescription(striker.name, bowler.name, runsScored, extraType, extraRuns, isWicket, wicketType)
    )

    val updatedBalls = inn.balls + delivery

    val updatedInnings = inn.copy(
      totalRuns = newTotalRuns,
      wickets = newWickets,
      legalBalls = newLegalBalls,
      currentStrikerId = finalStrikerId,
      currentNonStrikerId = finalNonStrikerId,
      currentBowlerId = updatedBowler.id,
      extras = updatedExtras,
      batsmen = updatedBatsmenList,
      bowlers = updatedBowlersList,
      balls = updatedBalls,
      fallOfWickets = fallOfWickets
    )

    // Check match status & innings completion
    processMatchProgression(current, updatedInnings, updatedSnapshots)
  }

  private fun processMatchProgression(
    match: CricketMatch,
    updatedInnings: Innings,
    updatedSnapshots: List<CricketMatchSnapshot>
  ) {
    val totalBallsAllowed = match.totalOvers * 6
    val totalWicketsPossible = (updatedInnings.batsmen.size - 1).coerceAtLeast(10)

    if (match.currentInningsIndex == 0) {
      // 1st Innings
      val isInnings1Finished = updatedInnings.legalBalls >= totalBallsAllowed || updatedInnings.wickets >= totalWicketsPossible

      if (isInnings1Finished) {
        val completedInnings1 = updatedInnings.copy(isCompleted = true)
        val target = completedInnings1.totalRuns + 1

        // Initialize Innings 2
        val battingTeam = if (match.team1.id == completedInnings1.battingTeamId) match.team2 else match.team1
        val bowlingTeam = if (match.team1.id == completedInnings1.battingTeamId) match.team1 else match.team2

        val innings2 = Innings(
          inningsNumber = 2,
          battingTeamId = battingTeam.id,
          battingTeamName = battingTeam.name,
          bowlingTeamId = bowlingTeam.id,
          bowlingTeamName = bowlingTeam.name,
          currentStrikerId = battingTeam.players.getOrNull(0)?.id ?: "",
          currentNonStrikerId = battingTeam.players.getOrNull(1)?.id ?: "",
          currentBowlerId = bowlingTeam.players.lastOrNull()?.id ?: "",
          batsmen = battingTeam.players,
          bowlers = bowlingTeam.players
        )

        val updatedMatch = match.copy(
          currentInningsIndex = 1,
          innings1 = completedInnings1,
          innings2 = innings2,
          targetRuns = target,
          status = MatchStatus.LIVE,
          historyTimeline = updatedSnapshots
        )
        updateMatchInList(updatedMatch)
        addNotification("Innings break! Target is $target runs for ${battingTeam.name}")
      } else {
        val updatedMatch = match.copy(
          innings1 = updatedInnings,
          historyTimeline = updatedSnapshots
        )
        updateMatchInList(updatedMatch)
      }
    } else {
      // 2nd Innings (Chasing)
      val target = match.targetRuns ?: (match.innings1.totalRuns + 1)
      val chasingTeamName = updatedInnings.battingTeamName
      val defendingTeamName = updatedInnings.bowlingTeamName

      val hasChasedDown = updatedInnings.totalRuns >= target
      val isAllOutOrBallsDone = updatedInnings.legalBalls >= totalBallsAllowed || updatedInnings.wickets >= totalWicketsPossible

      if (hasChasedDown || isAllOutOrBallsDone) {
        val completedInnings2 = updatedInnings.copy(isCompleted = true)
        val resultMessage = when {
          hasChasedDown -> {
            val wicketsRemaining = totalWicketsPossible - updatedInnings.wickets
            "🏆 $chasingTeamName won by $wicketsRemaining wicket${if (wicketsRemaining > 1) "s" else ""}"
          }
          updatedInnings.totalRuns == target - 1 -> {
            "🤝 Match Tied (Super Over required)"
          }
          else -> {
            val runsMargin = target - 1 - updatedInnings.totalRuns
            "🏆 $defendingTeamName won by $runsMargin run${if (runsMargin > 1) "s" else ""}"
          }
        }

        // Determine MVP (Player of the match)
        val mvp = calculatePlayerOfTheMatch(match.innings1, completedInnings2)

        val completedMatch = match.copy(
          innings2 = completedInnings2,
          status = MatchStatus.COMPLETED,
          resultMessage = resultMessage,
          playerOfTheMatchId = mvp?.first?.id,
          playerOfTheMatchName = mvp?.first?.name,
          playerOfTheMatchStats = mvp?.second,
          historyTimeline = updatedSnapshots
        )

        updateMatchInList(completedMatch)
        _milestoneAlert.value = MilestoneAlert(MilestoneType.VICTORY, "🏆 MATCH FINISHED!", resultMessage)
        addNotification(resultMessage)
      } else {
        val updatedMatch = match.copy(
          innings2 = updatedInnings,
          historyTimeline = updatedSnapshots
        )
        updateMatchInList(updatedMatch)
      }
    }
  }

  fun undoLastBall() {
    val current = _currentMatch.value ?: return
    if (current.historyTimeline.isEmpty()) return

    val lastSnapshot = current.historyTimeline.last()
    val remainingSnapshots = current.historyTimeline.dropLast(1)

    val revertedMatch = current.copy(
      innings1 = lastSnapshot.innings1,
      innings2 = lastSnapshot.innings2,
      currentInningsIndex = lastSnapshot.currentInningsIndex,
      status = lastSnapshot.status,
      resultMessage = lastSnapshot.resultMessage,
      historyTimeline = remainingSnapshots
    )

    updateMatchInList(revertedMatch)
  }

  fun endCurrentInningsEarly() {
    val current = _currentMatch.value ?: return
    if (current.status == MatchStatus.COMPLETED) return

    val inn = current.currentInnings
    val updatedInnings = inn.copy(isCompleted = true)
    processMatchProgression(current, updatedInnings, current.historyTimeline)
  }

  private fun calculatePlayerOfTheMatch(inn1: Innings, inn2: Innings): Pair<Player, String>? {
    val allPlayers = (inn1.batsmen + inn1.bowlers + inn2.batsmen + inn2.bowlers).distinctBy { it.id }
    if (allPlayers.isEmpty()) return null

    val scoredPlayers = allPlayers.map { p ->
      // Score = runs * 1 + fours * 1 + sixes * 2 + wickets * 25 + maidens * 10
      val matchRuns = (inn1.batsmen.find { it.id == p.id }?.runs ?: 0) + (inn2.batsmen.find { it.id == p.id }?.runs ?: 0)
      val matchBalls = (inn1.batsmen.find { it.id == p.id }?.ballsFaced ?: 0) + (inn2.batsmen.find { it.id == p.id }?.ballsFaced ?: 0)
      val matchWickets = (inn1.bowlers.find { it.id == p.id }?.wickets ?: 0) + (inn2.bowlers.find { it.id == p.id }?.wickets ?: 0)
      val matchFours = (inn1.batsmen.find { it.id == p.id }?.fours ?: 0) + (inn2.batsmen.find { it.id == p.id }?.fours ?: 0)
      val matchSixes = (inn1.batsmen.find { it.id == p.id }?.sixes ?: 0) + (inn2.batsmen.find { it.id == p.id }?.sixes ?: 0)

      val score = (matchRuns * 1.0) + (matchFours * 1.0) + (matchSixes * 2.0) + (matchWickets * 25.0)
      val desc = buildString {
        if (matchRuns > 0) append("$matchRuns runs ($matchBalls balls)")
        if (matchWickets > 0) {
          if (isNotEmpty()) append(", ")
          append("$matchWickets wickets")
        }
      }
      Triple(p, score, desc.ifBlank { "${p.name} - Key Performance" })
    }

    val top = scoredPlayers.maxByOrNull { it.second } ?: return null
    return Pair(top.first, top.third)
  }

  private fun updateMatchInnings(match: CricketMatch, updatedInnings: Innings) {
    val updatedMatch = if (match.currentInningsIndex == 0 || match.innings2 == null) {
      match.copy(innings1 = updatedInnings)
    } else {
      match.copy(innings2 = updatedInnings)
    }
    updateMatchInList(updatedMatch)
  }

  private fun updateMatchInList(match: CricketMatch) {
    val updated = _matches.value.map { if (it.id == match.id) match else it }
    _matches.value = updated
    if (_currentMatch.value?.id == match.id) {
      _currentMatch.value = match
    }
    saveMatchesToPrefs(updated)
  }

  fun deleteMatch(matchId: String) {
    val updated = _matches.value.filter { it.id != matchId }
    _matches.value = updated
    if (_currentMatch.value?.id == matchId) {
      _currentMatch.value = updated.firstOrNull()
    }
    saveMatchesToPrefs(updated)
  }

  fun addNewTeam(name: String, shortName: String, colorHex: Long, playerNames: List<String>) {
    val players = playerNames.mapIndexed { idx, pName ->
      Player(
        id = "cust_p_${System.currentTimeMillis()}_$idx",
        name = pName.trim(),
        role = when {
          idx < 3 -> PlayerRole.BATTER
          idx in 3..6 -> PlayerRole.ALL_ROUNDER
          idx == 7 -> PlayerRole.WICKET_KEEPER
          else -> PlayerRole.BOWLER
        },
        isCaptain = idx == 0,
        isViceCaptain = idx == 1
      )
    }
    val newTeam = Team(
      id = "cust_team_${System.currentTimeMillis()}",
      name = name,
      shortName = shortName.ifBlank { name.take(3).uppercase() },
      colorHex = colorHex,
      players = players
    )
    _teams.value = _teams.value + newTeam
    _userProfile.value = _userProfile.value.copy(teamsCreated = _userProfile.value.teamsCreated + 1)
  }

  private fun formatBallDescription(
    striker: String,
    bowler: String,
    runs: Int,
    extra: ExtraType,
    extraRuns: Int,
    isWicket: Boolean,
    wicketType: WicketType?
  ): String = when {
    isWicket -> "$bowler to $striker, OUT! (${wicketType?.displayName ?: "Wicket"})"
    extra == ExtraType.WIDE -> "$bowler bowls Wide (+${1 + extraRuns})"
    extra == ExtraType.NO_BALL -> "$bowler bowls No Ball (+${1 + runs + extraRuns})"
    extra == ExtraType.BYE -> "$bowler to $striker, $extraRuns Bye"
    extra == ExtraType.LEG_BYE -> "$bowler to $striker, $extraRuns Leg Bye"
    runs == 6 -> "$bowler to $striker, SIX RUNS! Smashed into the crowd!"
    runs == 4 -> "$bowler to $striker, FOUR RUNS! Pierces the gap!"
    runs == 0 -> "$bowler to $striker, no run, dot ball"
    else -> "$bowler to $striker, $runs run${if (runs > 1) "s" else ""}"
  }

  private fun saveMatchesToPrefs(matches: List<CricketMatch>) {
    try {
      val array = JSONArray()
      for (m in matches) {
        val obj = JSONObject()
        obj.put("id", m.id)
        obj.put("matchName", m.matchName)
        obj.put("matchType", m.matchType.name)
        obj.put("totalOvers", m.totalOvers)
        obj.put("venue", m.venue)
        obj.put("dateStr", m.dateStr)
        obj.put("tossWinnerTeamId", m.tossWinnerTeamId)
        obj.put("tossDecision", m.tossDecision.name)
        obj.put("currentInningsIndex", m.currentInningsIndex)
        obj.put("targetRuns", m.targetRuns ?: -1)
        obj.put("status", m.status.name)
        obj.put("resultMessage", m.resultMessage)
        obj.put("potmName", m.playerOfTheMatchName ?: "")
        obj.put("potmStats", m.playerOfTheMatchStats ?: "")

        // Team1
        val t1Obj = JSONObject()
        t1Obj.put("id", m.team1.id)
        t1Obj.put("name", m.team1.name)
        t1Obj.put("shortName", m.team1.shortName)
        t1Obj.put("colorHex", m.team1.colorHex)
        obj.put("team1", t1Obj)

        // Team2
        val t2Obj = JSONObject()
        t2Obj.put("id", m.team2.id)
        t2Obj.put("name", m.team2.name)
        t2Obj.put("shortName", m.team2.shortName)
        t2Obj.put("colorHex", m.team2.colorHex)
        obj.put("team2", t2Obj)

        // Innings 1
        obj.put("inn1_runs", m.innings1.totalRuns)
        obj.put("inn1_wickets", m.innings1.wickets)
        obj.put("inn1_balls", m.innings1.legalBalls)
        obj.put("inn1_batId", m.innings1.battingTeamId)
        obj.put("inn1_batName", m.innings1.battingTeamName)

        // Innings 2
        if (m.innings2 != null) {
          obj.put("has_inn2", true)
          obj.put("inn2_runs", m.innings2.totalRuns)
          obj.put("inn2_wickets", m.innings2.wickets)
          obj.put("inn2_balls", m.innings2.legalBalls)
          obj.put("inn2_batId", m.innings2.battingTeamId)
          obj.put("inn2_batName", m.innings2.battingTeamName)
        } else {
          obj.put("has_inn2", false)
        }

        array.put(obj)
      }
      prefs.edit().putString("matches_cache", array.toString()).apply()
    } catch (e: Exception) {
      // ignore
    }
  }

  private fun parseMatchesJson(jsonStr: String): List<CricketMatch> {
    val array = JSONArray(jsonStr)
    val list = mutableListOf<CricketMatch>()
    for (i in 0 until array.length()) {
      val obj = array.getJSONObject(i)
      val id = obj.getString("id")
      // If matches are sample matches, return full rich models
      if (id == "match_sample_live") {
        list.add(SampleData.createLiveSampleMatch())
        continue
      }
      if (id == "match_sample_completed") {
        list.add(SampleData.createCompletedSampleMatch())
        continue
      }

      val t1Obj = obj.getJSONObject("team1")
      val team1 = Team(
        id = t1Obj.getString("id"),
        name = t1Obj.getString("name"),
        shortName = t1Obj.optString("shortName", "T1"),
        colorHex = t1Obj.optLong("colorHex", 0xFF00E676),
        players = SampleData.getWarriorsPlayers()
      )

      val t2Obj = obj.getJSONObject("team2")
      val team2 = Team(
        id = t2Obj.getString("id"),
        name = t2Obj.getString("name"),
        shortName = t2Obj.optString("shortName", "T2"),
        colorHex = t2Obj.optLong("colorHex", 0xFF38BDF8),
        players = SampleData.getKingsPlayers()
      )

      val inn1BatId = obj.optString("inn1_batId", team1.id)
      val inn1BatName = obj.optString("inn1_batName", team1.name)
      val inn1BowlId = if (inn1BatId == team1.id) team2.id else team1.id
      val inn1BowlName = if (inn1BatId == team1.id) team2.name else team1.name

      val innings1 = Innings(
        inningsNumber = 1,
        battingTeamId = inn1BatId,
        battingTeamName = inn1BatName,
        bowlingTeamId = inn1BowlId,
        bowlingTeamName = inn1BowlName,
        totalRuns = obj.optInt("inn1_runs", 0),
        wickets = obj.optInt("inn1_wickets", 0),
        legalBalls = obj.optInt("inn1_balls", 0),
        currentStrikerId = team1.players.firstOrNull()?.id ?: "",
        currentNonStrikerId = team1.players.getOrNull(1)?.id ?: "",
        currentBowlerId = team2.players.firstOrNull()?.id ?: "",
        batsmen = if (inn1BatId == team1.id) team1.players else team2.players,
        bowlers = if (inn1BatId == team1.id) team2.players else team1.players
      )

      val hasInn2 = obj.optBoolean("has_inn2", false)
      val innings2 = if (hasInn2) {
        val inn2BatId = obj.optString("inn2_batId", team2.id)
        val inn2BatName = obj.optString("inn2_batName", team2.name)
        val inn2BowlId = if (inn2BatId == team1.id) team2.id else team1.id
        val inn2BowlName = if (inn2BatId == team1.id) team2.name else team1.name

        Innings(
          inningsNumber = 2,
          battingTeamId = inn2BatId,
          battingTeamName = inn2BatName,
          bowlingTeamId = inn2BowlId,
          bowlingTeamName = inn2BowlName,
          totalRuns = obj.optInt("inn2_runs", 0),
          wickets = obj.optInt("inn2_wickets", 0),
          legalBalls = obj.optInt("inn2_balls", 0),
          currentStrikerId = team2.players.firstOrNull()?.id ?: "",
          currentNonStrikerId = team2.players.getOrNull(1)?.id ?: "",
          currentBowlerId = team1.players.firstOrNull()?.id ?: "",
          batsmen = if (inn2BatId == team1.id) team1.players else team2.players,
          bowlers = if (inn2BatId == team1.id) team2.players else team1.players
        )
      } else null

      val target = obj.optInt("targetRuns", -1).let { if (it > 0) it else null }

      list.add(
        CricketMatch(
          id = id,
          matchName = obj.optString("matchName", "Cricket Match"),
          matchType = try { MatchType.valueOf(obj.optString("matchType", "T20")) } catch (e: Exception) { MatchType.T20 },
          totalOvers = obj.optInt("totalOvers", 20),
          team1 = team1,
          team2 = team2,
          venue = obj.optString("venue", "Stadium"),
          dateStr = obj.optString("dateStr", "Today"),
          tossWinnerTeamId = obj.optString("tossWinnerTeamId", team1.id),
          tossDecision = try { TossDecision.valueOf(obj.optString("tossDecision", "BAT")) } catch (e: Exception) { TossDecision.BAT },
          currentInningsIndex = obj.optInt("currentInningsIndex", 0),
          innings1 = innings1,
          innings2 = innings2,
          targetRuns = target,
          status = try { MatchStatus.valueOf(obj.optString("status", "LIVE")) } catch (e: Exception) { MatchStatus.LIVE },
          resultMessage = obj.optString("resultMessage", ""),
          playerOfTheMatchName = obj.optString("potmName", "").ifBlank { null },
          playerOfTheMatchStats = obj.optString("potmStats", "").ifBlank { null }
        )
      )
    }
    return list
  }
}
