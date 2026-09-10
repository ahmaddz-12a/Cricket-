import React, { useState } from 'react';
import { useCricket } from '../context/CricketContext';
import { MatchFormat, TossDecision } from '../types';
import { PlusCircle, Shield, Trophy } from 'lucide-react';

export const CreateMatchScreen: React.FC = () => {
  const { teams, createNewMatch } = useCricket();

  const [title, setTitle] = useState('');
  const [venue, setVenue] = useState('Lord\'s Cricket Ground');
  const [format, setFormat] = useState<MatchFormat>('T20');
  const [overs, setOvers] = useState<number>(20);
  const [team1Id, setTeam1Id] = useState(teams[0]?.id || '');
  const [team2Id, setTeam2Id] = useState(teams[1]?.id || '');
  const [tossWinnerId, setTossWinnerId] = useState(teams[0]?.id || '');
  const [tossDecision, setTossDecision] = useState<TossDecision>('bat');

  const formatOptions: { id: MatchFormat; label: string; defaultOvers: number }[] = [
    { id: 'T20', label: 'T20 (20 Overs)', defaultOvers: 20 },
    { id: 'ODI', label: 'ODI (50 Overs)', defaultOvers: 50 },
    { id: 'Custom', label: 'Custom (10 Overs)', defaultOvers: 10 },
    { id: 'Test', label: 'Super Over (1 Over)', defaultOvers: 1 },
  ];

  const handleFormatChange = (fmt: MatchFormat, defaultOvers: number) => {
    setFormat(fmt);
    setOvers(defaultOvers);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (team1Id === team2Id) {
      alert('Please select two different teams!');
      return;
    }
    createNewMatch(
      title,
      venue,
      format,
      overs,
      team1Id,
      team2Id,
      tossWinnerId || team1Id,
      tossDecision
    );
  };

  return (
    <div id="create-match-container" className="mx-auto max-w-2xl space-y-6 pb-20">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-6">
        <div className="border-b border-slate-800 pb-4">
          <h2 className="text-xl font-bold text-white flex items-center space-x-2">
            <PlusCircle className="h-6 w-6 text-emerald-400" />
            <span>Setup New Cricket Match</span>
          </h2>
          <p className="text-xs text-slate-400 mt-1">
            Configure squads, toss result, and overs to initialize live scoring
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          {/* Match Title & Venue */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5">
                Match Title (Optional)
              </label>
              <input
                type="text"
                id="match-title-input"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="e.g. World Cup Final 2026"
                className="w-full rounded-xl border border-slate-700 bg-slate-950 px-3.5 py-2.5 text-xs text-white placeholder-slate-500 focus:border-emerald-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5">
                Venue
              </label>
              <input
                type="text"
                id="match-venue-input"
                value={venue}
                onChange={(e) => setVenue(e.target.value)}
                placeholder="Stadium Name"
                className="w-full rounded-xl border border-slate-700 bg-slate-950 px-3.5 py-2.5 text-xs text-white placeholder-slate-500 focus:border-emerald-500 focus:outline-none"
              />
            </div>
          </div>

          {/* Format Picker */}
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-2">
              Match Format
            </label>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
              {formatOptions.map((opt) => (
                <button
                  key={opt.id}
                  type="button"
                  id={`format-btn-${opt.id}`}
                  onClick={() => handleFormatChange(opt.id, opt.defaultOvers)}
                  className={`p-3 rounded-xl border text-center transition-all ${
                    format === opt.id
                      ? 'border-emerald-500 bg-emerald-500/20 text-white font-bold'
                      : 'border-slate-800 bg-slate-950/60 text-slate-400 hover:border-slate-700'
                  }`}
                >
                  <span className="block text-xs font-bold">{opt.id}</span>
                  <span className="text-[10px] text-slate-400">{opt.defaultOvers} ov</span>
                </button>
              ))}
            </div>
          </div>

          {/* Overs Input */}
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5">
              Overs Per Side
            </label>
            <input
              type="number"
              id="overs-count-input"
              min={1}
              max={100}
              value={overs}
              onChange={(e) => setOvers(Number(e.target.value))}
              className="w-full rounded-xl border border-slate-700 bg-slate-950 px-3.5 py-2.5 text-xs text-white focus:border-emerald-500 focus:outline-none"
            />
          </div>

          {/* Teams Selection */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 border-t border-slate-800 pt-4">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5 flex items-center space-x-1.5">
                <Shield className="h-3.5 w-3.5 text-emerald-400" />
                <span>Team 1 (Home)</span>
              </label>
              <select
                id="team1-select"
                value={team1Id}
                onChange={(e) => setTeam1Id(e.target.value)}
                className="w-full rounded-xl border border-slate-700 bg-slate-950 px-3.5 py-2.5 text-xs text-white focus:border-emerald-500 focus:outline-none"
              >
                {teams.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.name} ({t.shortName})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5 flex items-center space-x-1.5">
                <Shield className="h-3.5 w-3.5 text-sky-400" />
                <span>Team 2 (Away)</span>
              </label>
              <select
                id="team2-select"
                value={team2Id}
                onChange={(e) => setTeam2Id(e.target.value)}
                className="w-full rounded-xl border border-slate-700 bg-slate-950 px-3.5 py-2.5 text-xs text-white focus:border-emerald-500 focus:outline-none"
              >
                {teams.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.name} ({t.shortName})
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Toss Details */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 border-t border-slate-800 pt-4">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5 flex items-center space-x-1.5">
                <Trophy className="h-3.5 w-3.5 text-amber-400" />
                <span>Toss Won By</span>
              </label>
              <select
                id="toss-winner-select"
                value={tossWinnerId}
                onChange={(e) => setTossWinnerId(e.target.value)}
                className="w-full rounded-xl border border-slate-700 bg-slate-950 px-3.5 py-2.5 text-xs text-white focus:border-emerald-500 focus:outline-none"
              >
                <option value={team1Id}>{teams.find((t) => t.id === team1Id)?.name || 'Team 1'}</option>
                <option value={team2Id}>{teams.find((t) => t.id === team2Id)?.name || 'Team 2'}</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1.5">
                Elected To
              </label>
              <div className="grid grid-cols-2 gap-2">
                <button
                  type="button"
                  id="toss-bat-btn"
                  onClick={() => setTossDecision('bat')}
                  className={`py-2 rounded-xl text-xs font-bold border transition-all ${
                    tossDecision === 'bat'
                      ? 'border-emerald-500 bg-emerald-500/20 text-white'
                      : 'border-slate-800 bg-slate-950/60 text-slate-400'
                  }`}
                >
                  Bat First
                </button>
                <button
                  type="button"
                  id="toss-bowl-btn"
                  onClick={() => setTossDecision('bowl')}
                  className={`py-2 rounded-xl text-xs font-bold border transition-all ${
                    tossDecision === 'bowl'
                      ? 'border-emerald-500 bg-emerald-500/20 text-white'
                      : 'border-slate-800 bg-slate-950/60 text-slate-400'
                  }`}
                >
                  Bowl First
                </button>
              </div>
            </div>
          </div>

          <button
            type="submit"
            id="start-match-btn"
            className="w-full py-3.5 rounded-xl bg-gradient-to-r from-emerald-600 to-teal-500 hover:from-emerald-500 hover:to-teal-400 text-white font-extrabold text-sm shadow-xl shadow-emerald-500/30 transition-all active:scale-98"
          >
            Start Match & Begin Scoring 🏏
          </button>
        </form>
      </div>
    </div>
  );
};
