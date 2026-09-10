import React, { useState } from 'react';
import { useCricket } from '../context/CricketContext';
import { FileSpreadsheet, ArrowLeft } from 'lucide-react';

export const ScorecardScreen: React.FC = () => {
  const { currentMatch, setActiveScreen } = useCricket();
  const [selectedInnings, setSelectedInnings] = useState<number>(0);

  if (!currentMatch) {
    return (
      <div className="flex h-96 items-center justify-center text-slate-400">
        No active match selected.
      </div>
    );
  }

  const innings = currentMatch.innings[selectedInnings];
  const battingTeam = innings?.teamId === currentMatch.team1.id ? currentMatch.team1 : currentMatch.team2;
  const bowlingTeam = innings?.teamId === currentMatch.team1.id ? currentMatch.team2 : currentMatch.team1;

  if (!innings) {
    return <div className="text-center p-8 text-slate-400">Innings not yet started.</div>;
  }

  const oversFormatted = `${Math.floor(innings.legalBalls / 6)}.${innings.legalBalls % 6}`;
  const crr = innings.legalBalls > 0 ? ((innings.runs / innings.legalBalls) * 6).toFixed(2) : '0.00';

  const strikeRate = (r: number, b: number) => (b > 0 ? ((r / b) * 100).toFixed(1) : '0.0');
  const bowlerEconomy = (r: number, lb: number) => (lb > 0 ? ((r / lb) * 6).toFixed(2) : '0.00');

  // Did not bat players
  const battedPlayerIds = new Set(innings.batting.map((b) => b.playerId));
  const didNotBat = battingTeam.players.filter((p) => !battedPlayerIds.has(p.id));

  return (
    <div id="scorecard-screen-container" className="mx-auto max-w-4xl space-y-4 pb-20">
      {/* Top Header & Innings Switcher */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-slate-900/90 border border-slate-800 rounded-2xl p-4 shadow-lg">
        <div className="flex items-center space-x-3">
          <button
            id="back-to-live-from-scorecard"
            onClick={() => setActiveScreen('live')}
            className="flex h-9 w-9 items-center justify-center rounded-xl bg-slate-800 text-slate-300 hover:text-white hover:bg-slate-700 transition-colors"
          >
            <ArrowLeft className="h-4 w-4" />
          </button>
          <div>
            <h2 className="text-lg font-bold text-white flex items-center space-x-2">
              <FileSpreadsheet className="h-5 w-5 text-emerald-400" />
              <span>Full Match Scorecard</span>
            </h2>
            <p className="text-xs text-slate-400">
              {currentMatch.team1.name} vs {currentMatch.team2.name} • {currentMatch.format}
            </p>
          </div>
        </div>

        {/* Innings Tabs */}
        <div className="flex bg-slate-950 p-1 rounded-xl border border-slate-800 self-start sm:self-auto">
          <button
            id="scorecard-tab-innings-1"
            onClick={() => setSelectedInnings(0)}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all ${
              selectedInnings === 0
                ? 'bg-emerald-500 text-white shadow'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            1st Innings ({currentMatch.innings[0].teamName})
          </button>
          <button
            id="scorecard-tab-innings-2"
            onClick={() => setSelectedInnings(1)}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all ${
              selectedInnings === 1
                ? 'bg-emerald-500 text-white shadow'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            2nd Innings ({currentMatch.innings[1].teamName})
          </button>
        </div>
      </div>

      {/* Innings Summary Banner */}
      <div className="flex items-center justify-between bg-gradient-to-r from-emerald-950/60 to-slate-900 border border-emerald-500/20 rounded-2xl p-4 shadow-md">
        <div>
          <span className="text-xs uppercase font-extrabold text-emerald-400 tracking-wider block">
            {innings.teamName}
          </span>
          <div className="flex items-baseline space-x-2 mt-0.5">
            <span className="text-3xl font-black text-white">
              {innings.runs}/{innings.wickets}
            </span>
            <span className="text-xs text-slate-400">({oversFormatted} Overs, CRR: {crr})</span>
          </div>
        </div>
        {currentMatch.resultMessage && (
          <div className="text-right text-xs font-bold text-slate-300 max-w-xs">
            {currentMatch.resultMessage}
          </div>
        )}
      </div>

      {/* Batting Scorecard Table */}
      <div className="overflow-hidden rounded-2xl border border-slate-800 bg-slate-900 shadow-xl">
        <div className="border-b border-slate-800 bg-slate-950/80 px-4 py-3">
          <h3 className="text-xs font-bold uppercase tracking-wider text-emerald-400">Batting</h3>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="border-b border-slate-800 bg-slate-950/40 text-[11px] uppercase font-bold text-slate-400">
              <tr>
                <th className="px-4 py-2.5">Batter</th>
                <th className="px-4 py-2.5">Dismissal</th>
                <th className="px-3 py-2.5 text-right">R</th>
                <th className="px-3 py-2.5 text-right">B</th>
                <th className="px-3 py-2.5 text-right">4s</th>
                <th className="px-3 py-2.5 text-right">6s</th>
                <th className="px-4 py-2.5 text-right">SR</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {innings.batting.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-4 py-4 text-center text-slate-500">
                    No batting data available for this innings.
                  </td>
                </tr>
              ) : (
                innings.batting.map((b) => (
                  <tr key={b.playerId} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3 font-semibold text-white">
                      <div className="flex items-center space-x-1.5">
                        <span>{b.name}</span>
                        {!b.isOut && (
                          <span className="text-[10px] font-bold text-emerald-400 bg-emerald-950/80 px-1 py-0.2 rounded border border-emerald-500/20">
                            not out
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-4 py-3 text-slate-400">
                      {b.isOut ? b.dismissal?.description || 'out' : 'not out'}
                    </td>
                    <td className="px-3 py-3 text-right font-bold text-white text-sm">{b.runs}</td>
                    <td className="px-3 py-3 text-right text-slate-400">{b.balls}</td>
                    <td className="px-3 py-3 text-right text-slate-400">{b.fours}</td>
                    <td className="px-3 py-3 text-right text-slate-400">{b.sixes}</td>
                    <td className="px-4 py-3 text-right font-medium text-slate-300">
                      {strikeRate(b.runs, b.balls)}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Extras & Total Row */}
        <div className="border-t border-slate-800 bg-slate-950/60 px-4 py-3 text-xs space-y-1">
          <div className="flex justify-between text-slate-400">
            <span>
              Extras ({innings.extras.total}) • b {innings.extras.byes}, lb {innings.extras.legByes}, wd{' '}
              {innings.extras.wides}, nb {innings.extras.noBalls}
            </span>
            <span className="font-bold text-white">{innings.extras.total}</span>
          </div>
          <div className="flex justify-between border-t border-slate-800/60 pt-2 font-bold text-sm text-white">
            <span>Total ({innings.wickets} wickets, {oversFormatted} overs)</span>
            <span className="text-emerald-400 font-extrabold text-base">{innings.runs}</span>
          </div>
        </div>

        {/* Did not bat */}
        {didNotBat.length > 0 && (
          <div className="border-t border-slate-800 bg-slate-950/30 px-4 py-2.5 text-xs text-slate-400">
            <span className="font-bold text-slate-300 mr-2">Did not bat:</span>
            <span>{didNotBat.map((p) => p.name).join(', ')}</span>
          </div>
        )}
      </div>

      {/* Bowling Scorecard Table */}
      <div className="overflow-hidden rounded-2xl border border-slate-800 bg-slate-900 shadow-xl">
        <div className="border-b border-slate-800 bg-slate-950/80 px-4 py-3">
          <h3 className="text-xs font-bold uppercase tracking-wider text-emerald-400">
            Bowling ({bowlingTeam.name})
          </h3>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="border-b border-slate-800 bg-slate-950/40 text-[11px] uppercase font-bold text-slate-400">
              <tr>
                <th className="px-4 py-2.5">Bowler</th>
                <th className="px-3 py-2.5 text-right">O</th>
                <th className="px-3 py-2.5 text-right">M</th>
                <th className="px-3 py-2.5 text-right">R</th>
                <th className="px-3 py-2.5 text-right">W</th>
                <th className="px-4 py-2.5 text-right">Econ</th>
                <th className="px-3 py-2.5 text-right">WD</th>
                <th className="px-3 py-2.5 text-right">NB</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {innings.bowling.length === 0 ? (
                <tr>
                  <td colSpan={8} className="px-4 py-4 text-center text-slate-500">
                    No bowling data recorded for this innings.
                  </td>
                </tr>
              ) : (
                innings.bowling.map((b) => (
                  <tr key={b.playerId} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3 font-semibold text-white">{b.name}</td>
                    <td className="px-3 py-3 text-right text-slate-300">{b.oversBowled}</td>
                    <td className="px-3 py-3 text-right text-slate-400">{b.maidens}</td>
                    <td className="px-3 py-3 text-right font-medium text-slate-200">{b.runsConceded}</td>
                    <td className="px-3 py-3 text-right font-extrabold text-emerald-400 text-sm">
                      {b.wickets}
                    </td>
                    <td className="px-4 py-3 text-right text-slate-300">
                      {bowlerEconomy(b.runsConceded, b.legalBalls)}
                    </td>
                    <td className="px-3 py-3 text-right text-slate-400">{b.wides}</td>
                    <td className="px-3 py-3 text-right text-slate-400">{b.noBalls}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Fall of Wickets (FOW) */}
      {innings.fallOfWickets.length > 0 && (
        <div className="overflow-hidden rounded-2xl border border-slate-800 bg-slate-900 p-4 shadow-xl">
          <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-3">
            Fall of Wickets
          </h3>
          <div className="flex flex-wrap gap-2">
            {innings.fallOfWickets.map((fow) => (
              <div
                key={fow.wicketNumber}
                className="flex items-center space-x-2 rounded-xl bg-slate-950/80 border border-slate-800 px-3 py-2 text-xs"
              >
                <span className="font-extrabold text-red-400">{fow.score}/{fow.wicketNumber}</span>
                <span className="text-slate-300 font-medium">{fow.batsmanName}</span>
                <span className="text-slate-500">({fow.overs} ov)</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
