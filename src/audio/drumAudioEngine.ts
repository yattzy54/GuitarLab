import { getAudioContext, ensureAudioContextStarted } from './audioContext';
import { DrumPattern } from '../data/drumPatterns';

export type DrumInstrument =
  | 'kick'
  | 'snare'
  | 'hihatClosed'
  | 'hihatOpen'
  | 'crash'
  | 'ride'
  | 'tomHigh'
  | 'tomLow'
  | 'percussion';

export type DrumKitType = 'rock' | 'metal' | 'pop' | 'electronic';

interface StepWindow {
  step: number;
  time: number;
  duration: number;
}

class DrumAudioEngine {
  private isPlaying = false;
  private currentPattern: DrumPattern | null = null;
  private bpm = 120;
  private swing = 0; // 0 to 0.5 (triplet feel)
  private currentStep = 0;
  private nextStepTime = 0;
  private timerId: number | null = null;
  private animFrameId: number | null = null;
  private lookaheadMs = 25;
  private scheduleAheadTime = 0.12; // 120ms
  private masterGainNode: GainNode | null = null;
  private isMetronomeEnabled = false;
  private currentKit: DrumKitType = 'rock';

  private scheduledWindows: StepWindow[] = [];
  private lastReportedStep = -1;

  private onStepCallback: ((step: number) => void) | null = null;
  private onPlayStateCallback: ((playing: boolean) => void) | null = null;

  private masterVolume = 0.85;

  public setDrumKit(kit: DrumKitType) {
    this.currentKit = kit;
  }

  public getDrumKit(): DrumKitType {
    return this.currentKit;
  }

  private getMasterGain(): GainNode {
    const ctx = getAudioContext();
    if (!this.masterGainNode) {
      this.masterGainNode = ctx.createGain();
      this.masterGainNode.gain.setValueAtTime(this.masterVolume, ctx.currentTime);
      this.masterGainNode.connect(ctx.destination);
    }
    return this.masterGainNode;
  }

  public setMasterVolume(vol: number) {
    this.masterVolume = Math.max(0, Math.min(1, vol));
    if (this.masterGainNode) {
      const ctx = getAudioContext();
      this.masterGainNode.gain.setValueAtTime(this.masterVolume, ctx.currentTime);
    }
  }

  public getMasterVolume(): number {
    return this.masterVolume;
  }

  public setMetronome(enabled: boolean) {
    this.isMetronomeEnabled = enabled;
  }

  public isMetronomeOn(): boolean {
    return this.isMetronomeEnabled;
  }

  public setSwing(swingAmount: number) {
    this.swing = Math.max(0, Math.min(0.5, swingAmount));
  }

  public getSwing(): number {
    return this.swing;
  }

  public setCallbacks(
    onStep: (step: number) => void,
    onPlayState: (playing: boolean) => void
  ) {
    this.onStepCallback = onStep;
    this.onPlayStateCallback = onPlayState;
  }

  // --- Sound Synthesizers for each drum voice with kit variations ---

