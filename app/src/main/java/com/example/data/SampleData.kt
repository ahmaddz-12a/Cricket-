package com.example.data

import com.example.model.CricketMatch
import com.example.model.ExtraType
import com.example.model.ExtrasBreakdown
import com.example.model.FallOfWicket
import com.example.model.Innings
import com.example.model.MatchStatus
import com.example.model.MatchType
import com.example.model.Player
import com.example.model.PlayerRole
import com.example.model.Team
import com.example.model.TossDecision

object SampleData {

  fun getWarriorsPlayers(): List<Player> = listOf(
    Player(id = "w1", name = "Ahmed", role = PlayerRole.BATTER, isCaptain = true, runs = 45, ballsFaced = 32, fours = 5, sixes = 2, isOut = false, careerRuns = 840, careerWickets = 4),
    Player(id = "w2", name = "Zaky", role = PlayerRole.ALL_ROUNDER, isViceCaptain = true, runs = 28, ballsFaced = 19, fours = 3, sixes = 1, isOut = false, careerRuns = 612, careerWickets = 18),
    Player(id = "w3", name = "Tariq Khan", role = PlayerRole.BATTER, runs = 34, ballsFaced = 22, fours = 4, sixes = 1, isOut = true, dismissalText = "c Rahul b Sam", careerRuns = 520, careerWickets = 2),
    Player(id = "w4", name = "Bilal Asif", role = PlayerRole.WICKET_KEEPER, runs = 18, ballsFaced = 14, fours = 2, sixes = 0, isOut = true, dismissalText = "b Vikram", careerRuns = 410, careerWickets = 0),
    Player(id = "w5", name = "Hamza Ali", role = PlayerRole.ALL_ROUNDER, runs = 12, ballsFaced = 8, fours = 1, sixes = 1, isOut = true, dismissalText = "lbw b Sam", careerRuns = 380, careerWickets = 22),
    Player(id = "w6", name = "Imran Nazir", role = PlayerRole.BATTER, runs = 8, ballsFaced = 6, fours = 1, sixes = 0, isOut = true, dismissalText = "run out (Arjun)", careerRuns = 290, careerWickets = 1),
    Player(id = "w7", name = "Shahid Afridi", role = PlayerRole.ALL_ROUNDER, runs = 0, ballsFaced = 0, careerRuns = 750, careerWickets = 35),
    Player(id = "w8", name = "Rashid Khan", role = PlayerRole.BOWLER, ballsBowled = 24, maidens = 1, runsConceded = 22, wickets = 3, careerRuns = 190, careerWickets = 64),
    Player(id = "w9", name = "Wasim Akram", role = PlayerRole.BOWLER, ballsBowled = 24, maidens = 0, runsConceded = 28, wickets = 2, careerRuns = 150, careerWickets = 82),
    Player(id = "w10", name = "Shoaib Akhtar", role = PlayerRole.BOWLER, ballsBowled = 24, maidens = 0, runsConceded = 35, wickets = 2, careerRuns = 95, careerWickets = 76),
    Player(id = "w11", name = "Jasprit Bumrah", role = PlayerRole.BOWLER, ballsBowled = 24, maidens = 2, runsConceded = 18, wickets = 1, careerRuns = 60, careerWickets = 90)
  )

  fun getKingsPlayers(): List<Player> = listOf(
    Player(id = "k1", name = "Rahul", role = PlayerRole.ALL_ROUNDER, isCaptain = true, runs = 42, ballsFaced = 29, fours = 4, sixes = 2, ballsBowled = 20, maidens = 0, runsConceded = 28, wickets = 2, careerRuns = 920, careerWickets = 34),
    Player(id = "k2", name = "Sam Curran", role = PlayerRole.ALL_ROUNDER, isViceCaptain = true, runs = 31, ballsFaced = 24, fours = 3, sixes = 1, ballsBowled = 24, maidens = 0, runsConceded = 32, wickets = 2, careerRuns = 510, careerWickets = 40),
    Player(id = "k3", name = "Arjun Verma", role = PlayerRole.BATTER, runs = 25, ballsFaced = 18, fours = 3, sixes = 0, isOut = true, dismissalText = "c Bilal b Rashid", careerRuns = 440, careerWickets = 0),
    Player(id = "k4", name = "Vikram Rathore", role = PlayerRole.BOWLER, runs = 14, ballsFaced = 12, fours = 1, sixes = 0, ballsBowled = 24, maidens = 0, runsConceded = 34, wickets = 1, careerRuns = 180, careerWickets = 45),
    Player(id = "k5", name = "David Warner", role = PlayerRole.BATTER, runs = 10, ballsFaced = 9, fours = 1, sixes = 0, isOut = true, dismissalText = "b Wasim", careerRuns = 890, careerWickets = 0),
    Player(id = "k6", name = "Glenn Maxwell", role = PlayerRole.ALL_ROUNDER, runs = 8, ballsFaced = 5, fours = 0, sixes = 1, isOut = true, dismissalText = "c Ahmed b Rashid", careerRuns = 730, careerWickets = 25),
    Player(id = "k7", name = "Rishabh Pant", role = PlayerRole.WICKET_KEEPER, runs = 6, ballsFaced = 7, fours = 0, sixes = 0, isOut = true, dismissalText = "lbw b Shoaib", careerRuns = 620, careerWickets = 0),
    Player(id = "k8", name = "Mitchell Starc", role = PlayerRole.BOWLER, runs = 4, ballsFaced = 4, ballsBowled = 24, maidens = 0, runsConceded = 36, wickets = 0, careerRuns = 120, careerWickets = 58),
    Player(id = "k9", name = "Pat Cummins", role = PlayerRole.BOWLER, runs = 2, ballsFaced = 3, ballsBowled = 24, maidens = 0, runsConceded = 30, wickets = 0, careerRuns = 210, careerWickets = 52),
    Player(id = "k10", name = "Kagiso Rabada", role = PlayerRole.BOWLER, runs = 0, ballsFaced = 1, ballsBowled = 24, maidens = 0, runsConceded = 28, wickets = 0, careerRuns = 80, careerWickets = 60),
    Player(id = "k11", name = "Trent Boult", role = PlayerRole.BOWLER, runs = 0, ballsFaced = 0, ballsBowled = 24, maidens = 1, runsConceded = 25, wickets = 0, careerRuns = 50, careerWickets = 65)
  )

