import { DetectedPitch } from '../types';
import { NOTE_NAMES } from '../data/musicTheory';

/**
 * Enhanced YIN pitch detector (de Cheveigné & Kawahara) optimized for
 * electric, acoustic, and bass guitars with rich harmonic overtones and low-signal sustain.
 */
export class YinPitchDetector {
  private sampleRate: number;
  private bufferSize: number;
  private threshold: number;
  private half: number;
  private yin: Float32Array;

  constructor(sampleRate: number, bufferSize = 4096, threshold = 0.18) {
    this.sampleRate = sampleRate;
    this.bufferSize = bufferSize;
    this.threshold = threshold;
    this.half = Math.floor(bufferSize / 2);
    this.yin = new Float32Array(this.half);
  }

  public detect(samples: Float32Array): [frequencyHz: number, clarity: number] {
    const tauMax = this.half;
    this.difference(samples, tauMax);
    this.cumulativeMeanNormalizedDifference(tauMax);
    const tau = this.absoluteThreshold(tauMax);
    if (tau < 2) return [0, 0];

    const betterTau = this.parabolicInterpolation(tau);
    if (betterTau <= 0) return [0, 0];

    const clarity = Math.max(0, Math.min(1, 1 - this.yin[tau]));
    const frequencyHz = this.sampleRate / betterTau;
    return [frequencyHz, clarity];
  }

  private difference(samples: Float32Array, tauMax: number) {
    this.yin[0] = 1.0;
    const len = samples.length;
    const windowSize = len - tauMax;
    for (let tau = 1; tau < tauMax; tau++) {
      let sum = 0;
      let i = 0;
      while (i < windowSize) {
        const delta = samples[i] - samples[i + tau];
        sum += delta * delta;
        i++;
      }
      this.yin[tau] = sum;
    }
  }

  private cumulativeMeanNormalizedDifference(tauMax: number) {
    this.yin[0] = 1.0;
    let running = 0;
    for (let tau = 1; tau < tauMax; tau++) {
      running += this.yin[tau];
      this.yin[tau] = running === 0 ? 1 : (this.yin[tau] * tau) / running;
    }
  }

  private absoluteThreshold(tauMax: number): number {
    let tau = 2;
    while (tau < tauMax) {
      if (this.yin[tau] < this.threshold) {
        while (tau + 1 < tauMax && this.yin[tau + 1] < this.yin[tau]) {
          tau++;
        }
        return tau;
      }
      tau++;
    }

    let minTau = -1;
    let minVal = Number.MAX_VALUE;
    for (let t = 2; t < tauMax; t++) {
      if (this.yin[t] < minVal) {
        minVal = this.yin[t];
        minTau = t;
      }
    }
    return minVal < 0.55 ? minTau : -1;
  }

  private parabolicInterpolation(tau: number): number {
    if (tau <= 0 || tau >= this.yin.length - 1) return tau;
    const s0 = this.yin[tau - 1];
    const s1 = this.yin[tau];
    const s2 = this.yin[tau + 1];
    const denom = 2 * s1 - s2 - s0;
    if (denom === 0) return tau;
    return tau + (s2 - s0) / (2 * denom);
  }
}

/**
 * Calculates note details, cents deviation, and in-tune status from frequency
 */
export function pitchFromFrequency(frequencyHz: number, a4Hz = 440, clarity = 1): DetectedPitch | null {
  if (frequencyHz < 30 || !Number.isFinite(frequencyHz) || frequencyHz > 1600) return null;
  const midi = 69.0 + 12.0 * (Math.log(frequencyHz / a4Hz) / Math.LN2);
  const nearest = Math.max(0, Math.min(127, Math.round(midi)));
  const cents = (midi - nearest) * 100.0;
  const noteIndex = ((nearest % 12) + 12) % 12;
  const octave = Math.floor(nearest / 12) - 1;
  return {
    frequencyHz,
    midiNote: nearest,
    noteName: NOTE_NAMES[noteIndex],
    octave,
    cents,
    clarity,
    inTune: Math.abs(cents) <= 4, // within 4 cents is guitar in-tune tolerance
  };
}
