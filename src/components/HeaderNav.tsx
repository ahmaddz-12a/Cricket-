import React from 'react';
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
    </>
  );
};
