import React, { useState } from 'react';
import { useCricket } from '../context/CricketContext';
import {
  RotateCcw,
  RefreshCw,
  UserCheck,
  Zap,
  Activity,
  ChevronRight,
  Sparkles,
} from 'lucide-react';

export const LiveScoringScreen: React.FC = () => {
  const {
    currentMatch,
    scoreBall,
    undoLastBall,
    rotateStrike,
    setWicketModalOpen,
    setBowlerSelectModalOpen,
    setActiveScreen,
  } = useCricket();

  const [customExtraRuns, setCustomExtraRuns] = useState<number>(0);
  const [extraTypeActive, setExtraTypeActive] = useState<'wide' | 'no_ball' | 'bye' | 'leg_bye' | null>(null);

  if (!currentMatch) {
    return (
      <div className="flex h-96 flex-col items-center justify-center p-6 text-center">
        <Activity className="h-12 w-12 text-slate-600 animate-pulse mb-3" />
        <p className="text-slate-400 font-semibold">No active match found.</p>
        <button
          onClick={() => setActiveScreen('create')}
          className="mt-4 px-4 py-2 bg-emerald-500 text-white font-bold rounded-xl text-xs"
        >
          Create New Match
        </button>
      </div>
    );
  }

  const inningsIdx = currentMatch.currentInningsIndex;
  const currentInnings = currentMatch.innings[inningsIdx];
  const battingTeam = currentInnings.teamId === currentMatch.team1.id ? currentMatch.team1 : currentMatch.team2;
  const bowlingTeam = currentInnings.teamId === currentMatch.team1.id ? currentMatch.team2 : currentMatch.team1;

  const striker = currentInnings.batting.find((b) => b.playerId === currentMatch.currentStrikerId);
  const nonStriker = currentInnings.batting.find((b) => b.playerId === currentMatch.currentNonStrikerId);
  const bowler = currentInnings.bowling.find((b) => b.playerId === currentMatch.currentBowlerId);

  // Calculations
  const oversFormatted = `${Math.floor(currentInnings.legalBalls / 6)}.${currentInnings.legalBalls % 6}`;
  const totalOvers = currentMatch.totalOvers;
  const crr = currentInnings.legalBalls > 0 ? ((currentInnings.runs / currentInnings.legalBalls) * 6).toFixed(2) : '0.00';

  let rrr = '0.00';
  let runsNeeded = 0;
  let ballsRemaining = 0;
  if (inningsIdx === 1 && currentMatch.target) {
    runsNeeded = currentMatch.target - currentInnings.runs;
    ballsRemaining = totalOvers * 6 - currentInnings.legalBalls;
    rrr = ballsRemaining > 0 ? ((runsNeeded / ballsRemaining) * 6).toFixed(2) : '0.00';
  }

  const handleRunClick = (runs: number) => {
    if (extraTypeActive) {
      scoreBall(runs, extraTypeActive, runs);
      setExtraTypeActive(null);
    } else {
      scoreBall(runs);
    }
  };

  const handleExtraClick = (type: 'wide' | 'no_ball' | 'bye' | 'leg_bye') => {
    if (extraTypeActive === type) {
      setExtraTypeActive(null);
    } else {
      setExtraTypeActive(type);
    }
  };

  const strikeRate = (r: number, b: number) => (b > 0 ? ((r / b) * 100).toFixed(1) : '0.0');
  const bowlerEconomy = (r: number, lb: number) => (lb > 0 ? ((r / lb) * 6).toFixed(2) : '0.00');

  return (
    <div id="live-scoring-container" className="mx-auto max-w-4xl space-y-4 pb-20">
      {/* Top Match Card */}
      <div
        id="match-banner-card"
        className="relative overflow-hidden rounded-2xl border border-slate-800 bg-gradient-to-b from-slate-900 via-slate-900/90 to-slate-950 p-5 shadow-xl"
      >
        <div className="flex flex-wrap items-center justify-between gap-2 border-b border-slate-800 pb-3">
          <div className="flex items-center space-x-2">
            <span className="inline-flex items-center rounded-md bg-emerald-500/10 px-2 py-0.5 text-xs font-bold text-emerald-400 border border-emerald-500/20">
              <span className="mr-1.5 h-1.5 w-1.5 rounded-full bg-emerald-400 animate-ping"></span>
              {currentMatch.status === 'completed' ? 'MATCH ENDED' : 'LIVE SCORING'}
            </span>
            <span className="text-xs font-semibold text-slate-400">{currentMatch.title}</span>
          </div>
          <span className="text-xs text-slate-500">{currentMatch.venue}</span>
        </div>

        {/* Big Live Score Numbers */}
        <div className="mt-4 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <div className="flex items-baseline space-x-3">
              <span className="text-xs font-bold uppercase tracking-wider text-emerald-400">
                {currentInnings.teamName}
              </span>
              <span className="text-xs text-slate-400">({inningsIdx === 0 ? '1st Innings' : '2nd Innings'})</span>
            </div>
            <div className="flex items-baseline space-x-3 mt-1">
              <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight text-white font-sans">
                {currentInnings.runs}
                <span className="text-slate-500">/</span>
                <span className="text-emerald-400">{currentInnings.wickets}</span>
              </h1>
              <span className="text-lg sm:text-xl font-bold text-slate-400">
                ({oversFormatted} / {totalOvers} ov)
              </span>
            </div>
          </div>

          {/* Rates & Targets */}
          <div className="flex items-center space-x-4 bg-slate-950/60 p-3 rounded-xl border border-slate-800/80">
            <div>
              <span className="text-[10px] uppercase font-bold text-slate-400 block">CRR</span>
              <span className="text-sm font-extrabold text-slate-200">{crr}</span>
            </div>

            {inningsIdx === 1 && currentMatch.target && (
              <>
                <div className="h-6 w-px bg-slate-800" />
                <div>
                  <span className="text-[10px] uppercase font-bold text-amber-400 block">REQ RR</span>
                  <span className="text-sm font-extrabold text-amber-300">{rrr}</span>
                </div>
                <div className="h-6 w-px bg-slate-800" />
                <div>
                  <span className="text-[10px] uppercase font-bold text-slate-400 block">TARGET</span>
                  <span className="text-sm font-extrabold text-white">{currentMatch.target}</span>
                </div>
              </>
            )}
          </div>
        </div>

        {/* Equation statement */}
        {inningsIdx === 1 && currentMatch.target && currentMatch.status !== 'completed' && (
          <div className="mt-3 rounded-lg bg-emerald-500/10 border border-emerald-500/20 px-3 py-1.5 text-xs font-semibold text-emerald-300 flex items-center justify-between">
            <span>
              Need <strong className="text-white font-bold">{runsNeeded}</strong> runs from{' '}
              <strong className="text-white font-bold">{ballsRemaining}</strong> balls
            </span>
            <span className="text-[11px] text-emerald-400/80">
              {10 - currentInnings.wickets} wkts in hand
            </span>
          </div>
        )}

        {currentMatch.status === 'completed' && currentMatch.resultMessage && (
          <div className="mt-3 rounded-lg bg-emerald-600 px-3 py-2 text-xs font-bold text-white shadow-lg shadow-emerald-600/30 flex items-center space-x-2">
            <Sparkles className="h-4 w-4 text-yellow-300" />
            <span>{currentMatch.resultMessage}</span>
          </div>
        )}
      </div>

      {/* Batsman & Bowler Active Strip */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Batsmen On Pitch */}
        <div
          id="batsmen-active-card"
          className="rounded-2xl border border-slate-800 bg-slate-900/90 p-4 shadow-lg space-y-3"
        >
          <div className="flex items-center justify-between border-b border-slate-800 pb-2">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400 flex items-center space-x-1.5">
              <span>Batting</span>
            </span>
            <button
              id="rotate-strike-quick-btn"
              onClick={rotateStrike}
              className="flex items-center space-x-1 text-[11px] font-semibold text-emerald-400 hover:text-emerald-300 bg-emerald-500/10 px-2 py-1 rounded-lg border border-emerald-500/20"
            >
              <RefreshCw className="h-3 w-3" />
              <span>Rotate Strike</span>
            </button>
          </div>

          <div className="space-y-2">
            {/* Striker */}
            <div
              id="striker-row"
              className="flex items-center justify-between p-2.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30"
            >
              <div className="flex items-center space-x-2 min-w-0">
                <span className="h-2 w-2 rounded-full bg-emerald-400 animate-pulse"></span>
                <div className="truncate">
                  <div className="flex items-center space-x-1.5">
                    <span className="font-bold text-sm text-white truncate">
                      {striker?.name || 'Select Striker'}
                    </span>
                    <span className="text-[10px] font-extrabold text-emerald-400 bg-emerald-950 px-1 py-0.2 rounded border border-emerald-500/30">
                      *
                    </span>
                  </div>
                  <span className="text-[11px] text-slate-400">
                    SR: {strikeRate(striker?.runs || 0, striker?.balls || 0)}
                  </span>
                </div>
              </div>

              <div className="text-right">
                <span className="text-lg font-black text-white">
                  {striker?.runs || 0}
                  <span className="text-xs font-normal text-slate-400 ml-1">({striker?.balls || 0})</span>
                </span>
                <div className="text-[10px] text-slate-400 space-x-1.5">
                  <span>4s: <strong className="text-slate-200">{striker?.fours || 0}</strong></span>
                  <span>6s: <strong className="text-slate-200">{striker?.sixes || 0}</strong></span>
                </div>
              </div>
            </div>

            {/* Non-Striker */}
            <div
              id="non-striker-row"
              className="flex items-center justify-between p-2.5 rounded-xl bg-slate-950/60 border border-slate-800/80"
            >
              <div className="min-w-0">
                <span className="font-semibold text-sm text-slate-300 truncate block">
                  {nonStriker?.name || 'Select Non-Striker'}
                </span>
                <span className="text-[11px] text-slate-400">
                  SR: {strikeRate(nonStriker?.runs || 0, nonStriker?.balls || 0)}
                </span>
              </div>

              <div className="text-right">
                <span className="text-lg font-black text-slate-300">
                  {nonStriker?.runs || 0}
                  <span className="text-xs font-normal text-slate-500 ml-1">({nonStriker?.balls || 0})</span>
                </span>
                <div className="text-[10px] text-slate-400 space-x-1.5">
                  <span>4s: <strong className="text-slate-200">{nonStriker?.fours || 0}</strong></span>
                  <span>6s: <strong className="text-slate-200">{nonStriker?.sixes || 0}</strong></span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Current Bowler Card */}
        <div
          id="bowler-active-card"
          className="rounded-2xl border border-slate-800 bg-slate-900/90 p-4 shadow-lg space-y-3"
        >
          <div className="flex items-center justify-between border-b border-slate-800 pb-2">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-400">
              Bowling ({bowlingTeam.shortName})
            </span>
            <button
              id="change-bowler-btn"
              onClick={() => setBowlerSelectModalOpen(true)}
              className="flex items-center space-x-1 text-[11px] font-semibold text-emerald-400 hover:text-emerald-300 bg-emerald-500/10 px-2 py-1 rounded-lg border border-emerald-500/20"
            >
              <UserCheck className="h-3 w-3" />
              <span>Change Bowler</span>
            </button>
          </div>

          <div className="p-3 rounded-xl bg-slate-950/70 border border-slate-800/90">
            <div className="flex items-center justify-between">
              <div>
                <span className="font-bold text-sm text-white block truncate">
                  {bowler?.name || 'Select Bowler'}
                </span>
                <span className="text-xs text-slate-400">
                  Econ: {bowlerEconomy(bowler?.runsConceded || 0, bowler?.legalBalls || 0)}
                </span>
              </div>
              <div className="text-right">
                <span className="text-lg font-black text-emerald-400">
                  {bowler?.wickets || 0}
                  <span className="text-slate-500">/</span>
                  <span className="text-white">{bowler?.runsConceded || 0}</span>
                </span>
                <span className="text-xs text-slate-400 block font-medium">
                  {bowler?.oversBowled || 0} ov
                </span>
              </div>
            </div>

            <div className="mt-3 flex items-center justify-between text-[11px] text-slate-400 border-t border-slate-800/80 pt-2">
              <span>Maidens: <strong className="text-slate-200">{bowler?.maidens || 0}</strong></span>
              <span>Wides: <strong className="text-slate-200">{bowler?.wides || 0}</strong></span>
              <span>No Balls: <strong className="text-slate-200">{bowler?.noBalls || 0}</strong></span>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Balls Timeline */}
      <div
        id="recent-balls-card"
        className="rounded-2xl border border-slate-800 bg-slate-900/70 p-3.5 shadow-md"
      >
        <div className="flex items-center justify-between mb-2">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-400">This Over / Recent</span>
          <span className="text-xs text-slate-500">Latest deliveries first</span>
        </div>

        <div className="flex items-center space-x-2 overflow-x-auto pb-1">
          {currentInnings.recentBalls.length === 0 ? (
            <span className="text-xs text-slate-500 italic">No balls in this innings yet.</span>
          ) : (
            currentInnings.recentBalls.slice(0, 16).map((b) => (
              <div
                key={b.id}
                title={`${b.strikerName}: ${b.commentary}`}
                className={`flex h-8 min-w-8 items-center justify-center rounded-full text-xs font-extrabold shadow-sm transition-transform hover:scale-110 ${
                  b.isWicket
                    ? 'bg-red-600 text-white ring-2 ring-red-400/40'
                    : b.runsScored === 4
                    ? 'bg-sky-600 text-white'
                    : b.runsScored === 6
                    ? 'bg-purple-600 text-white ring-2 ring-purple-400/40'
                    : b.isExtra
                    ? 'bg-amber-600 text-white'
                    : b.runsScored === 0
                    ? 'bg-slate-800 text-slate-300 border border-slate-700'
                    : 'bg-emerald-700 text-white'
                }`}
              >
                {b.label}
              </div>
            ))
          )}
        </div>
      </div>

      {/* Scoring Keypad Controls */}
      <div
        id="scoring-keypad-card"
        className="rounded-2xl border border-slate-800 bg-slate-900 p-4 shadow-xl space-y-3"
      >
        <div className="flex items-center justify-between border-b border-slate-800 pb-2">
          <div className="flex items-center space-x-2">
            <Zap className="h-4 w-4 text-emerald-400" />
            <span className="text-xs font-bold uppercase tracking-wider text-white">Scoring Controls</span>
            {extraTypeActive && (
              <span className="rounded bg-amber-500/20 px-2 py-0.5 text-[10px] font-bold text-amber-300 uppercase">
                Pending: {extraTypeActive.replace('_', ' ')}
              </span>
            )}
          </div>

          <div className="flex items-center space-x-2">
            <button
              id="undo-last-ball-btn"
              onClick={undoLastBall}
              className="flex items-center space-x-1 rounded-lg border border-slate-700 bg-slate-800 px-2.5 py-1 text-xs font-semibold text-slate-300 hover:text-white hover:bg-slate-700 transition-colors"
            >
              <RotateCcw className="h-3.5 w-3.5" />
              <span>Undo</span>
            </button>
          </div>
        </div>

        {/* Primary Run Buttons */}
        <div className="grid grid-cols-6 gap-2">
          {[0, 1, 2, 3, 4, 6].map((num) => (
            <button
              key={num}
              id={`score-run-btn-${num}`}
              onClick={() => handleRunClick(num)}
              className={`flex h-14 sm:h-16 flex-col items-center justify-center rounded-xl font-extrabold text-lg sm:text-xl transition-all active:scale-95 shadow-md ${
                num === 4
                  ? 'bg-gradient-to-b from-sky-600 to-sky-700 hover:from-sky-500 hover:to-sky-600 text-white border border-sky-400/30'
                  : num === 6
                  ? 'bg-gradient-to-b from-purple-600 to-purple-700 hover:from-purple-500 hover:to-purple-600 text-white border border-purple-400/30'
                  : num === 0
                  ? 'bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700'
                  : 'bg-gradient-to-b from-emerald-600 to-emerald-700 hover:from-emerald-500 hover:to-emerald-600 text-white border border-emerald-500/30'
              }`}
            >
              <span>{num}</span>
              <span className="text-[9px] uppercase font-normal opacity-80">
                {num === 0 ? 'Dot' : num === 4 ? 'Boundary' : num === 6 ? 'Six' : 'Runs'}
              </span>
            </button>
          ))}
        </div>

        {/* Extras & Wicket Bar */}
        <div className="grid grid-cols-5 gap-2 pt-1">
          <button
            id="extra-btn-wide"
            onClick={() => handleExtraClick('wide')}
            className={`py-3 rounded-xl text-xs font-bold uppercase transition-all ${
              extraTypeActive === 'wide'
                ? 'bg-amber-500 text-slate-950 ring-2 ring-amber-300'
                : 'bg-slate-800 text-amber-400 hover:bg-slate-700 border border-amber-500/20'
            }`}
          >
            Wide (Wd)
          </button>

          <button
            id="extra-btn-no-ball"
            onClick={() => handleExtraClick('no_ball')}
            className={`py-3 rounded-xl text-xs font-bold uppercase transition-all ${
              extraTypeActive === 'no_ball'
                ? 'bg-amber-500 text-slate-950 ring-2 ring-amber-300'
                : 'bg-slate-800 text-amber-400 hover:bg-slate-700 border border-amber-500/20'
            }`}
          >
            No Ball (Nb)
          </button>

          <button
            id="extra-btn-bye"
            onClick={() => handleExtraClick('bye')}
            className={`py-3 rounded-xl text-xs font-bold uppercase transition-all ${
              extraTypeActive === 'bye'
                ? 'bg-amber-500 text-slate-950 ring-2 ring-amber-300'
                : 'bg-slate-800 text-slate-300 hover:bg-slate-700 border border-slate-700'
            }`}
          >
            Bye (B)
          </button>

          <button
            id="extra-btn-leg-bye"
            onClick={() => handleExtraClick('leg_bye')}
            className={`py-3 rounded-xl text-xs font-bold uppercase transition-all ${
              extraTypeActive === 'leg_bye'
                ? 'bg-amber-500 text-slate-950 ring-2 ring-amber-300'
                : 'bg-slate-800 text-slate-300 hover:bg-slate-700 border border-slate-700'
            }`}
          >
            Leg Bye (Lb)
          </button>

          <button
            id="wicket-trigger-btn"
            onClick={() => setWicketModalOpen(true)}
            className="py-3 rounded-xl bg-gradient-to-r from-red-600 to-rose-600 hover:from-red-500 hover:to-rose-500 text-white text-xs font-extrabold uppercase shadow-lg shadow-red-600/30 transition-all active:scale-95"
          >
            Wicket!
          </button>
        </div>
      </div>

      {/* Quick Scorecard Preview Link */}
      <div className="flex justify-end">
        <button
          id="view-full-scorecard-quick-link"
          onClick={() => setActiveScreen('scorecard')}
          className="flex items-center space-x-1.5 text-xs font-bold text-emerald-400 hover:text-emerald-300 py-1"
        >
          <span>View Full Detailed Scorecard</span>
          <ChevronRight className="h-4 w-4" />
        </button>
      </div>
    </div>
  );
};
