import React, { createContext, useContext, useState, useEffect } from 'react';
import { Match, MatchFormat, TossDecision, ExtraType, DismissalType, BallRecord, Team } from '../types';
import { DEFAULT_TEAMS, INITIAL_MATCHES } from '../data/seedData';
import { soundManager } from '../utils/audio';

interface CricketContextType {
  matches: Match[];
  currentMatch: Match | null;
  teams: Team[];
  theme: 'dark' | 'light';
  soundEnabled: boolean;
  activeScreen: 'live' | 'scorecard' | 'create' | 'matches' | 'stats' | 'teams';
  milestoneBanner: string | null;
  overSummaryModal: boolean;
  wicketModalOpen: boolean;
  bowlerSelectModalOpen: boolean;
  setTheme: (theme: 'dark' | 'light') => void;
  setSoundEnabled: (enabled: boolean) => void;
  setActiveScreen: (screen: 'live' | 'scorecard' | 'create' | 'matches' | 'stats' | 'teams') => void;
  selectMatch: (matchId: string) => void;
  scoreBall: (runs: number, extraType?: ExtraType, extraRuns?: number, isWicket?: boolean, dismissalType?: DismissalType, outBatsmanId?: string) => void;
  undoLastBall: () => void;
  rotateStrike: () => void;
  setBowler: (bowlerId: string) => void;
  setStriker: (strikerId: string) => void;
  setNonStriker: (nonStrikerId: string) => void;
  createNewMatch: (title: string, venue: string, format: MatchFormat, overs: number, team1Id: string, team2Id: string, tossWinnerId: string, tossDecision: TossDecision) => void;
  setWicketModalOpen: (open: boolean) => void;
  setBowlerSelectModalOpen: (open: boolean) => void;
  setOverSummaryModal: (open: boolean) => void;
  dismissBanner: () => void;
}

const CricketContext = createContext<CricketContextType | undefined>(undefined);

const STORAGE_KEY = 'fx_cricket_matches_v2';
const THEME_KEY = 'fx_cricket_theme';