  public playKick(time?: number, velocity = 0.95) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    switch (this.currentKit) {
      case 'metal': {
        // High transient click (3800Hz beater) + fast punch 60Hz
        osc.type = 'sine';
        osc.frequency.setValueAtTime(170, t);
        osc.frequency.exponentialRampToValueAtTime(55, t + 0.04);

        gain.gain.setValueAtTime(velocity * 1.2, t);
        gain.gain.exponentialRampToValueAtTime(0.001, t + 0.16);

        // Sharp trigger click
        const clickOsc = ctx.createOscillator();
        const clickGain = ctx.createGain();
        clickOsc.type = 'triangle';
        clickOsc.frequency.setValueAtTime(3800, t);
        clickOsc.frequency.exponentialRampToValueAtTime(600, t + 0.012);
        clickGain.gain.setValueAtTime(velocity * 1.1, t);
        clickGain.gain.exponentialRampToValueAtTime(0.001, t + 0.018);

        clickOsc.connect(clickGain);
        clickGain.connect(dest);
        clickOsc.start(t);
        clickOsc.stop(t + 0.02);
        break;
      }
      case 'electronic': {
        // Legendary TR-808 deep sine boom with extended low-end
        osc.type = 'sine';
        osc.frequency.setValueAtTime(115, t);
        osc.frequency.exponentialRampToValueAtTime(38, t + 0.09);

        gain.gain.setValueAtTime(velocity * 1.3, t);
        gain.gain.exponentialRampToValueAtTime(0.001, t + 0.45);
        break;
      }
      case 'pop': {
        // Radio punch: round deep sub with warm body
        osc.type = 'sine';
        osc.frequency.setValueAtTime(130, t);
        osc.frequency.exponentialRampToValueAtTime(45, t + 0.07);

        gain.gain.setValueAtTime(velocity * 1.05, t);
        gain.gain.exponentialRampToValueAtTime(0.001, t + 0.28);
        break;
      }
      case 'rock':
      default: {
        // Acoustic punch with wooden beater
        osc.type = 'sine';
        osc.frequency.setValueAtTime(150, t);
        osc.frequency.exponentialRampToValueAtTime(48, t + 0.06);

        gain.gain.setValueAtTime(velocity * 1.15, t);
        gain.gain.exponentialRampToValueAtTime(0.001, t + 0.26);

        const clickOsc = ctx.createOscillator();
        const clickGain = ctx.createGain();
        clickOsc.type = 'triangle';
        clickOsc.frequency.setValueAtTime(320, t);
        clickOsc.frequency.exponentialRampToValueAtTime(80, t + 0.018);
        clickGain.gain.setValueAtTime(velocity * 0.7, t);
        clickGain.gain.exponentialRampToValueAtTime(0.001, t + 0.022);

        clickOsc.connect(clickGain);
        clickGain.connect(dest);
        clickOsc.start(t);
        clickOsc.stop(t + 0.025);
        break;
      }
    }

