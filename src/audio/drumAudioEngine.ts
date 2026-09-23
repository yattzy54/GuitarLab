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

class DrumAudioEngine {
  private isPlaying = false;
  private currentPattern: DrumPattern | null = null;
  private bpm = 120;
  private swing = 0; // 0 to 0.5 (triplet feel)
  private currentStep = 0;
  private nextStepTime = 0;
  private timerId: number | null = null;
  private lookaheadMs = 25;
  private scheduleAheadTime = 0.1; // 100ms
  private masterGainNode: GainNode | null = null;
  private isMetronomeEnabled = false;

  private onStepCallback: ((step: number) => void) | null = null;
  private onPlayStateCallback: ((playing: boolean) => void) | null = null;

  private masterVolume = 0.85;

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

  // --- Sound Synthesizers for each drum voice ---

  public playKick(time?: number, velocity = 0.95) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    // Deep sub pitch drop
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    osc.type = 'sine';
    osc.frequency.setValueAtTime(145, t);
    osc.frequency.exponentialRampToValueAtTime(42, t + 0.08);

    gain.gain.setValueAtTime(velocity * 1.1, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + 0.32);

    osc.connect(gain);
    gain.connect(dest);

    osc.start(t);
    osc.stop(t + 0.33);

    // Beater click punch
    const clickOsc = ctx.createOscillator();
    const clickGain = ctx.createGain();
    clickOsc.type = 'triangle';
    clickOsc.frequency.setValueAtTime(320, t);
    clickOsc.frequency.exponentialRampToValueAtTime(60, t + 0.02);

    clickGain.gain.setValueAtTime(velocity * 0.7, t);
    clickGain.gain.exponentialRampToValueAtTime(0.001, t + 0.025);

    clickOsc.connect(clickGain);
    clickGain.connect(dest);

