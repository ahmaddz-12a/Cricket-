import React, { useState } from 'react';
import { useCricket } from '../context/CricketContext';
import { History, Play, FileSpreadsheet, PlusCircle } from 'lucide-react';

export const MatchesScreen: React.FC = () => {
  const { matches, selectMatch, setActiveScreen } = useCricket();
  const [filter, setFilter] = useState<'all' | 'live' | 'completed'>('all');

  const filteredMatches = matches.filter((m) => {
    if (filter === 'live') return m.status === 'live';
    if (filter === 'completed') return m.status === 'completed';
    return true;
  });

  return (
    <div id="matches-screen-container" className="mx-auto max-w-4xl space-y-4 pb-20">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-slate-900 border border-slate-800 rounded-2xl p-4 shadow-lg">
        <div>
          <h2 className="text-lg font-bold text-white flex items-center space-x-2">
            <History className="h-5 w-5 text-emerald-400" />
            <span>Matches & History</span>
          </h2>
          <p className="text-xs text-slate-400">Review completed fixtures or resume live matches</p>
        </div>

        <div className="flex items-center space-x-2">
          <div className="flex bg-slate-950 p-1 rounded-xl border border-slate-800">
            {(['all', 'live', 'completed'] as const).map((tab) => (
              <button
                key={tab}
                id={`filter-matches-${tab}`}
                onClick={() => setFilter(tab)}
                className={`px-3 py-1 text-xs font-bold capitalize rounded-lg transition-all ${
                  filter === tab ? 'bg-emerald-500 text-white' : 'text-slate-400 hover:text-white'
                }`}
              >
                {tab}
              </button>
            ))}
          </div>

          <button
            id="new-match-from-list-btn"
            onClick={() => setActiveScreen('create')}
            className="flex items-center space-x-1 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold px-3 py-2 rounded-xl transition-all shadow-md shadow-emerald-600/20"
          >
            <PlusCircle className="h-3.5 w-3.5" />
            <span className="hidden sm:inline">New Match</span>
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filteredMatches.map((m) => {
          const inn1 = m.innings[0];
          const inn2 = m.innings[1];
          const isLive = m.status === 'live';

          const overs1 = `${Math.floor(inn1.legalBalls / 6)}.${inn1.legalBalls % 6}`;
          const overs2 = `${Math.floor(inn2.legalBalls / 6)}.${inn2.legalBalls % 6}`;

          return (
            <div
              key={m.id}
              id={`match-card-${m.id}`}
              className="rounded-2xl border border-slate-800 bg-slate-900/90 p-5 shadow-lg space-y-4 hover:border-slate-700 transition-all flex flex-col justify-between"
            >
              <div>
                <div className="flex items-center justify-between border-b border-slate-800/80 pb-2.5">
                  <span className="text-xs font-bold text-slate-300 truncate max-w-[200px]">
                    {m.title}
                  </span>
                  <span
                    className={`rounded-md px-2 py-0.5 text-[10px] font-bold uppercase ${
                      isLive
                        ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                        : 'bg-slate-800 text-slate-400'
                    }`}
                  >
                    {isLive ? 'Live' : 'Completed'}
                  </span>
                </div>

                <div className="mt-3 space-y-2">
                  {/* Team 1 Score */}
                  <div className="flex items-center justify-between text-xs">
                    <span className="font-bold text-white flex items-center space-x-2">
                      <span className="h-2.5 w-2.5 rounded-full" style={{ backgroundColor: m.team1.badgeColor }}></span>
                      <span>{m.team1.name}</span>
                    </span>
                    <span className="font-extrabold text-white text-sm">
                      {inn1.runs}/{inn1.wickets}{' '}
                      <span className="text-[11px] font-normal text-slate-400">({overs1} ov)</span>
                    </span>
                  </div>

                  {/* Team 2 Score */}
                  <div className="flex items-center justify-between text-xs">
                    <span className="font-bold text-white flex items-center space-x-2">
                      <span className="h-2.5 w-2.5 rounded-full" style={{ backgroundColor: m.team2.badgeColor }}></span>
                      <span>{m.team2.name}</span>
                    </span>
                    <span className="font-extrabold text-white text-sm">
                      {inn2.runs}/{inn2.wickets}{' '}
                      <span className="text-[11px] font-normal text-slate-400">({overs2} ov)</span>
                    </span>
                  </div>
                </div>

                {m.resultMessage && (
                  <p className="mt-3 text-xs font-semibold text-emerald-400 bg-emerald-950/40 p-2 rounded-xl border border-emerald-500/20">
                    {m.resultMessage}
                  </p>
                )}

                <div className="mt-2 text-[11px] text-slate-500">
                  {m.venue} • {m.format}
                </div>
              </div>

              <div className="flex items-center space-x-2 pt-2 border-t border-slate-800/80">
                <button
                  id={`resume-match-btn-${m.id}`}
                  onClick={() => selectMatch(m.id)}
                  className="flex-1 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs shadow transition-colors flex items-center justify-center space-x-1.5"
                >
                  <Play className="h-3.5 w-3.5 fill-current" />
                  <span>{isLive ? 'Score Live' : 'Open Match'}</span>
                </button>
                <button
                  id={`view-scorecard-btn-${m.id}`}
                  onClick={() => {
                    selectMatch(m.id);
                    setActiveScreen('scorecard');
                  }}
                  className="py-2 px-3 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold text-xs border border-slate-700 transition-colors flex items-center space-x-1"
                >
                  <FileSpreadsheet className="h-3.5 w-3.5 text-emerald-400" />
                  <span>Scorecard</span>
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
