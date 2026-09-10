import React from 'react';
import { useCricket } from '../context/CricketContext';
import { Trophy, Award, Target, Flame } from 'lucide-react';

export const StatsScreen: React.FC = () => {
  const { matches } = useCricket();

  // Aggregate stats across all matches
  const batsmanStatsMap: { [id: string]: { name: string; runs: number; balls: number; fours: number; sixes: number; highest: number } } = {};
  const bowlerStatsMap: { [id: string]: { name: string; wickets: number; runs: number; balls: number } } = {};

  matches.forEach((m) => {
    m.innings.forEach((inn) => {
      inn.batting.forEach((b) => {
        if (!batsmanStatsMap[b.playerId]) {
          batsmanStatsMap[b.playerId] = { name: b.name, runs: 0, balls: 0, fours: 0, sixes: 0, highest: 0 };
        }
        batsmanStatsMap[b.playerId].runs += b.runs;
        batsmanStatsMap[b.playerId].balls += b.balls;
        batsmanStatsMap[b.playerId].fours += b.fours;
        batsmanStatsMap[b.playerId].sixes += b.sixes;
        if (b.runs > batsmanStatsMap[b.playerId].highest) {
          batsmanStatsMap[b.playerId].highest = b.runs;
        }
      });

      inn.bowling.forEach((bo) => {
        if (!bowlerStatsMap[bo.playerId]) {
          bowlerStatsMap[bo.playerId] = { name: bo.name, wickets: 0, runs: 0, balls: 0 };
        }
        bowlerStatsMap[bo.playerId].wickets += bo.wickets;
        bowlerStatsMap[bo.playerId].runs += bo.runsConceded;
        bowlerStatsMap[bo.playerId].balls += bo.legalBalls;
      });
    });
  });

  const topScorers = Object.values(batsmanStatsMap).sort((a, b) => b.runs - a.runs).slice(0, 5);
  const topWicketTakers = Object.values(bowlerStatsMap).sort((a, b) => b.wickets - a.wickets).slice(0, 5);

  return (
    <div id="stats-screen-container" className="mx-auto max-w-4xl space-y-6 pb-20">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 shadow-lg">
        <h2 className="text-lg font-bold text-white flex items-center space-x-2">
          <Trophy className="h-5 w-5 text-yellow-400" />
          <span>Tournament Statistics & Leaderboards</span>
        </h2>
        <p className="text-xs text-slate-400">Real-time performance analytics calculated across recorded matches</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Leading Run Scorers */}
        <div className="rounded-2xl border border-slate-800 bg-slate-900 p-5 shadow-xl space-y-4">
          <div className="flex items-center space-x-2 border-b border-slate-800 pb-3">
            <Flame className="h-5 w-5 text-amber-400" />
            <h3 className="text-sm font-bold uppercase tracking-wider text-white">Top Run Scorers (Orange Cap)</h3>
          </div>

          <div className="space-y-2">
            {topScorers.map((b, idx) => {
              const sr = b.balls > 0 ? ((b.runs / b.balls) * 100).toFixed(1) : '0.0';
              return (
                <div
                  key={b.name}
                  className="flex items-center justify-between p-3 rounded-xl bg-slate-950/70 border border-slate-800/80"
                >
                  <div className="flex items-center space-x-3">
                    <span className="flex h-6 w-6 items-center justify-center rounded-full bg-slate-800 text-xs font-bold text-slate-300">
                      {idx + 1}
                    </span>
                    <div>
                      <span className="font-bold text-sm text-white block">{b.name}</span>
                      <span className="text-[11px] text-slate-400">
                        SR: {sr} • 4s: {b.fours} • 6s: {b.sixes}
                      </span>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="text-lg font-black text-amber-400">{b.runs}</span>
                    <span className="text-xs text-slate-400 block">HS: {b.highest}</span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Leading Wicket Takers */}
        <div className="rounded-2xl border border-slate-800 bg-slate-900 p-5 shadow-xl space-y-4">
          <div className="flex items-center space-x-2 border-b border-slate-800 pb-3">
            <Target className="h-5 w-5 text-purple-400" />
            <h3 className="text-sm font-bold uppercase tracking-wider text-white">Top Wicket Takers (Purple Cap)</h3>
          </div>

          <div className="space-y-2">
            {topWicketTakers.map((bo, idx) => {
              const econ = bo.balls > 0 ? ((bo.runs / bo.balls) * 6).toFixed(2) : '0.00';
              const overs = `${Math.floor(bo.balls / 6)}.${bo.balls % 6}`;
              return (
                <div
                  key={bo.name}
                  className="flex items-center justify-between p-3 rounded-xl bg-slate-950/70 border border-slate-800/80"
                >
                  <div className="flex items-center space-x-3">
                    <span className="flex h-6 w-6 items-center justify-center rounded-full bg-slate-800 text-xs font-bold text-slate-300">
                      {idx + 1}
                    </span>
                    <div>
                      <span className="font-bold text-sm text-white block">{bo.name}</span>
                      <span className="text-[11px] text-slate-400">
                        {overs} ov • Econ: {econ}
                      </span>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="text-lg font-black text-purple-400">{bo.wickets}</span>
                    <span className="text-xs text-slate-400 block">{bo.runs} runs</span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};