    clickOsc.start(t);
    clickOsc.stop(t + 0.03);
  }

  public playSnare(time?: number, velocity = 0.85) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    // Body tone (dual tone)
    const osc1 = ctx.createOscillator();
    const osc2 = ctx.createOscillator();
    const bodyGain = ctx.createGain();

    osc1.type = 'triangle';
    osc1.frequency.setValueAtTime(185, t);
    osc1.frequency.exponentialRampToValueAtTime(120, t + 0.09);

    osc2.type = 'sine';
    osc2.frequency.setValueAtTime(280, t);
    osc2.frequency.exponentialRampToValueAtTime(140, t + 0.07);

    bodyGain.gain.setValueAtTime(velocity * 0.75, t);
    bodyGain.gain.exponentialRampToValueAtTime(0.001, t + 0.16);

    osc1.connect(bodyGain);
    osc2.connect(bodyGain);
    bodyGain.connect(dest);

    osc1.start(t);
    osc2.start(t);
    osc1.stop(t + 0.18);
    osc2.stop(t + 0.18);

    // Snare wires noise
    const noiseLen = Math.floor(ctx.sampleRate * 0.22);
    const noiseBuf = ctx.createBuffer(1, noiseLen, ctx.sampleRate);
    const data = noiseBuf.getChannelData(0);
    for (let i = 0; i < noiseLen; i++) {
      data[i] = Math.random() * 2 - 1;
    }

    const noiseSource = ctx.createBufferSource();
    noiseSource.buffer = noiseBuf;

    const bpf = ctx.createBiquadFilter();
    bpf.type = 'bandpass';
    bpf.frequency.setValueAtTime(2600, t);
    bpf.Q.setValueAtTime(1.2, t);

    const hpf = ctx.createBiquadFilter();
    hpf.type = 'highpass';
    hpf.frequency.setValueAtTime(1100, t);

    const noiseGain = ctx.createGain();
    noiseGain.gain.setValueAtTime(velocity * 0.9, t);
    noiseGain.gain.exponentialRampToValueAtTime(0.001, t + 0.22);

    noiseSource.connect(bpf);
    bpf.connect(hpf);
    hpf.connect(noiseGain);
    noiseGain.connect(dest);

    noiseSource.start(t);
  }

  public playHihatClosed(time?: number, velocity = 0.65) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const noiseLen = Math.floor(ctx.sampleRate * 0.06);
    const noiseBuf = ctx.createBuffer(1, noiseLen, ctx.sampleRate);
    const data = noiseBuf.getChannelData(0);
    for (let i = 0; i < noiseLen; i++) {
      data[i] = Math.random() * 2 - 1;
    }

    const noise = ctx.createBufferSource();
    noise.buffer = noiseBuf;

    const hpf = ctx.createBiquadFilter();
    hpf.type = 'highpass';
    hpf.frequency.setValueAtTime(7500, t);

    const gain = ctx.createGain();
    gain.gain.setValueAtTime(velocity * 0.65, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + 0.05);

    noise.connect(hpf);
    hpf.connect(gain);
    gain.connect(dest);

    noise.start(t);
  }

  public playHihatOpen(time?: number, velocity = 0.75) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const noiseLen = Math.floor(ctx.sampleRate * 0.35);
    const noiseBuf = ctx.createBuffer(1, noiseLen, ctx.sampleRate);
    const data = noiseBuf.getChannelData(0);
    for (let i = 0; i < noiseLen; i++) {
      data[i] = Math.random() * 2 - 1;
    }

    const noise = ctx.createBufferSource();
    noise.buffer = noiseBuf;

    const hpf = ctx.createBiquadFilter();
    hpf.type = 'highpass';
    hpf.frequency.setValueAtTime(6000, t);

    const gain = ctx.createGain();
    gain.gain.setValueAtTime(velocity * 0.7, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + 0.32);

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
    gain.gain.setValueAtTime(velocity * 0.75, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + 1.4);

    noise.connect(hpf);
    hpf.connect(gain);
    gain.connect(dest);

    noise.start(t);
  }

  public playRide(time?: number, velocity = 0.7) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    // Metallic bell harmonic
    const osc = ctx.createOscillator();
    const oscGain = ctx.createGain();
    osc.type = 'sine';
    osc.frequency.setValueAtTime(860, t);

    oscGain.gain.setValueAtTime(velocity * 0.45, t);
    oscGain.gain.exponentialRampToValueAtTime(0.001, t + 0.45);

    osc.connect(oscGain);
    oscGain.connect(dest);
    osc.start(t);
    osc.stop(t + 0.5);

    // High shimmer
    const noiseLen = Math.floor(ctx.sampleRate * 0.6);
    const noiseBuf = ctx.createBuffer(1, noiseLen, ctx.sampleRate);
    const data = noiseBuf.getChannelData(0);
    for (let i = 0; i < noiseLen; i++) {
      data[i] = Math.random() * 2 - 1;
    }

    const noise = ctx.createBufferSource();
    noise.buffer = noiseBuf;

    const bpf = ctx.createBiquadFilter();
    bpf.type = 'bandpass';
    bpf.frequency.setValueAtTime(7000, t);
    bpf.Q.setValueAtTime(3.0, t);

    const gain = ctx.createGain();
    gain.gain.setValueAtTime(velocity * 0.5, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + 0.55);

    noise.connect(bpf);
    bpf.connect(gain);
    gain.connect(dest);

    noise.start(t);
  }

  public playTomHigh(time?: number, velocity = 0.8) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    osc.type = 'sine';
    osc.frequency.setValueAtTime(190, t);
    osc.frequency.exponentialRampToValueAtTime(105, t + 0.18);

    gain.gain.setValueAtTime(velocity * 0.85, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + 0.25);

    osc.connect(gain);
    gain.connect(dest);

    osc.start(t);
    osc.stop(t + 0.26);
  }

  public playTomLow(time?: number, velocity = 0.85) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    osc.type = 'sine';
    osc.frequency.setValueAtTime(125, t);
    osc.frequency.exponentialRampToValueAtTime(65, t + 0.24);

    gain.gain.setValueAtTime(velocity * 0.9, t);
    gain.gain.exponentialRampToValueAtTime(0.001, t + 0.32);

    osc.connect(gain);
    gain.connect(dest);

    osc.start(t);
    osc.stop(t + 0.33);
  }

  public playPercussion(time?: number, velocity = 0.8) {
    const ctx = getAudioContext();
    const t = time ?? ctx.currentTime;
    const dest = this.getMasterGain();

    // Handclap multi-burst
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

  // --- Scheduler ---

  private scheduleStep(step: number, time: number) {
    if (!this.currentPattern) return;

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

    // Optional Metronome click
    if (this.isMetronomeEnabled) {
      const isQuarter = step % 4 === 0;
      if (isQuarter) {
        this.playMetronomeTick(time, step === 0);
      }
    }

    // Schedule UI callback near exact time
    const ctx = getAudioContext();
    const delayMs = Math.max(0, (time - ctx.currentTime) * 1000);
    setTimeout(() => {
      if (this.isPlaying && this.onStepCallback) {
        this.onStepCallback(step);
      }
    }, delayMs);
  }

  private advanceStep() {
    if (!this.currentPattern) return;

    const totalSteps = this.currentPattern.stepsCount || 16;
    const secondsPerBeat = 60.0 / this.bpm;
    // Step is 16th note in 4/4 (4 steps per beat) or 8th note in 6/8
    const isCompound = this.currentPattern.timeSignature === '6/8' || this.currentPattern.timeSignature === '12/8';
    const stepsPerBeat = isCompound ? 3 : 4;
    let stepDuration = secondsPerBeat / stepsPerBeat;

    // Apply swing on odd steps if not compound
    if (!isCompound && this.swing > 0) {
      if (this.currentStep % 2 === 0) {
        stepDuration += stepDuration * this.swing;
      } else {
        stepDuration -= stepDuration * this.swing;
      }
    }

    this.nextStepTime += stepDuration;
    this.currentStep = (this.currentStep + 1) % totalSteps;
  }

  private scheduler() {
    const ctx = getAudioContext();
    while (this.nextStepTime < ctx.currentTime + this.scheduleAheadTime) {
      this.scheduleStep(this.currentStep, this.nextStepTime);
      this.advanceStep();
    }
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
    this.nextStepTime = ctx.currentTime + 0.05;

    if (this.timerId !== null) {
      window.clearInterval(this.timerId);
    }
    this.timerId = window.setInterval(() => this.scheduler(), this.lookaheadMs);

    if (this.onPlayStateCallback) {
      this.onPlayStateCallback(true);
    }
  }

  public stop() {
    this.isPlaying = false;
    if (this.timerId !== null) {
      window.clearInterval(this.timerId);
      this.timerId = null;
    }
    this.currentStep = 0;

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