export const CricketProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [teams] = useState<Team[]>(DEFAULT_TEAMS);
  const [matches, setMatches] = useState<Match[]>(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) return JSON.parse(saved);
    } catch (e) {
      console.error(e);
    }
    return INITIAL_MATCHES;
  });

  const [currentMatchId, setCurrentMatchId] = useState<string>('match-live-1');
  const [theme, setThemeState] = useState<'dark' | 'light'>(() => {
    try {
      const saved = localStorage.getItem(THEME_KEY);
      if (saved === 'light' || saved === 'dark') return saved;
    } catch (e) {
      console.error(e);
    }
    return 'dark';
  });

  const [soundEnabled, setSoundEnabledState] = useState<boolean>(true);
  const [activeScreen, setActiveScreen] = useState<'live' | 'scorecard' | 'create' | 'matches' | 'stats' | 'teams'>('live');
  const [milestoneBanner, setMilestoneBanner] = useState<string | null>(null);
  const [overSummaryModal, setOverSummaryModal] = useState<boolean>(false);
  const [wicketModalOpen, setWicketModalOpen] = useState<boolean>(false);
  const [bowlerSelectModalOpen, setBowlerSelectModalOpen] = useState<boolean>(false);

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(matches));
    } catch (e) {
      console.error(e);
    }
  }, [matches]);

  useEffect(() => {
    try {
      localStorage.setItem(THEME_KEY, theme);
    } catch (e) {
      console.error(e);
    }
    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [theme]);

  const setTheme = (newTheme: 'dark' | 'light') => {
    setThemeState(newTheme);
  };

  const setSoundEnabled = (enabled: boolean) => {
    setSoundEnabledState(enabled);
    soundManager.soundEnabled = enabled;
  };

  const currentMatch = matches.find((m) => m.id === currentMatchId) || matches[0] || null;

  const selectMatch = (matchId: string) => {
    setCurrentMatchId(matchId);
    setActiveScreen('live');
  };

  const dismissBanner = () => setMilestoneBanner(null);

  const rotateStrike = () => {
    if (!currentMatch) return;
    setMatches((prev) =>
      prev.map((m) => {
        if (m.id !== currentMatch.id) return m;
        return {
          ...m,
          currentStrikerId: m.currentNonStrikerId,
          currentNonStrikerId: m.currentStrikerId,
        };
      })
    );
  };

  const setBowler = (bowlerId: string) => {
    if (!currentMatch) return;
    setMatches((prev) =>
      prev.map((m) => {
        if (m.id !== currentMatch.id) return m;
        return { ...m, currentBowlerId: bowlerId };
      })
    );
    setBowlerSelectModalOpen(false);
  };

  const setStriker = (strikerId: string) => {
    if (!currentMatch) return;
    setMatches((prev) =>
      prev.map((m) => {
        if (m.id !== currentMatch.id) return m;
        return { ...m, currentStrikerId: strikerId };
      })
    );
  };

  const setNonStriker = (nonStrikerId: string) => {
    if (!currentMatch) return;
    setMatches((prev) =>
      prev.map((m) => {
        if (m.id !== currentMatch.id) return m;
        return { ...m, currentNonStrikerId: nonStrikerId };
      })
    );
  };

  const createNewMatch = (
    title: string,
    venue: string,
    format: MatchFormat,
    overs: number,
    team1Id: string,
    team2Id: string,
    tossWinnerId: string,
    tossDecision: TossDecision
  ) => {
    const t1 = teams.find((t) => t.id === team1Id) || teams[0];
    const t2 = teams.find((t) => t.id === team2Id) || teams[1];

    const firstBatTeam =
      tossWinnerId === t1.id
        ? tossDecision === 'bat'
          ? t1
          : t2
        : tossDecision === 'bat'
        ? t2
        : t1;
    const secondBatTeam = firstBatTeam.id === t1.id ? t2 : t1;

    const newMatch: Match = {
      id: `match-${Date.now()}`,
      title: title || `${t1.shortName} vs ${t2.shortName} ${format}`,
      venue: venue || 'Cricket Stadium',
      format,
      totalOvers: overs,
      team1: t1,
      team2: t2,
      tossWinnerId,
      tossDecision,
      status: 'live',
      currentInningsIndex: 0,
      innings: [
        {
          teamId: firstBatTeam.id,
          teamName: firstBatTeam.name,
          runs: 0,
          wickets: 0,
          legalBalls: 0,
          batting: [
            { playerId: firstBatTeam.players[0].id, name: firstBatTeam.players[0].name, runs: 0, balls: 0, fours: 0, sixes: 0, isOut: false },
            { playerId: firstBatTeam.players[1].id, name: firstBatTeam.players[1].name, runs: 0, balls: 0, fours: 0, sixes: 0, isOut: false },
          ],
          bowling: [
            {
              playerId: secondBatTeam.players[secondBatTeam.players.length - 1].id,
              name: secondBatTeam.players[secondBatTeam.players.length - 1].name,
              oversBowled: 0,
              legalBalls: 0,
              maidens: 0,
              runsConceded: 0,
              wickets: 0,
              wides: 0,
              noBalls: 0,
            },
          ],
          fallOfWickets: [],
          extras: { wides: 0, noBalls: 0, byes: 0, legByes: 0, penalty: 0, total: 0 },
          recentBalls: [],
        },
        {
          teamId: secondBatTeam.id,
          teamName: secondBatTeam.name,
          runs: 0,
          wickets: 0,
          legalBalls: 0,
          batting: [],
          bowling: [],
          fallOfWickets: [],
          extras: { wides: 0, noBalls: 0, byes: 0, legByes: 0, penalty: 0, total: 0 },
          recentBalls: [],
        },
      ],
      currentStrikerId: firstBatTeam.players[0].id,
      currentNonStrikerId: firstBatTeam.players[1].id,
      currentBowlerId: secondBatTeam.players[secondBatTeam.players.length - 1].id,
      createdAt: new Date().toISOString(),
    };

    setMatches((prev) => [newMatch, ...prev]);
    setCurrentMatchId(newMatch.id);
    setActiveScreen('live');
  };

  const scoreBall = (
    runs: number,
    extraType?: ExtraType,
    extraRuns = 0,
    isWicket = false,
    dismissalType?: DismissalType,
    outBatsmanId?: string
  ) => {
    if (!currentMatch || currentMatch.status === 'completed') return;

    const inningsIdx = currentMatch.currentInningsIndex;
    const currentInnings = { ...currentMatch.innings[inningsIdx] };
    const battingTeam = currentInnings.teamId === currentMatch.team1.id ? currentMatch.team1 : currentMatch.team2;
    const bowlingTeam = currentInnings.teamId === currentMatch.team1.id ? currentMatch.team2 : currentMatch.team1;

    let striker = currentInnings.batting.find((b) => b.playerId === currentMatch.currentStrikerId);
    let nonStriker = currentInnings.batting.find((b) => b.playerId === currentMatch.currentNonStrikerId);

    // Initialize striker or non-striker in batting array if missing
    if (!striker && currentMatch.currentStrikerId) {
      const p = battingTeam.players.find((pl) => pl.id === currentMatch.currentStrikerId);
      if (p) {
        striker = { playerId: p.id, name: p.name, runs: 0, balls: 0, fours: 0, sixes: 0, isOut: false };
        currentInnings.batting = [...currentInnings.batting, striker];
      }
    }
    if (!nonStriker && currentMatch.currentNonStrikerId) {
      const p = battingTeam.players.find((pl) => pl.id === currentMatch.currentNonStrikerId);
      if (p) {
        nonStriker = { playerId: p.id, name: p.name, runs: 0, balls: 0, fours: 0, sixes: 0, isOut: false };
        currentInnings.batting = [...currentInnings.batting, nonStriker];
      }
    }

    let bowler = currentInnings.bowling.find((b) => b.playerId === currentMatch.currentBowlerId);
    if (!bowler && currentMatch.currentBowlerId) {
      const p = bowlingTeam.players.find((pl) => pl.id === currentMatch.currentBowlerId);
      if (p) {
        bowler = {
          playerId: p.id,
          name: p.name,
          oversBowled: 0,
          legalBalls: 0,
          maidens: 0,
          runsConceded: 0,
          wickets: 0,
          wides: 0,
          noBalls: 0,
        };
        currentInnings.bowling = [...currentInnings.bowling, bowler];
      }
    }

    const isLegalBall = !(extraType === 'wide' || extraType === 'no_ball');
    let totalBallRuns = runs;
    let extraRunsAdded = 0;

    const newExtras = { ...currentInnings.extras };
    if (extraType === 'wide') {
      extraRunsAdded = 1 + (extraRuns || runs);
      newExtras.wides += extraRunsAdded;
      newExtras.total += extraRunsAdded;
      totalBallRuns = extraRunsAdded;
      if (bowler) bowler.wides += 1;
    } else if (extraType === 'no_ball') {
      extraRunsAdded = 1 + extraRuns;
      newExtras.noBalls += 1;
      newExtras.total += extraRunsAdded;
      totalBallRuns = runs + extraRunsAdded;
      if (bowler) bowler.noBalls += 1;
    } else if (extraType === 'bye') {
      newExtras.byes += runs;
      newExtras.total += runs;
    } else if (extraType === 'leg_bye') {
      newExtras.legByes += runs;
      newExtras.total += runs;
    } else if (extraType === 'penalty') {
      newExtras.penalty += 5;
      newExtras.total += 5;
      totalBallRuns += 5;
    }

    // Update batsman score (only off the bat if not bye/leg-bye/wide)
    let batRuns = 0;
    if (!extraType || extraType === 'no_ball') {
      batRuns = runs;
    }

    if (striker && extraType !== 'wide') {
      striker.runs += batRuns;
      striker.balls += 1;
      if (batRuns === 4) {
        striker.fours += 1;
        soundManager.playBoundaryFour();
      } else if (batRuns === 6) {
        striker.sixes += 1;
        soundManager.playSix();
      } else if (batRuns > 0) {
        soundManager.playBatHit();
      }

      // Check milestones
      if (striker.runs >= 50 && striker.runs - batRuns < 50) {
        soundManager.playMilestone();
        setMilestoneBanner(`50 UP! Sensational half-century for ${striker.name}! 🏏🔥`);
      } else if (striker.runs >= 100 && striker.runs - batRuns < 100) {
        soundManager.playMilestone();
        setMilestoneBanner(`CENTURY! Magnificent 100 for ${striker.name}! 💯🏏`);
      }
    }

    // Update bowler stats
    if (bowler) {
      if (isLegalBall) {
        bowler.legalBalls += 1;
        const completeOvers = Math.floor(bowler.legalBalls / 6);
        const remBalls = bowler.legalBalls % 6;
        bowler.oversBowled = Number(`${completeOvers}.${remBalls}`);
      }
      // Bowler conceded runs: includes bat runs + wides + no balls (not byes or leg byes)
      if (extraType === 'wide' || extraType === 'no_ball' || !extraType) {
        bowler.runsConceded += totalBallRuns;
      }
    }

    // Handle wicket
    let wicketFell = false;
    let newStrikerId = currentMatch.currentStrikerId;
    let newNonStrikerId = currentMatch.currentNonStrikerId;

    if (isWicket) {
      soundManager.playWicket();
      wicketFell = true;
      const whoOut = outBatsmanId === currentMatch.currentNonStrikerId ? nonStriker : striker;
      if (whoOut) {
        whoOut.isOut = true;
        const dType = dismissalType || 'bowled';
        let desc = `${dType} b ${bowler?.name || 'Bowler'}`;
        if (dType === 'run_out') desc = 'run out';
        whoOut.dismissal = {
          type: dType,
          bowlerName: bowler?.name,
          description: desc,
        };
      }

      if (bowler && dismissalType !== 'run_out') {
        bowler.wickets += 1;
      }

      const fallScore = currentInnings.runs + totalBallRuns;
      const oversFormatted = `${Math.floor((currentInnings.legalBalls + (isLegalBall ? 1 : 0)) / 6)}.${(currentInnings.legalBalls + (isLegalBall ? 1 : 0)) % 6}`;
      currentInnings.fallOfWickets = [
        ...currentInnings.fallOfWickets,
        {
          wicketNumber: currentInnings.wickets + 1,
          score: fallScore,
          overs: oversFormatted,
          batsmanName: whoOut?.name || 'Batsman',
        },
      ];

      // Find next available batsman
      const battedIds = new Set(currentInnings.batting.map((b) => b.playerId));
      const nextPlayer = battingTeam.players.find((p) => !battedIds.has(p.id));
      if (nextPlayer) {
        const newBatsmanStats: BatsmanStats = {
          playerId: nextPlayer.id,
          name: nextPlayer.name,
          runs: 0,
          balls: 0,
          fours: 0,
          sixes: 0,
          isOut: false,
        };
        currentInnings.batting.push(newBatsmanStats);
        if (whoOut?.playerId === currentMatch.currentStrikerId) {
          newStrikerId = nextPlayer.id;
        } else {
          newNonStrikerId = nextPlayer.id;
        }
      }
    }

    // Rotate strike on odd runs if not out or after runs completed
    let strikeRotated = false;
    if (runs % 2 === 1) {
      const temp = newStrikerId;
      newStrikerId = newNonStrikerId;
      newNonStrikerId = temp;
      strikeRotated = true;
    }

    // Prepare ball label
    let ballLabel = runs.toString();
    if (isWicket) {
      ballLabel = runs > 0 ? `${runs}W` : 'W';
    } else if (extraType === 'wide') {
      ballLabel = extraRunsAdded > 1 ? `Wd+${extraRunsAdded - 1}` : 'Wd';
    } else if (extraType === 'no_ball') {
      ballLabel = runs > 0 ? `Nb+${runs}` : 'Nb';
    } else if (extraType === 'bye') {
      ballLabel = `B${runs}`;
    } else if (extraType === 'leg_bye') {
      ballLabel = `Lb${runs}`;
    }

    const newBallRecord: BallRecord = {
      id: `ball-${Date.now()}-${Math.random()}`,
      ballNumberInOver: isLegalBall ? ((currentInnings.legalBalls) % 6) + 1 : (currentInnings.legalBalls % 6) + 1,
      overNumber: Math.floor(currentInnings.legalBalls / 6),
      runsScored: totalBallRuns,
      isLegal: isLegalBall,
      isExtra: !!extraType,
      extraType,
      extraRuns: extraRunsAdded,
      isWicket,
      dismissalType,
      outBatsmanId,
      strikerId: currentMatch.currentStrikerId,
      nonStrikerId: currentMatch.currentNonStrikerId,
      bowlerId: currentMatch.currentBowlerId,
      strikerName: striker?.name || 'Striker',
      bowlerName: bowler?.name || 'Bowler',
      commentary: isWicket
        ? `WICKET! ${striker?.name} falls!`
        : runs === 6
        ? `MAXIMUM! Massive strike by ${striker?.name} sails over the ropes!`
        : runs === 4
        ? `FOUR! Beautifully timed drive to the fence by ${striker?.name}!`
        : extraType === 'wide'
        ? `Wide signaled by the umpire.`
        : `${runs} run(s) scored.`,
      label: ballLabel,
    };

    currentInnings.runs += totalBallRuns;
    if (isLegalBall) {
      currentInnings.legalBalls += 1;
    }
    if (wicketFell) {
      currentInnings.wickets += 1;
    }
    currentInnings.extras = newExtras;
    currentInnings.recentBalls = [newBallRecord, ...currentInnings.recentBalls.slice(0, 23)];

    // Check over completion (6 legal balls)
    let isOverComplete = false;
    if (isLegalBall && currentInnings.legalBalls % 6 === 0) {
      isOverComplete = true;
      // Change strike at end of over
      const temp = newStrikerId;
      newStrikerId = newNonStrikerId;
      newNonStrikerId = temp;
      setOverSummaryModal(true);
      setBowlerSelectModalOpen(true);
    }

    // Check innings end or match end
    const maxBalls = currentMatch.totalOvers * 6;
    const allOut = currentInnings.wickets >= 10;
    const oversFinished = currentInnings.legalBalls >= maxBalls;

    let nextInningsIdx = inningsIdx;
    let matchStatus: Match['status'] = currentMatch.status;
    let resultMsg = currentMatch.resultMessage;

    // Check if target reached in 2nd innings
    if (inningsIdx === 1 && currentMatch.target) {
      if (currentInnings.runs >= currentMatch.target) {
        matchStatus = 'completed';
        const wicketsLeft = 10 - currentInnings.wickets;
        resultMsg = `${currentInnings.teamName} won by ${wicketsLeft} wicket${wicketsLeft === 1 ? '' : 's'}! 🏆`;
        soundManager.playMilestone();
      } else if (allOut || oversFinished) {
        matchStatus = 'completed';
        const runDeficit = currentMatch.target - 1 - currentInnings.runs;
        const firstTeam = currentMatch.innings[0].teamName;
        if (runDeficit === 0) {
          resultMsg = 'Match Tied! An absolute thriller! 🤝';
        } else {
          resultMsg = `${firstTeam} won by ${runDeficit} run${runDeficit === 1 ? '' : 's'}! 🏆`;
        }
        soundManager.playMilestone();
      }
    } else if (inningsIdx === 0 && (allOut || oversFinished)) {
      // 1st innings complete!
      nextInningsIdx = 1;
      const target = currentInnings.runs + 1;
      const secondTeam = bowlingTeam;
      // Initialize 2nd innings batting
      currentMatch.innings[1] = {
        teamId: secondTeam.id,
        teamName: secondTeam.name,
        runs: 0,
        wickets: 0,
        legalBalls: 0,
        batting: [
          { playerId: secondTeam.players[0].id, name: secondTeam.players[0].name, runs: 0, balls: 0, fours: 0, sixes: 0, isOut: false },
          { playerId: secondTeam.players[1].id, name: secondTeam.players[1].name, runs: 0, balls: 0, fours: 0, sixes: 0, isOut: false },
        ],
        bowling: [],
        fallOfWickets: [],
        extras: { wides: 0, noBalls: 0, byes: 0, legByes: 0, penalty: 0, total: 0 },
        recentBalls: [],
      };
      newStrikerId = secondTeam.players[0].id;
      newNonStrikerId = secondTeam.players[1].id;
      newNewBowlerId:
      newStrikerId = secondTeam.players[0].id;
      newNonStrikerId = secondTeam.players[1].id;
      resultMsg = `${secondTeam.name} need ${target} runs to win from ${currentMatch.totalOvers} overs`;
      setMilestoneBanner(`INNINGS BREAK! Target: ${target} runs 🎯`);
    }

    const updatedInnings: [typeof currentInnings, typeof currentInnings] = [...currentMatch.innings];
    updatedInnings[inningsIdx] = currentInnings;

    setMatches((prev) =>
      prev.map((m) => {
        if (m.id !== currentMatch.id) return m;
        return {
          ...m,
          innings: updatedInnings,
          currentInningsIndex: nextInningsIdx,
          currentStrikerId: newStrikerId,
          currentNonStrikerId: newNonStrikerId,
          target: inningsIdx === 0 && (allOut || oversFinished) ? currentInnings.runs + 1 : m.target,
          status: matchStatus,
          resultMessage: resultMsg,
        };
      })
    );
  };

  const undoLastBall = () => {
    if (!currentMatch) return;
    const inningsIdx = currentMatch.currentInningsIndex;
    const innings = { ...currentMatch.innings[inningsIdx] };
    if (!innings.recentBalls || innings.recentBalls.length === 0) return;

    const [lastBall, ...remainingBalls] = innings.recentBalls;
    innings.recentBalls = remainingBalls;
    innings.runs = Math.max(0, innings.runs - lastBall.runsScored);
    if (lastBall.isLegal) {
      innings.legalBalls = Math.max(0, innings.legalBalls - 1);
    }
    if (lastBall.isWicket) {
      innings.wickets = Math.max(0, innings.wickets - 1);
      innings.fallOfWickets = innings.fallOfWickets.slice(0, -1);
    }

    // Revert batsman runs
    const striker = innings.batting.find((b) => b.playerId === lastBall.strikerId);
    if (striker && !lastBall.isExtra) {
      striker.runs = Math.max(0, striker.runs - lastBall.runsScored);
      striker.balls = Math.max(0, striker.balls - 1);
      if (lastBall.runsScored === 4) striker.fours = Math.max(0, striker.fours - 1);
      if (lastBall.runsScored === 6) striker.sixes = Math.max(0, striker.sixes - 1);
    }
    if (lastBall.isWicket && striker) {
      striker.isOut = false;
      delete striker.dismissal;
    }

    // Revert bowler stats
    const bowler = innings.bowling.find((b) => b.playerId === lastBall.bowlerId);
    if (bowler) {
      if (lastBall.isLegal) {
        bowler.legalBalls = Math.max(0, bowler.legalBalls - 1);
        const comp = Math.floor(bowler.legalBalls / 6);
        const rem = bowler.legalBalls % 6;
        bowler.oversBowled = Number(`${comp}.${rem}`);
      }
      bowler.runsConceded = Math.max(0, bowler.runsConceded - lastBall.runsScored);
      if (lastBall.isWicket) {
        bowler.wickets = Math.max(0, bowler.wickets - 1);
      }
    }

    const updatedInnings: [typeof innings, typeof innings] = [...currentMatch.innings];
    updatedInnings[inningsIdx] = innings;

    setMatches((prev) =>
      prev.map((m) => {
        if (m.id !== currentMatch.id) return m;
        return {
          ...m,
          innings: updatedInnings,
          status: 'live',
        };
      })
    );
  };

  return (
    <CricketContext.Provider
      value={{
        matches,
        currentMatch,
        teams,
        theme,
        soundEnabled,
        activeScreen,
        milestoneBanner,
        overSummaryModal,
        wicketModalOpen,
        bowlerSelectModalOpen,
        setTheme,
        setSoundEnabled,
        setActiveScreen,
        selectMatch,
        scoreBall,
        undoLastBall,
        rotateStrike,
        setBowler,
        setStriker,
        setNonStriker,
        createNewMatch,
        setWicketModalOpen,
        setBowlerSelectModalOpen,
        setOverSummaryModal,
        dismissBanner,
      }}
    >
      {children}
    </CricketContext.Provider>
  );
};

export const useCricket = () => {
  const context = useContext(CricketContext);
  if (!context) throw new Error('useCricket must be used within a CricketProvider');
  return context;
};