    osc.connect(gain);
    gain.connect(dest);
    osc.start(t);
    osc.stop(t + 0.48);
  }

  public playSnare(time?: number, velocity = 0.85) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const osc1 = ctx.createOscillator();
    const bodyGain = ctx.createGain();

    const noiseLen = Math.floor(ctx.sampleRate * 0.22);
    const noiseBuf = ctx.createBuffer(1, noiseLen, ctx.sampleRate);
    const data = noiseBuf.getChannelData(0);
    for (let i = 0; i < noiseLen; i++) {
      data[i] = Math.random() * 2 - 1;
    }
    const noiseSource = ctx.createBufferSource();
    noiseSource.buffer = noiseBuf;

    const bpf = ctx.createBiquadFilter();
    const noiseGain = ctx.createGain();

    if (this.currentKit === 'metal') {
      // High-tension steel snare crack
      osc1.type = 'triangle';
      osc1.frequency.setValueAtTime(310, t);
      osc1.frequency.exponentialRampToValueAtTime(170, t + 0.07);
      bodyGain.gain.setValueAtTime(velocity * 0.9, t);
      bodyGain.gain.exponentialRampToValueAtTime(0.001, t + 0.14);

      bpf.type = 'bandpass';
      bpf.frequency.setValueAtTime(3200, t);
      bpf.Q.setValueAtTime(1.8, t);
      noiseGain.gain.setValueAtTime(velocity * 1.1, t);
      noiseGain.gain.exponentialRampToValueAtTime(0.001, t + 0.16);
    } else if (this.currentKit === 'electronic') {
      // TR-909 snappy dual-tone
      osc1.type = 'triangle';
      osc1.frequency.setValueAtTime(180, t);
      osc1.frequency.exponentialRampToValueAtTime(120, t + 0.06);
      bodyGain.gain.setValueAtTime(velocity * 0.8, t);
      bodyGain.gain.exponentialRampToValueAtTime(0.001, t + 0.12);

      bpf.type = 'highpass';
      bpf.frequency.setValueAtTime(1800, t);
      noiseGain.gain.setValueAtTime(velocity * 0.95, t);
      noiseGain.gain.exponentialRampToValueAtTime(0.001, t + 0.18);
    } else if (this.currentKit === 'pop') {
      // Layered clap + warm snare
      osc1.type = 'sine';
      osc1.frequency.setValueAtTime(210, t);
      osc1.frequency.exponentialRampToValueAtTime(130, t + 0.09);
      bodyGain.gain.setValueAtTime(velocity * 0.7, t);
      bodyGain.gain.exponentialRampToValueAtTime(0.001, t + 0.18);

      bpf.type = 'bandpass';
      bpf.frequency.setValueAtTime(2200, t);
      bpf.Q.setValueAtTime(1.2, t);
      noiseGain.gain.setValueAtTime(velocity * 0.85, t);
      noiseGain.gain.exponentialRampToValueAtTime(0.001, t + 0.24);
    } else {
      // Rock: 14" maple snare
      osc1.type = 'triangle';
      osc1.frequency.setValueAtTime(185, t);
      osc1.frequency.exponentialRampToValueAtTime(115, t + 0.09);
      bodyGain.gain.setValueAtTime(velocity * 0.8, t);
      bodyGain.gain.exponentialRampToValueAtTime(0.001, t + 0.17);

      bpf.type = 'bandpass';
      bpf.frequency.setValueAtTime(2600, t);
      bpf.Q.setValueAtTime(1.3, t);
      noiseGain.gain.setValueAtTime(velocity * 0.9, t);
      noiseGain.gain.exponentialRampToValueAtTime(0.001, t + 0.22);
    }

    osc1.connect(bodyGain);
    bodyGain.connect(dest);
    osc1.start(t);
    osc1.stop(t + 0.25);

    noiseSource.connect(bpf);
    bpf.connect(noiseGain);
    noiseGain.connect(dest);
    noiseSource.start(t);
  }

  public playHihatClosed(time?: number, velocity = 0.65) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const noiseLen = Math.floor(ctx.sampleRate * 0.05);
    const noiseBuf = ctx.createBuffer(1, noiseLen, ctx.sampleRate);
    const data = noiseBuf.getChannelData(0);
    for (let i = 0; i < noiseLen; i++) {
      data[i] = Math.random() * 2 - 1;
    }

    const noise = ctx.createBufferSource();
    noise.buffer = noiseBuf;

    const hpf = ctx.createBiquadFilter();
    hpf.type = 'highpass';
    hpf.frequency.setValueAtTime(this.currentKit === 'metal' ? 8800 : 7500, t);

    const gain = ctx.createGain();
    gain.gain.setValueAtTime(velocity * 0.7, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + (this.currentKit === 'metal' ? 0.035 : 0.05));

    noise.connect(hpf);
    hpf.connect(gain);
    gain.connect(dest);

    noise.start(t);
  }

  public playHihatOpen(time?: number, velocity = 0.75) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const decay = this.currentKit === 'electronic' ? 0.35 : 0.28;
    const noiseLen = Math.floor(ctx.sampleRate * decay);
    const noiseBuf = ctx.createBuffer(1, noiseLen, ctx.sampleRate);
    const data = noiseBuf.getChannelData(0);
    for (let i = 0; i < noiseLen; i++) {
      data[i] = Math.random() * 2 - 1;
    }

    const noise = ctx.createBufferSource();
    noise.buffer = noiseBuf;

    const hpf = ctx.createBiquadFilter();
    hpf.type = 'highpass';
    hpf.frequency.setValueAtTime(6200, t);

    const gain = ctx.createGain();
    gain.gain.setValueAtTime(velocity * 0.75, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + decay);

    noise.connect(hpf);
    hpf.connect(gain);
    gain.connect(dest);

    noise.start(t);
  }

  public playCrash(time?: number, velocity = 0.8) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const noiseLen = Math.floor(ctx.sampleRate * 1.5);
    const noiseBuf = ctx.createBuffer(1, noiseLen, ctx.sampleRate);
    const data = noiseBuf.getChannelData(0);
    for (let i = 0; i < noiseLen; i++) {
      data[i] = Math.random() * 2 - 1;
    }

    const noise = ctx.createBufferSource();
    noise.buffer = noiseBuf;

    const hpf = ctx.createBiquadFilter();
    hpf.type = 'highpass';
    hpf.frequency.setValueAtTime(4500, t);

    const gain = ctx.createGain();
    gain.gain.setValueAtTime(velocity * 0.8, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + 1.2);

    noise.connect(hpf);
    hpf.connect(gain);
    gain.connect(dest);

    noise.start(t);
  }

  public playRide(time?: number, velocity = 0.7) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const osc = ctx.createOscillator();
    const oscGain = ctx.createGain();
    osc.type = 'sine';
    osc.frequency.setValueAtTime(this.currentKit === 'metal' ? 2800 : 2200, t);

    oscGain.gain.setValueAtTime(velocity * 0.45, t);
    oscGain.gain.exponentialRampToValueAtTime(0.001, t + 0.42);

    osc.connect(oscGain);
    oscGain.connect(dest);
    osc.start(t);
    osc.stop(t + 0.45);
  }

  public playTomHigh(time?: number, velocity = 0.8) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    osc.type = 'sine';
    osc.frequency.setValueAtTime(190, t);
    osc.frequency.exponentialRampToValueAtTime(105, t + 0.16);

    gain.gain.setValueAtTime(velocity * 0.85, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + 0.24);

    osc.connect(gain);
    gain.connect(dest);

    osc.start(t);
    osc.stop(t + 0.25);
  }

  public playTomLow(time?: number, velocity = 0.85) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    osc.type = 'sine';
    osc.frequency.setValueAtTime(125, t);
    osc.frequency.exponentialRampToValueAtTime(62, t + 0.22);

    gain.gain.setValueAtTime(velocity * 0.9, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + 0.30);

    osc.connect(gain);
    gain.connect(dest);

    osc.start(t);
    osc.stop(t + 0.32);
  }

  public playPercussion(time?: number, velocity = 0.8) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const burstCount = 3;
    for (let b = 0; b < burstCount; b++) {
      const burstTime = t + b * 0.012;
      const noiseLen = Math.floor(ctx.sampleRate * 0.025);
      const noiseBuf = ctx.createBuffer(1, noiseLen, ctx.sampleRate);
      const data = noiseBuf.getChannelData(0);
      for (let i = 0; i < noiseLen; i++) {
        data[i] = Math.random() * 2 - 1;
      }

      const noise = ctx.createBufferSource();
      noise.buffer = noiseBuf;

      const bpf = ctx.createBiquadFilter();
      bpf.type = 'bandpass';
      bpf.frequency.setValueAtTime(1200, burstTime);

      const gain = ctx.createGain();
      gain.gain.setValueAtTime(velocity * (b === burstCount - 1 ? 0.9 : 0.4), burstTime);
      gain.gain.exponentialRampToValueAtTime(0.001, burstTime + (b === burstCount - 1 ? 0.18 : 0.02));

      noise.connect(bpf);
      bpf.connect(gain);
      gain.connect(dest);

      noise.start(burstTime);
    }
  }

  public playMetronomeTick(time: number, isAccent: boolean) {
    const ctx = getAudioContext();
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    osc.type = 'sine';
    osc.frequency.setValueAtTime(isAccent ? 1760 : 880, time);

    gain.gain.setValueAtTime(0.35, time);
    gain.gain.exponentialRampToValueAtTime(0.001, time + 0.03);

    osc.connect(gain);
    gain.connect(this.getMasterGain());

    osc.start(time);
    osc.stop(time + 0.035);
  }

  public playVoice(voice: DrumInstrument, time?: number, velocity = 0.85) {
    switch (voice) {
      case 'kick':
        this.playKick(time, velocity);
        break;
      case 'snare':
        this.playSnare(time, velocity);
        break;
      case 'hihatClosed':
        this.playHihatClosed(time, velocity);
        break;
      case 'hihatOpen':
        this.playHihatOpen(time, velocity);
        break;
      case 'crash':
        this.playCrash(time, velocity);
        break;
      case 'ride':
        this.playRide(time, velocity);
        break;
      case 'tomHigh':
        this.playTomHigh(time, velocity);
        break;
      case 'tomLow':
        this.playTomLow(time, velocity);
        break;
      case 'percussion':
        this.playPercussion(time, velocity);
        break;
    }
  }

  // --- Scheduler & Animation Frame Step Clock ---

  private scheduleStep(step: number, time: number, duration: number) {
    if (!this.currentPattern) return;

    this.scheduledWindows.push({ step, time, duration });

    const tracks = this.currentPattern.tracks;

    if (tracks.kick?.[step]) this.playKick(time, 0.95);
    if (tracks.snare?.[step]) this.playSnare(time, 0.9);
    if (tracks.hihatClosed?.[step]) this.playHihatClosed(time, 0.7);
    if (tracks.hihatOpen?.[step]) this.playHihatOpen(time, 0.75);
    if (tracks.crash?.[step]) this.playCrash(time, 0.8);
    if (tracks.ride?.[step]) this.playRide(time, 0.7);
    if (tracks.tomHigh?.[step]) this.playTomHigh(time, 0.85);
    if (tracks.tomLow?.[step]) this.playTomLow(time, 0.85);
    if (tracks.percussion?.[step]) this.playPercussion(time, 0.8);

    if (this.isMetronomeEnabled && step % 4 === 0) {
      this.playMetronomeTick(time, step === 0);
    }
  }

  private advanceStep() {
    if (!this.currentPattern) return;

    const totalSteps = this.currentPattern.stepsCount || 16;
    const secondsPerBeat = 60.0 / this.bpm;
    const isCompound = this.currentPattern.timeSignature === '6/8' || this.currentPattern.timeSignature === '12/8';
    const stepsPerBeat = isCompound ? 3 : 4;
    let stepDuration = secondsPerBeat / stepsPerBeat;

    if (!isCompound && this.swing > 0) {
      if (this.currentStep % 2 === 0) {
        stepDuration += stepDuration * this.swing;
      } else {
        stepDuration -= stepDuration * this.swing;
      }
    }

    const curTime = this.nextStepTime;
    this.scheduleStep(this.currentStep, curTime, stepDuration);

    this.nextStepTime += stepDuration;
    this.currentStep = (this.currentStep + 1) % totalSteps;
  }

  private scheduler() {
    const ctx = getAudioContext();
    while (this.nextStepTime < ctx.currentTime + this.scheduleAheadTime) {
      this.advanceStep();
    }
  }

  // High-precision requestAnimationFrame loop that guarantees zero skipped visual steps
  private startStepClock() {
    const updateClock = () => {
      if (!this.isPlaying) return;
      const ctx = getAudioContext();
      const now = ctx.currentTime;

      // Find current step window corresponding to actual audio output time
      let activeStep = -1;
      // Clean up past windows and find current
      while (this.scheduledWindows.length > 0 && this.scheduledWindows[0].time + this.scheduledWindows[0].duration < now) {
        this.scheduledWindows.shift();
      }

      if (this.scheduledWindows.length > 0) {
        const first = this.scheduledWindows[0];
        if (now >= first.time) {
          activeStep = first.step;
        }
      }

      if (activeStep !== -1 && activeStep !== this.lastReportedStep) {
        this.lastReportedStep = activeStep;
        if (this.onStepCallback) {
          this.onStepCallback(activeStep);
        }
      }

      this.animFrameId = requestAnimationFrame(updateClock);
    };

    this.animFrameId = requestAnimationFrame(updateClock);
  }

  public async start(pattern: DrumPattern, customBpm?: number) {
    await ensureAudioContextStarted();
    const ctx = getAudioContext();

    this.currentPattern = pattern;
    if (customBpm) {
      this.bpm = customBpm;
    } else {
      this.bpm = pattern.bpm;
    }

    this.isPlaying = true;
    this.currentStep = 0;
    this.lastReportedStep = -1;
    this.scheduledWindows = [];
    this.nextStepTime = ctx.currentTime + 0.04;

    if (this.timerId !== null) {
      window.clearInterval(this.timerId);
    }
    this.timerId = window.setInterval(() => this.scheduler(), this.lookaheadMs);

    if (this.animFrameId !== null) {
      cancelAnimationFrame(this.animFrameId);
    }
    this.startStepClock();

    if (this.onPlayStateCallback) {
      this.onPlayStateCallback(true);
    }
    if (this.onStepCallback) {
      this.onStepCallback(0);
    }
  }

  public stop() {
    this.isPlaying = false;
    if (this.timerId !== null) {
      window.clearInterval(this.timerId);
      this.timerId = null;
    }
    if (this.animFrameId !== null) {
      cancelAnimationFrame(this.animFrameId);
      this.animFrameId = null;
    }
    this.currentStep = 0;
    this.lastReportedStep = -1;
    this.scheduledWindows = [];

    if (this.onPlayStateCallback) {
      this.onPlayStateCallback(false);
    }
    if (this.onStepCallback) {
      this.onStepCallback(-1);
    }
  }

  public setTempo(newBpm: number) {
    this.bpm = Math.max(30, Math.min(280, Math.round(newBpm)));
  }

  public getTempo(): number {
    return this.bpm;
  }

  public getIsPlaying(): boolean {
    return this.isPlaying;
  }

  public setPattern(pattern: DrumPattern) {
    this.currentPattern = pattern;
  }

  public getCurrentPattern(): DrumPattern | null {
    return this.currentPattern;
  }
}

export const drumEngine = new DrumAudioEngine();
