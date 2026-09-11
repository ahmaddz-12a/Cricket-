import React, { useState } from 'react';
import { useCricket } from '../context/CricketContext';
import {
  Flame,
  Radio,
  FileSpreadsheet,
  PlusCircle,
  History,
  Trophy,
  Users,
  Sun,
  Moon,
  Volume2,
  VolumeX,
  Download,
  Smartphone,
  CheckCircle,
  X,
} from 'lucide-react';

export const HeaderNav: React.FC = () => {
  const {
    activeScreen,
    setActiveScreen,
    currentMatch,
    theme,
    setTheme,
    soundEnabled,
    setSoundEnabled,
  } = useCricket();

  const [showDownloadModal, setShowDownloadModal] = useState(false);

  const navItems = [
    { id: 'live', label: 'Live Scoring', icon: Radio },
    { id: 'scorecard', label: 'Scorecard', icon: FileSpreadsheet },
    { id: 'create', label: 'New Match', icon: PlusCircle },
    { id: 'matches', label: 'Matches', icon: History },
    { id: 'stats', label: 'Stats', icon: Trophy },
    { id: 'teams', label: 'Teams', icon: Users },
  ] as const;

  return (
    <>
      {/* Top Application Bar */}
      <header id="main-header" className="sticky top-0 z-40 w-full border-b border-slate-800/80 bg-slate-950/90 backdrop-blur-md transition-colors dark:border-slate-800/80 dark:bg-slate-950/90">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3 sm:px-6">
          <div
            id="brand-logo"
            onClick={() => setActiveScreen('live')}
            className="flex cursor-pointer items-center space-x-3 group"
          >
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-tr from-emerald-600 to-teal-400 text-white shadow-lg shadow-emerald-500/20 transition-transform group-hover:scale-105">
              <Flame className="h-6 w-6 fill-current text-white" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="text-xl font-extrabold tracking-tight text-white">
                  FX <span className="text-emerald-400">Cricket</span>
                </span>
                <span className="rounded-md bg-emerald-500/10 px-1.5 py-0.5 text-[10px] font-bold text-emerald-400 border border-emerald-500/20">
                  PRO
                </span>
              </div>
              <p className="text-xs text-slate-400 font-medium">Real-Time Scoring Suite</p>
            </div>
          </div>

          {/* Center Navigation on Desktop */}
          <nav id="desktop-navigation" className="hidden md:flex items-center space-x-1 bg-slate-900/90 p-1 rounded-xl border border-slate-800">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = activeScreen === item.id;
              return (
                <button
                  key={item.id}
                  id={`nav-btn-${item.id}`}
                  onClick={() => setActiveScreen(item.id)}
                  className={`flex items-center space-x-2 rounded-lg px-3.5 py-1.5 text-xs font-semibold transition-all ${
                    isActive
                      ? 'bg-emerald-500 text-white shadow-md shadow-emerald-500/25'
                      : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                  }`}
                >
                  <Icon className="h-4 w-4" />
                  <span>{item.label}</span>
                </button>
              );
            })}
          </nav>

          {/* Right Action Utilities */}
          <div className="flex items-center space-x-2">
            <button
              id="download-app-btn"
              onClick={() => setShowDownloadModal(true)}
              title="Download App or Source Code"
              className="flex items-center space-x-1 px-2.5 py-1.5 rounded-lg border border-emerald-500/40 bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 text-xs font-bold transition-colors"
            >
              <Download className="h-4 w-4" />
              <span className="hidden sm:inline">Download</span>
            </button>

            <button
              id="sound-toggle-btn"
              onClick={() => setSoundEnabled(!soundEnabled)}
              title={soundEnabled ? 'Mute Sounds' : 'Enable Sounds'}
              className="flex h-9 w-9 items-center justify-center rounded-lg border border-slate-800 bg-slate-900 text-slate-300 hover:text-white hover:bg-slate-800 transition-colors"
            >
              {soundEnabled ? <Volume2 className="h-4 w-4 text-emerald-400" /> : <VolumeX className="h-4 w-4 text-slate-500" />}
            </button>

            <button
              id="theme-toggle-btn"
              onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
              title={`Switch to ${theme === 'dark' ? 'Light' : 'Dark'} Mode`}
              className="flex h-9 w-9 items-center justify-center rounded-lg border border-slate-800 bg-slate-900 text-slate-300 hover:text-white hover:bg-slate-800 transition-colors"
            >
              {theme === 'dark' ? <Sun className="h-4 w-4 text-amber-400" /> : <Moon className="h-4 w-4 text-sky-400" />}
            </button>

            {currentMatch && (
              <button
                id="quick-format-badge"
                onClick={() => setActiveScreen('scorecard')}
                className="hidden sm:flex items-center space-x-1.5 rounded-lg border border-emerald-500/30 bg-emerald-950/40 px-3 py-1.5 text-xs font-semibold text-emerald-400 hover:bg-emerald-900/30 transition-colors"
              >
                <span className="h-2 w-2 rounded-full bg-emerald-400 animate-pulse"></span>
                <span>{currentMatch.format} Live</span>
              </button>
            )}
          </div>
        </div>
      </header>

      {/* Mobile Bottom Navigation Bar */}
      <nav id="mobile-bottom-nav" className="fixed bottom-0 left-0 right-0 z-40 flex md:hidden items-center justify-around border-t border-slate-800/90 bg-slate-950/95 py-2 px-1 backdrop-blur-lg">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeScreen === item.id;
          return (
            <button
              key={item.id}
              id={`mobile-nav-${item.id}`}
              onClick={() => setActiveScreen(item.id)}
              className={`flex flex-col items-center justify-center py-1 px-2 rounded-lg text-[11px] font-medium transition-colors ${
                isActive ? 'text-emerald-400 font-bold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <div className={`p-1 rounded-md ${isActive ? 'bg-emerald-500/20' : ''}`}>
                <Icon className="h-5 w-5" />
              </div>
              <span className="mt-0.5">{item.label}</span>
            </button>
          );
        })}
      </nav>
      {/* Download / Install Modal */}
      {showDownloadModal && (
        <div
          id="download-modal-backdrop"
          className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/80 p-4 backdrop-blur-sm animate-in fade-in duration-200"
        >
          <div
            id="download-modal-content"
            className="w-full max-w-md rounded-2xl border border-slate-800 bg-slate-900 p-6 shadow-2xl space-y-5"
          >
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <div className="flex items-center space-x-2.5">
                <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-emerald-500/20 text-emerald-400">
                  <Download className="h-5 w-5" />
                </div>
                <div>
                  <h3 className="text-base font-bold text-white">Download & Install</h3>
                  <p className="text-xs text-slate-400">Get offline code or install on phone</p>
                </div>
              </div>
              <button
                id="close-download-modal-btn"
                onClick={() => setShowDownloadModal(false)}
                className="rounded-lg p-1 text-slate-400 hover:bg-slate-800 hover:text-white"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            {/* Option 1: Direct ZIP download */}
            <div className="rounded-xl border border-emerald-500/30 bg-emerald-950/20 p-4 space-y-2">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <Download className="h-4 w-4 text-emerald-400" />
                  <span className="text-sm font-bold text-white">Source Code (.ZIP)</span>
                </div>
                <span className="text-[11px] font-semibold text-emerald-400 bg-emerald-500/20 px-2 py-0.5 rounded-full">
                  168 KB
                </span>
              </div>
              <p className="text-xs text-slate-300">
                Complete React + TypeScript + Tailwind project ready to run locally with `npm run dev`.
              </p>
              <a
                id="direct-download-zip-link"
                href="/fx-cricket-score.zip"
                download="fx-cricket-score.zip"
                className="mt-2 flex items-center justify-center space-x-2 w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs transition-colors shadow-lg shadow-emerald-500/20"
              >
                <Download className="h-4 w-4 stroke-[2.5]" />
                <span>Download fx-cricket-score.zip</span>
              </a>
            </div>

            {/* Option 2: Mobile App Install */}
            <div className="rounded-xl border border-slate-800 bg-slate-950/60 p-4 space-y-2.5">
              <div className="flex items-center space-x-2">
                <Smartphone className="h-4 w-4 text-sky-400" />
                <span className="text-sm font-bold text-white">Install on Mobile (Android / iOS)</span>
              </div>
              <ul className="text-xs text-slate-400 space-y-1.5 list-disc list-inside">
                <li>
                  <strong className="text-slate-200">Android (Chrome):</strong> Tap menu (⋮) &gt; <span className="text-emerald-400 font-semibold">"Install App"</span> or <span className="text-emerald-400 font-semibold">"Add to Home screen"</span>.
                </li>
                <li>
                  <strong className="text-slate-200">iPhone (Safari):</strong> Tap Share (<span className="text-slate-200">↑</span>) &gt; <span className="text-emerald-400 font-semibold">"Add to Home Screen"</span>.
                </li>
              </ul>
              <button
                id="copy-share-url-btn"
                onClick={() => {
                  navigator.clipboard.writeText(window.location.href);
                  alert('App link copied to clipboard!');
                }}
                className="w-full py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 font-semibold text-xs transition-colors flex items-center justify-center space-x-1.5"
              >
                <CheckCircle className="h-3.5 w-3.5 text-sky-400" />
                <span>Copy App URL</span>
              </button>
            </div>

            <button
              id="dismiss-download-modal-btn"
              onClick={() => setShowDownloadModal(false)}
              className="w-full py-2 rounded-xl text-xs font-semibold text-slate-400 hover:text-white transition-colors"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </>
  );
};
