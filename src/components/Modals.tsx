import React, { useState } from 'react';
import { useCricket } from '../context/CricketContext';
import { DismissalType } from '../types';
import { AlertCircle, X, Check, Award, ArrowRight } from 'lucide-react';

export const Modals: React.FC = () => {
  const {
    currentMatch,
    wicketModalOpen,
    setWicketModalOpen,
    bowlerSelectModalOpen,
    setBowlerSelectModalOpen,
    overSummaryModal,
    setOverSummaryModal,
    milestoneBanner,
    dismissBanner,
    scoreBall,
    setBowler,
  } = useCricket();

  // Wicket modal state
  const [selectedDismissal, setSelectedDismissal] = useState<DismissalType>('caught');
  const [whoIsOut, setWhoIsOut] = useState<'striker' | 'nonStriker'>('striker');

  if (!currentMatch) return null;

  const currentInnings = currentMatch.innings[currentMatch.currentInningsIndex];
  const bowlingTeam = currentInnings.teamId === currentMatch.team1.id ? currentMatch.team2 : currentMatch.team1;
  const striker = currentInnings.batting.find((b) => b.playerId === currentMatch.currentStrikerId);
  const nonStriker = currentInnings.batting.find((b) => b.playerId === currentMatch.currentNonStrikerId);

  const handleConfirmWicket = () => {
    const outBatsmanId = whoIsOut === 'striker' ? currentMatch.currentStrikerId : currentMatch.currentNonStrikerId;
    scoreBall(0, undefined, 0, true, selectedDismissal, outBatsmanId);
    setWicketModalOpen(false);
  };

  const dismissalOptions: { type: DismissalType; label: string; desc: string }[] = [
    { type: 'bowled', label: 'Bowled', desc: 'Stumps shattered' },
    { type: 'caught', label: 'Caught', desc: 'Caught by fielder or keeper' },
    { type: 'lbw', label: 'LBW', desc: 'Leg before wicket' },
    { type: 'run_out', label: 'Run Out', desc: 'Broken stumps while running' },
    { type: 'stumped', label: 'Stumped', desc: 'Keeper takes bails off' },
    { type: 'hit_wicket', label: 'Hit Wicket', desc: 'Batsman dislodges bails' },
  ];

  return (
    <>
      {/* Milestone / Celebration Banner */}
      {milestoneBanner && (
        <div
          id="milestone-banner"
          className="fixed top-20 left-1/2 -translate-x-1/2 z-50 flex items-center space-x-3 rounded-2xl bg-gradient-to-r from-emerald-600 via-teal-600 to-emerald-700 px-6 py-3.5 text-white shadow-2xl shadow-emerald-500/40 border border-emerald-400/40 animate-bounce"
        >
          <Award className="h-6 w-6 text-yellow-300 fill-yellow-300" />
          <span className="font-bold tracking-wide text-sm sm:text-base">{milestoneBanner}</span>
          <button
            id="milestone-dismiss-btn"
            onClick={dismissBanner}
            className="ml-2 rounded-full p-1 hover:bg-white/20 transition-colors"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
      )}

      {/* Wicket Modal */}
      {wicketModalOpen && (
        <div
          id="wicket-modal-overlay"
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4 backdrop-blur-sm"
        >
          <div
            id="wicket-modal-card"
            className="w-full max-w-md rounded-2xl border border-red-500/30 bg-slate-900 p-6 shadow-2xl text-slate-100"
          >
            <div className="flex items-center justify-between pb-4 border-b border-slate-800">
              <div className="flex items-center space-x-2 text-red-400 font-bold text-lg">
                <AlertCircle className="h-6 w-6" />
                <span>Record Wicket</span>
              </div>
              <button
                id="close-wicket-modal-btn"
                onClick={() => setWicketModalOpen(false)}
                className="text-slate-400 hover:text-white"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            {/* Who is out selection */}
            <div className="mt-4">
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-2">
                Dismissed Batsman
              </label>
              <div className="grid grid-cols-2 gap-3">
                <button
                  type="button"
                  id="select-striker-out-btn"
                  onClick={() => setWhoIsOut('striker')}
                  className={`flex flex-col p-3 rounded-xl border text-left transition-all ${
                    whoIsOut === 'striker'
                      ? 'border-red-500 bg-red-500/10 text-white'
                      : 'border-slate-800 bg-slate-800/50 text-slate-300 hover:border-slate-700'
                  }`}
                >
                  <span className="text-xs text-red-400 font-medium">Striker</span>
                  <span className="font-bold text-sm truncate">{striker?.name || 'Striker'}</span>
                  <span className="text-xs text-slate-400">{striker?.runs || 0} ({striker?.balls || 0})</span>
                </button>

                <button
                  type="button"
                  id="select-non-striker-out-btn"
                  onClick={() => setWhoIsOut('nonStriker')}
                  className={`flex flex-col p-3 rounded-xl border text-left transition-all ${
                    whoIsOut === 'nonStriker'
                      ? 'border-red-500 bg-red-500/10 text-white'
                      : 'border-slate-800 bg-slate-800/50 text-slate-300 hover:border-slate-700'
                  }`}
                >
                  <span className="text-xs text-slate-400 font-medium">Non-Striker</span>
                  <span className="font-bold text-sm truncate">{nonStriker?.name || 'Non-Striker'}</span>
                  <span className="text-xs text-slate-400">{nonStriker?.runs || 0} ({nonStriker?.balls || 0})</span>
                </button>
              </div>
            </div>

            {/* Dismissal type selection */}
            <div className="mt-4">
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-2">
                Dismissal Type
              </label>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                {dismissalOptions.map((opt) => (
                  <button
                    key={opt.type}
                    type="button"
                    id={`dismissal-btn-${opt.type}`}
                    onClick={() => setSelectedDismissal(opt.type)}
                    className={`p-2.5 rounded-xl border text-center transition-all ${
                      selectedDismissal === opt.type
                        ? 'border-red-500 bg-red-500/20 text-white font-bold'
                        : 'border-slate-800 bg-slate-800/40 text-slate-400 hover:bg-slate-800 hover:text-slate-200'
                    }`}
                  >
                    <span className="text-xs block">{opt.label}</span>
                  </button>
                ))}
              </div>
            </div>

            <div className="mt-6 flex items-center justify-end space-x-3">
              <button
                type="button"
                id="cancel-wicket-btn"
                onClick={() => setWicketModalOpen(false)}
                className="px-4 py-2 text-xs font-semibold text-slate-400 hover:text-white rounded-lg"
              >
                Cancel
              </button>
              <button
                type="button"
                id="confirm-wicket-btn"
                onClick={handleConfirmWicket}
                className="px-5 py-2.5 bg-red-600 hover:bg-red-500 text-white font-bold text-xs rounded-xl shadow-lg shadow-red-600/30 transition-all flex items-center space-x-1.5"
              >
                <Check className="h-4 w-4" />
                <span>Confirm Out</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Select Next Bowler Modal */}
      {bowlerSelectModalOpen && (
        <div
          id="bowler-modal-overlay"
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4 backdrop-blur-sm"
        >
          <div
            id="bowler-modal-card"
            className="w-full max-w-md rounded-2xl border border-emerald-500/30 bg-slate-900 p-6 shadow-2xl text-slate-100"
          >
            <div className="flex items-center justify-between pb-4 border-b border-slate-800">
              <div>
                <h3 className="text-lg font-bold text-white flex items-center space-x-2">
                  <span>Select Next Bowler</span>
                </h3>
                <p className="text-xs text-slate-400">Choose who delivers the next over</p>
              </div>
              <button
                id="close-bowler-modal-btn"
                onClick={() => setBowlerSelectModalOpen(false)}
                className="text-slate-400 hover:text-white"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            <div className="mt-4 max-h-72 overflow-y-auto space-y-2 pr-1">
              {bowlingTeam.players.map((pl) => {
                const bStats = currentInnings.bowling.find((b) => b.playerId === pl.id);
                const isCurrent = currentMatch.currentBowlerId === pl.id;

                return (
                  <div
                    key={pl.id}
                    id={`bowler-choice-${pl.id}`}
                    onClick={() => setBowler(pl.id)}
                    className={`flex items-center justify-between p-3 rounded-xl border cursor-pointer transition-all ${
                      isCurrent
                        ? 'border-emerald-500 bg-emerald-500/10 text-white'
                        : 'border-slate-800 bg-slate-800/40 text-slate-300 hover:border-slate-700 hover:bg-slate-800/70'
                    }`}
                  >
                    <div>
                      <div className="flex items-center space-x-2">
                        <span className="font-semibold text-sm">{pl.name}</span>
                        {pl.role && (
                          <span className="text-[10px] uppercase font-bold text-emerald-400/80 bg-emerald-950/60 px-1.5 py-0.5 rounded border border-emerald-500/20">
                            {pl.role}
                          </span>
                        )}
                      </div>
                      {bStats ? (
                        <p className="text-xs text-slate-400 mt-0.5">
                          {bStats.oversBowled} ov • {bStats.runsConceded} runs • {bStats.wickets} wkt
                        </p>
                      ) : (
                        <p className="text-xs text-slate-500 mt-0.5">Yet to bowl</p>
                      )}
                    </div>
                    <ArrowRight className="h-4 w-4 text-emerald-400" />
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}

      {/* Over Summary Modal */}
      {overSummaryModal && !bowlerSelectModalOpen && (
        <div
          id="over-summary-modal"
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4 backdrop-blur-sm"
        >
          <div className="w-full max-w-sm rounded-2xl border border-slate-700 bg-slate-900 p-6 text-center shadow-2xl">
            <h3 className="text-xl font-extrabold text-white">Over Finished!</h3>
            <p className="text-xs text-slate-400 mt-1">
              Score: {currentInnings.runs}/{currentInnings.wickets} (
              {Math.floor(currentInnings.legalBalls / 6)}.{currentInnings.legalBalls % 6} ov)
            </p>
            <div className="mt-4 flex justify-center space-x-1.5">
              {currentInnings.recentBalls.slice(0, 6).reverse().map((b) => (
                <span
                  key={b.id}
                  className={`inline-flex h-8 w-8 items-center justify-center rounded-full text-xs font-bold ${
                    b.isWicket
                      ? 'bg-red-600 text-white'
                      : b.runsScored === 4
                      ? 'bg-blue-600 text-white'
                      : b.runsScored === 6
                      ? 'bg-purple-600 text-white'
                      : b.isExtra
                      ? 'bg-amber-600 text-white'
                      : 'bg-slate-800 text-slate-300'
                  }`}
                >
                  {b.label}
                </span>
              ))}
            </div>
            <button
              id="continue-after-over-btn"
              onClick={() => {
                setOverSummaryModal(false);
                setBowlerSelectModalOpen(true);
              }}
              className="mt-6 w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-white font-bold text-xs shadow-lg shadow-emerald-500/25 transition-colors"
            >
              Select Next Bowler
            </button>
          </div>
        </div>
      )}
    </>
  );
};
