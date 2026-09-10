package com.example.model

enum class MatchType(val displayName: String, val defaultOvers: Int) {
  T20("T20", 20),
  T10("T10", 10),
  ODI("ODI", 50),
  CUSTOM("Custom Overs", 5)
}

enum class TossDecision(val displayName: String) {
  BAT("Bat"),
  BOWL("Bowl")
}

enum class PlayerRole(val displayName: String) {
  BATTER("Batter"),
  BOWLER("Bowler"),
  ALL_ROUNDER("All-rounder"),
  WICKET_KEEPER("Wicketkeeper")
}

enum class ExtraType(val shortName: String) {
  NONE(""),
  WIDE("Wd"),
  NO_BALL("Nb"),
  BYE("B"),
  LEG_BYE("Lb")
}

enum class WicketType(val displayName: String) {
  BOWLED("Bowled"),
  CAUGHT("Caught"),
  LBW("LBW"),
  RUN_OUT("Run Out"),
  STUMPED("Stumped"),
  HIT_WICKET("Hit Wicket"),
  RETIRED_HURT("Retired Hurt")
}

enum class MatchStatus(val displayName: String) {
  UPCOMING("Upcoming"),
  LIVE("Live"),
  INNINGS_BREAK("Innings Break"),
  COMPLETED("Completed")
}

data class Player(
  val id: String,
  val name: String,
  val role: PlayerRole = PlayerRole.ALL_ROUNDER,
  val isCaptain: Boolean = false,
  val isViceCaptain: Boolean = false,
  // Batting stats in current match
  val runs: Int = 0,
  val ballsFaced: Int = 0,
  val fours: Int = 0,
  val sixes: Int = 0,
  val isOut: Boolean = false,
  val dismissalText: String = "not out",
  val battingOrder: Int = 0,
  // Bowling stats in current match
  val ballsBowled: Int = 0,
  val maidens: Int = 0,
  val runsConceded: Int = 0,
  val wickets: Int = 0,
  val widesConceded: Int = 0,
  val noBallsConceded: Int = 0,
  // Career stats
  val matchesPlayed: Int = 1,
  val careerRuns: Int = 0,
  val careerWickets: Int = 0
) {
  val strikeRate: Double
    get() = if (ballsFaced > 0) (runs.toDouble() / ballsFaced) * 100.0 else 0.0

  val oversBowledString: String
    get() {
      val ov = ballsBowled / 6
      val balls = ballsBowled % 6
      return "$ov.$balls"
    }

  val economy: Double
    get() {
      val totalOversFloat = ballsBowled / 6.0
      return if (totalOversFloat > 0) runsConceded / totalOversFloat else 0.0
    }
}

data class Team(
  val id: String,
  val name: String,
  val shortName: String,
  val colorHex: Long = 0xFF10B981,
  val players: List<Player> = emptyList()
)

data class ExtrasBreakdown(
  val wides: Int = 0,
  val noBalls: Int = 0,
  val byes: Int = 0,
  val legByes: Int = 0,
  val penalty: Int = 0
) {
  val total: Int
    get() = wides + noBalls + byes + legByes + penalty
}

data class FallOfWicket(
  val wicketNumber: Int,
  val runs: Int,
  val overs: String,
  val batsmanName: String
)

data class BallDelivery(
  val id: String,
  val ballIndex: Int,
  val overNumber: Int,
  val ballInOver: Int,
  val isLegalBall: Boolean,
  val batsmanId: String,
  val batsmanName: String,
  val bowlerId: String,
  val bowlerName: String,
  val runsScored: Int,
  val extraType: ExtraType = ExtraType.NONE,
  val extraRuns: Int = 0,
  val isWicket: Boolean = false,
  val wicketType: WicketType? = null,
  val dismissedPlayerId: String? = null,
  val dismissedPlayerName: String? = null,
  val fielderName: String? = null,
  val totalRunsOnBall: Int = 0,
  val description: String = ""
)

data class Innings(
  val inningsNumber: Int,
  val battingTeamId: String,
  val battingTeamName: String,
  val bowlingTeamId: String,
  val bowlingTeamName: String,
  val totalRuns: Int = 0,
  val wickets: Int = 0,
  val legalBalls: Int = 0,
  val currentStrikerId: String = "",
  val currentNonStrikerId: String = "",
  val currentBowlerId: String = "",
  val previousBowlerId: String = "",
  val extras: ExtrasBreakdown = ExtrasBreakdown(),
  val batsmen: List<Player> = emptyList(),
  val bowlers: List<Player> = emptyList(),
  val balls: List<BallDelivery> = emptyList(),
  val fallOfWickets: List<FallOfWicket> = emptyList(),
  val isCompleted: Boolean = false
) {
  val oversString: String
    get() {
      val ov = legalBalls / 6
      val b = legalBalls % 6
      return "$ov.$b"
    }

  val oversFloat: Double
    get() = (legalBalls / 6) + (legalBalls % 6) / 6.0

  val runRate: Double
    get() = if (oversFloat > 0) totalRuns / oversFloat else 0.0
}

data class CricketMatch(
  val id: String,
  val matchName: String,
  val matchType: MatchType = MatchType.T20,
  val totalOvers: Int = 20,
  val team1: Team,
  val team2: Team,
  val venue: String = "FX Cricket Stadium",
  val dateStr: String = "Today",
  val tossWinnerTeamId: String = "",
  val tossDecision: TossDecision = TossDecision.BAT,
  val currentInningsIndex: Int = 0,
  val innings1: Innings,
  val innings2: Innings? = null,
  val targetRuns: Int? = null,
  val status: MatchStatus = MatchStatus.LIVE,
  val resultMessage: String = "",
  val playerOfTheMatchId: String? = null,
  val playerOfTheMatchName: String? = null,
  val playerOfTheMatchStats: String? = null,
  val historyTimeline: List<CricketMatchSnapshot> = emptyList()
) {
  val currentInnings: Innings
    get() = if (currentInningsIndex == 0 || innings2 == null) innings1 else innings2

  val battingTeam: Team
    get() = if (currentInnings.battingTeamId == team1.id) team1 else team2

  val bowlingTeam: Team
    get() = if (currentInnings.bowlingTeamId == team1.id) team1 else team2
}

data class CricketMatchSnapshot(
  val innings1: Innings,
  val innings2: Innings?,
  val currentInningsIndex: Int,
  val status: MatchStatus,
  val resultMessage: String
)

enum class MilestoneType {
  FOUR,
  SIX,
  WICKET,
  FIFTY,
  CENTURY,
  HAT_TRICK,
  VICTORY
}

data class MilestoneAlert(
  val type: MilestoneType,
  val title: String,
  val subtitle: String,
  val id: Long = System.currentTimeMillis()
)

data class UserProfile(
  val username: String = "Cricket Champion",
  val favoriteTeam: String = "FX Warriors",
  val totalMatchesScored: Int = 18,
  val teamsCreated: Int = 6,
  val matchesWon: Int = 12,
  val isDarkMode: Boolean = true,
  val isSoundEnabled: Boolean = true,
  val isNotificationsEnabled: Boolean = true
)
