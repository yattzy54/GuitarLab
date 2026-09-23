import { MetronomeConfig, MetronomeBeat, TimeSignature, TrainerConfig } from '../types';
import { playMetronomeClick } from './guitarSynth';
import { getAudioContext } from './audioContext';

export class MetronomeEngine {
  private config: MetronomeConfig;
  private isRunning = false;
  private timerId: number | null = null;
  private nextNoteTime = 0.0;
  private lookaheadMs = 25.0; // How frequently to call scheduling function (in ms)
  private scheduleAheadTime = 0.1; // How far ahead to schedule audio (sec)
  private currentBeatInBar = 1;
  private currentBarIndex = 0;
  private currentBpm = 120;
  private barsCountInInterval = 0;
  private trainerStartTimeMs = 0;
  private onBeatCallback?: (beat: MetronomeBeat) => void;

  constructor(initialConfig: MetronomeConfig, onBeat?: (beat: MetronomeBeat) => void) {
    this.config = initialConfig;
    this.currentBpm = initialConfig.bpm;
    this.onBeatCallback = onBeat;
  }

  public updateConfig(newConfig: MetronomeConfig) {
    this.config = newConfig;
    if (!newConfig.trainer.enabled) {
      this.currentBpm = newConfig.bpm;
    }
  }

  public start() {
    if (this.isRunning) return;
    this.isRunning = true;
    this.currentBeatInBar = 1;
    this.currentBarIndex = 0;
    this.barsCountInInterval = 0;
    this.trainerStartTimeMs = Date.now();

    if (this.config.trainer.enabled) {
      this.currentBpm = this.config.trainer.startBpm;
    } else {
      this.currentBpm = this.config.bpm;
    }

    const ctx = getAudioContext();
    this.nextNoteTime = ctx.currentTime + 0.05;
    this.runScheduler();
  }

  public stop() {
    this.isRunning = false;
    if (this.timerId !== null) {
      window.clearTimeout(this.timerId);
      this.timerId = null;
    }
  }

  public getIsRunning(): boolean {
    return this.isRunning;
  }

  public getCurrentBpm(): number {
    return this.currentBpm;
  }

  private runScheduler = () => {
    if (!this.isRunning) return;

    const ctx = getAudioContext();
    while (this.nextNoteTime < ctx.currentTime + this.scheduleAheadTime) {
      this.scheduleNote(this.nextNoteTime);
      this.advanceNote();
    }

    this.timerId = window.setTimeout(this.runScheduler, this.lookaheadMs);
  };

  private scheduleNote(time: number) {
    const isAccent = this.config.timeSignature.accentBeats.includes(this.currentBeatInBar);
    
    // Play click at time
    playMetronomeClick(isAccent, this.config.volume);

    // Calculate trainer progress
    let progressToNextJump = 0;
    let beatsUntilJump: number | undefined;

    if (this.config.trainer.enabled) {
      if (this.config.trainer.intervalKind === 'BARS') {
        const intervalBars = Math.max(1, this.config.trainer.intervalValue);
        const barInCycle = this.barsCountInInterval % intervalBars;
        progressToNextJump = (barInCycle * this.config.timeSignature.beatsPerBar + (this.currentBeatInBar - 1)) / (intervalBars * this.config.timeSignature.beatsPerBar);
        beatsUntilJump = (intervalBars - barInCycle - 1) * this.config.timeSignature.beatsPerBar + (this.config.timeSignature.beatsPerBar - this.currentBeatInBar + 1);
      } else {
        const elapsedMinutes = (Date.now() - this.trainerStartTimeMs) / (60 * 1000);
        progressToNextJump = (elapsedMinutes % this.config.trainer.intervalValue) / this.config.trainer.intervalValue;
      }
    }

    if (this.onBeatCallback) {
      this.onBeatCallback({
        beatInBar: this.currentBeatInBar,
        barIndex: this.currentBarIndex,
        accent: isAccent,
        bpm: this.currentBpm,
        progressToNextJump,
        beatsUntilJump,
      });
    }
  }

  private advanceNote() {
    // 60.0 / BPM = seconds per quarter note
    const secondsPerBeat = 60.0 / this.currentBpm;
    // Adjust if beat unit is 8th note (e.g. 6/8)
    const factor = 4.0 / this.config.timeSignature.beatUnit;
    this.nextNoteTime += secondsPerBeat * factor;

    this.currentBeatInBar++;
    if (this.currentBeatInBar > this.config.timeSignature.beatsPerBar) {
      this.currentBeatInBar = 1;
      this.currentBarIndex++;
      this.barsCountInInterval++;

      // Check trainer speed step
      if (this.config.trainer.enabled) {
        this.checkTrainerStep();
      }
    }
  }

  private checkTrainerStep() {
    const trainer = this.config.trainer;
    let shouldStep = false;

    if (trainer.intervalKind === 'BARS') {
      if (this.barsCountInInterval >= trainer.intervalValue) {
        shouldStep = true;
        this.barsCountInInterval = 0;
      }
    } else {
      const elapsedMinutes = (Date.now() - this.trainerStartTimeMs) / (60 * 1000);
      if (elapsedMinutes >= trainer.intervalValue) {
        shouldStep = true;
        this.trainerStartTimeMs = Date.now();
      }
    }

    if (shouldStep) {
      if (trainer.startBpm <= trainer.targetBpm) {
        this.currentBpm = Math.min(trainer.targetBpm, this.currentBpm + trainer.incrementBpm);
      } else {
        this.currentBpm = Math.max(trainer.targetBpm, this.currentBpm - trainer.incrementBpm);
      }
    }
  }
}
