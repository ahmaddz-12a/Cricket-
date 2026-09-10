export type MatchFormat = 'T20' | 'ODI' | 'Test' | 'Custom';
export type MatchStatus = 'live' | 'innings_break' | 'completed' | 'upcoming';
export type TossDecision = 'bat' | 'bowl';
export type DismissalType = 'bowled' | 'caught' | 'lbw' | 'run_out' | 'stumped' | 'hit_wicket' | 'retired';
export type ExtraType = 'wide' | 'no_ball' | 'bye' | 'leg_bye' | 'penalty';

export interface Player {
  id: string;
  name: string;
  role: 'batsman' | 'bowler' | 'allrounder' | 'wicketkeeper';
  isCaptain?: boolean;
  isWicketKeeper?: boolean;
}

export interface Team {
  id: string;
  name: string;
  shortName: string;
  badgeColor: string;
  players: Player[];
}

export interface BatsmanStats {
  playerId: string;
  name: string;
  runs: number;
  balls: number;
  fours: number;
  sixes: number;
  isOut: boolean;
  dismissal?: {
    type: DismissalType;
    bowlerName?: string;
    fielderName?: string;
    description: string;
  };
}

export interface BowlerStats {
  playerId: string;
  name: string;
  oversBowled: number; // e.g. 3.2 is stored as total legal balls or fractional
  legalBalls: number;
  maidens: number;
  runsConceded: number;
  wickets: number;
  wides: number;
  noBalls: number;
}

export interface FallOfWicket {
  wicketNumber: number;
  score: number;
  overs: string;
  batsmanName: string;
}

export interface ExtrasBreakdown {
  wides: number;
  noBalls: number;
  byes: number;
  legByes: number;
  penalty: number;
  total: number;
}

export interface InningsState {
  teamId: string;
  teamName: string;
  runs: number;
  wickets: number;
  legalBalls: number;
  batting: BatsmanStats[];
  bowling: BowlerStats[];
  fallOfWickets: FallOfWicket[];
  extras: ExtrasBreakdown;
  recentBalls: BallRecord[];
}

export interface BallRecord {
  id: string;
  ballNumberInOver: number;
  overNumber: number;
  runsScored: number;
  isLegal: boolean;
  isExtra: boolean;
  extraType?: ExtraType;
  extraRuns: number;
  isWicket: boolean;
  dismissalType?: DismissalType;
  outBatsmanId?: string;
  strikerId: string;
  nonStrikerId: string;
  bowlerId: string;
  strikerName: string;
  bowlerName: string;
  commentary: string;
  label: string; // e.g. "4", "W", "1", "Wd+1", "Nb+6"
}

export interface Match {
  id: string;
  title: string;
  venue: string;
  format: MatchFormat;
  totalOvers: number;
  team1: Team;
  team2: Team;
  tossWinnerId: string;
  tossDecision: TossDecision;
  status: MatchStatus;
  currentInningsIndex: number; // 0 or 1
  innings: [InningsState, InningsState];
  currentStrikerId: string;
  currentNonStrikerId: string;
  currentBowlerId: string;
  target?: number;
  resultMessage?: string;
  createdAt: string;
}
