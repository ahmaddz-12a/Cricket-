import React, { useState } from 'react';
import { useCricket } from '../context/CricketContext';
import { Users, Shield, Crown, UserCheck } from 'lucide-react';

export const TeamsScreen: React.FC = () => {
  const { teams } = useCricket();
  const [selectedTeamId, setSelectedTeamId] = useState(teams[0]?.id || '');

  const currentTeam = teams.find((t) => t.id === selectedTeamId) || teams[0];

  return (
    <div id="teams-screen-container" className="mx-auto max-w-4xl space-y-6 pb-20">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 shadow-lg">
        <h2 className="text-lg font-bold text-white flex items-center space-x-2">
          <Users className="h-5 w-5 text-emerald-400" />
          <span>Teams & Squad Rosters</span>
        </h2>
        <p className="text-xs text-slate-400">View team player lists, roles, and assigned captains</p>
      </div>

      {/* Team Tabs */}
      <div className="flex space-x-2 overflow-x-auto pb-2">
        {teams.map((t) => (
          <button
            key={t.id}
            id={`team-tab-${t.id}`}
            onClick={() => setSelectedTeamId(t.id)}
            className={`flex items-center space-x-2 px-4 py-2.5 rounded-xl border text-xs font-bold transition-all whitespace-nowrap ${
              selectedTeamId === t.id
                ? 'border-emerald-500 bg-emerald-500/20 text-white shadow-md'
                : 'border-slate-800 bg-slate-900 text-slate-400 hover:border-slate-700'
            }`}
          >
            <span className="h-2.5 w-2.5 rounded-full" style={{ backgroundColor: t.badgeColor }}></span>
            <span>{t.name}</span>
          </button>
        ))}
      </div>

      {/* Selected Team Squad List */}
      {currentTeam && (
        <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6 shadow-xl space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <div className="flex items-center space-x-3">
              <div
                className="flex h-10 w-10 items-center justify-center rounded-xl font-black text-white text-sm"
                style={{ backgroundColor: currentTeam.badgeColor }}
              >
                {currentTeam.shortName}
              </div>
              <div>
                <h3 className="text-base font-bold text-white">{currentTeam.name}</h3>
                <p className="text-xs text-slate-400">{currentTeam.players.length} Squad Members</p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3 pt-2">
            {currentTeam.players.map((p, idx) => (
              <div
                key={p.id}
                className="flex items-center justify-between p-3 rounded-xl bg-slate-950/70 border border-slate-800/80 hover:border-slate-700 transition-colors"
              >
                <div className="flex items-center space-x-2.5 min-w-0">
                  <span className="text-xs font-bold text-slate-500 w-4">{idx + 1}</span>
                  <div className="truncate">
                    <span className="font-semibold text-xs text-white block truncate">{p.name}</span>
                    <span className="text-[10px] uppercase font-bold text-slate-400">{p.role}</span>
                  </div>
                </div>

                <div className="flex items-center space-x-1">
                  {p.isCaptain && (
                    <span
                      title="Captain"
                      className="flex h-5 w-5 items-center justify-center rounded-full bg-amber-500/20 text-amber-400 text-[10px] font-bold border border-amber-500/30"
                    >
                      C
                    </span>
                  )}
                  {p.isWicketKeeper && (
                    <span
                      title="Wicket Keeper"
                      className="flex h-5 w-5 items-center justify-center rounded-full bg-sky-500/20 text-sky-400 text-[10px] font-bold border border-sky-500/30"
                    >
                      WK
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
