import React from 'react';
import { CricketProvider, useCricket } from './context/CricketContext';
import { HeaderNav } from './components/HeaderNav';
import { LiveScoringScreen } from './components/LiveScoringScreen';
import { ScorecardScreen } from './components/ScorecardScreen';
import { CreateMatchScreen } from './components/CreateMatchScreen';
import { MatchesScreen } from './components/MatchesScreen';
import { StatsScreen } from './components/StatsScreen';
import { TeamsScreen } from './components/TeamsScreen';
import { Modals } from './components/Modals';

const MainContent: React.FC = () => {
  const { activeScreen, theme } = useCricket();

  return (
    <div className={`min-h-screen ${theme === 'dark' ? 'bg-slate-950 text-slate-100' : 'bg-slate-50 text-slate-900'} transition-colors`}>
      <HeaderNav />
      <main className="px-4 py-6 sm:px-6">
        {activeScreen === 'live' && <LiveScoringScreen />}
        {activeScreen === 'scorecard' && <ScorecardScreen />}
        {activeScreen === 'create' && <CreateMatchScreen />}
        {activeScreen === 'matches' && <MatchesScreen />}
        {activeScreen === 'stats' && <StatsScreen />}
        {activeScreen === 'teams' && <TeamsScreen />}
      </main>
      <Modals />
    </div>
  );
};

export function App() {
  return (
    <CricketProvider>
      <MainContent />
    </CricketProvider>
  );
}

export default App;