  fun getWarriorsTeam(): Team = Team(
    id = "team_warriors",
    name = "FX Warriors",
    shortName = "FXW",
    colorHex = 0xFF00E676,
    players = getWarriorsPlayers()
  )

  fun getKingsTeam(): Team = Team(
    id = "team_kings",
    name = "Cricket Kings",
    shortName = "CKG",
    colorHex = 0xFF38BDF8,
    players = getKingsPlayers()
  )

  fun getThunderboltsTeam(): Team = Team(
    id = "team_thunder",
    name = "Thunderbolts",
    shortName = "TBT",
    colorHex = 0xFFF59E0B,
    players = listOf(
      Player("t1", "Chris Gayle", PlayerRole.BATTER, isCaptain = true, careerRuns = 1200),
      Player("t2", "Andre Russell", PlayerRole.ALL_ROUNDER, isViceCaptain = true, careerRuns = 950, careerWickets = 48),
      Player("t3", "Sunil Narine", PlayerRole.BOWLER, careerRuns = 420, careerWickets = 82),
      Player("t4", "Kieron Pollard", PlayerRole.ALL_ROUNDER, careerRuns = 880, careerWickets = 38),
      Player("t5", "Nicholas Pooran", PlayerRole.WICKET_KEEPER, careerRuns = 610)
    )
  )

  fun getTitansTeam(): Team = Team(
    id = "team_titans",
    name = "Royal Titans",
    shortName = "RTT",
    colorHex = 0xFFA855F7,
    players = listOf(
      Player("rt1", "Virat Kohli", PlayerRole.BATTER, isCaptain = true, careerRuns = 1450),
      Player("rt2", "Rohit Sharma", PlayerRole.BATTER, isViceCaptain = true, careerRuns = 1380),
      Player("rt3", "Hardik Pandya", PlayerRole.ALL_ROUNDER, careerRuns = 790, careerWickets = 42),
      Player("rt4", "MS Dhoni", PlayerRole.WICKET_KEEPER, careerRuns = 1120),
      Player("rt5", "Ravindra Jadeja", PlayerRole.ALL_ROUNDER, careerRuns = 650, careerWickets = 72)
    )
  )

  fun createCompletedSampleMatch(): CricketMatch {
    val warriors = getWarriorsTeam()
    val kings = getKingsTeam()

    val innings1 = Innings(
      inningsNumber = 1,
      battingTeamId = warriors.id,
      battingTeamName = warriors.name,
      bowlingTeamId = kings.id,
      bowlingTeamName = kings.name,
      totalRuns = 156,
      wickets = 4,
      legalBalls = 110, // 18.2 overs
      currentStrikerId = "w1",
      currentNonStrikerId = "w2",
      currentBowlerId = "k1",
      extras = ExtrasBreakdown(wides = 6, noBalls = 2, byes = 2, legByes = 1),
      batsmen = warriors.players,
      bowlers = kings.players.filter { it.ballsBowled > 0 },
      fallOfWickets = listOf(
        FallOfWicket(1, 38, "4.2", "Tariq Khan"),
        FallOfWicket(2, 74, "9.1", "Bilal Asif"),
        FallOfWicket(3, 102, "13.4", "Hamza Ali"),
        FallOfWicket(4, 118, "15.0", "Imran Nazir")
      ),
      isCompleted = true
    )

    val innings2 = Innings(
      inningsNumber = 2,
      battingTeamId = kings.id,
      battingTeamName = kings.name,
      bowlingTeamId = warriors.id,
      bowlingTeamName = warriors.name,
      totalRuns = 142,
      wickets = 8,
      legalBalls = 120, // 20.0 overs
      currentStrikerId = "k8",
      currentNonStrikerId = "k9",
      currentBowlerId = "w8",
      extras = ExtrasBreakdown(wides = 8, noBalls = 1, byes = 3, legByes = 2),
      batsmen = kings.players,
      bowlers = warriors.players.filter { it.ballsBowled > 0 },
      fallOfWickets = listOf(
        FallOfWicket(1, 14, "2.1", "David Warner"),
        FallOfWicket(2, 42, "6.3", "Arjun Verma"),
        FallOfWicket(3, 68, "9.5", "Glenn Maxwell"),
        FallOfWicket(4, 82, "12.0", "Rishabh Pant"),
        FallOfWicket(5, 110, "15.2", "Rahul"),
        FallOfWicket(6, 125, "17.4", "Sam Curran"),
        FallOfWicket(7, 134, "18.5", "Vikram Rathore"),
        FallOfWicket(8, 140, "19.4", "Kagiso Rabada")
      ),
      isCompleted = true
    )

    return CricketMatch(
      id = "match_sample_completed",
      matchName = "Premier League Final 2026",
      matchType = MatchType.T20,
      totalOvers = 20,
      team1 = warriors,
      team2 = kings,
      venue = "National Cricket Stadium",
      dateStr = "Sep 10, 2026",
      tossWinnerTeamId = warriors.id,
      tossDecision = TossDecision.BAT,
      currentInningsIndex = 1,
      innings1 = innings1,
      innings2 = innings2,
      targetRuns = 157,
      status = MatchStatus.COMPLETED,
      resultMessage = "FX Warriors won by 14 runs",
      playerOfTheMatchId = "w1",
      playerOfTheMatchName = "Ahmed",
      playerOfTheMatchStats = "45 runs (32 balls), 5x4s, 2x6s"
    )
  }

  fun createLiveSampleMatch(): CricketMatch {
    val warriors = getWarriorsTeam()
    val kings = getKingsTeam()

    val innings1 = Innings(
      inningsNumber = 1,
      battingTeamId = kings.id,
      battingTeamName = kings.name,
      bowlingTeamId = warriors.id,
      bowlingTeamName = warriors.name,
      totalRuns = 179,
      wickets = 6,
      legalBalls = 120,
      extras = ExtrasBreakdown(wides = 5, noBalls = 1, byes = 2, legByes = 1),
      batsmen = kings.players,
      bowlers = warriors.players.filter { it.ballsBowled > 0 },
      isCompleted = true
    )

    val innings2 = Innings(
      inningsNumber = 2,
      battingTeamId = warriors.id,
      battingTeamName = warriors.name,
      bowlingTeamId = kings.id,
      bowlingTeamName = kings.name,
      totalRuns = 125,
      wickets = 4,
      legalBalls = 93, // 15.3 overs
      currentStrikerId = "w1", // Ahmed
      currentNonStrikerId = "w2", // Zaky
      currentBowlerId = "k1", // Rahul
      extras = ExtrasBreakdown(wides = 4, noBalls = 1, byes = 2, legByes = 0),
      batsmen = warriors.players,
      bowlers = listOf(
        Player("k1", "Rahul", PlayerRole.ALL_ROUNDER, ballsBowled = 20, maidens = 0, runsConceded = 28, wickets = 2),
        Player("k2", "Sam Curran", PlayerRole.ALL_ROUNDER, ballsBowled = 24, maidens = 0, runsConceded = 32, wickets = 1),
        Player("k4", "Vikram Rathore", PlayerRole.BOWLER, ballsBowled = 24, maidens = 0, runsConceded = 35, wickets = 1),
        Player("k8", "Mitchell Starc", PlayerRole.BOWLER, ballsBowled = 24, maidens = 1, runsConceded = 26, wickets = 0)
      ),
      fallOfWickets = listOf(
        FallOfWicket(1, 28, "3.4", "Tariq Khan"),
        FallOfWicket(2, 64, "7.2", "Bilal Asif"),
        FallOfWicket(3, 89, "10.5", "Hamza Ali"),
        FallOfWicket(4, 108, "13.2", "Imran Nazir")
      ),
      isCompleted = false
    )

    return CricketMatch(
      id = "match_sample_live",
      matchName = "Championship Semi-Final",
      matchType = MatchType.T20,
      totalOvers = 20,
      team1 = warriors,
      team2 = kings,
      venue = "Eden Arena",
      dateStr = "Today",
      tossWinnerTeamId = kings.id,
      tossDecision = TossDecision.BAT,
      currentInningsIndex = 1,
      innings1 = innings1,
      innings2 = innings2,
      targetRuns = 180,
      status = MatchStatus.LIVE,
      resultMessage = ""
    )
  }
}
