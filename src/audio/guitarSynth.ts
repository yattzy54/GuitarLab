import { getAudioContext } from './audioContext';

/**
 * Karplus-Strong Plucked String Synthesis for realistic guitar sound
 */
export function playGuitarPluck(frequencyHz: number, durationSeconds = 1.8, velocity = 0.8) {
  if (frequencyHz <= 20 || frequencyHz > 2500) return;

  try {
    const ctx = getAudioContext();
    if (ctx.state === 'suspended') {
      ctx.resume().catch(() => {});
    }

    const sampleRate = ctx.sampleRate;
    const periodSamples = Math.round(sampleRate / frequencyHz);
    if (periodSamples <= 0 || periodSamples > sampleRate) return;

    // Create an audio buffer for the plucked sound
    const totalSamples = Math.min(sampleRate * durationSeconds, sampleRate * 3);
    const audioBuffer = ctx.createBuffer(1, totalSamples, sampleRate);
    const channelData = audioBuffer.getChannelData(0);

    // Initial noise burst of length periodSamples
    for (let i = 0; i < periodSamples; i++) {
      channelData[i] = (Math.random() * 2 - 1) * velocity;
    }

    // Karplus-Strong feedback loop with damping factor
    const damping = 0.992;
    for (let i = periodSamples; i < totalSamples; i++) {
      // Lowpass averaging filter
      channelData[i] = (channelData[i - periodSamples] + channelData[i - periodSamples - 1]) * 0.5 * damping;
    }

    const source = ctx.createBufferSource();
    source.buffer = audioBuffer;

    const gainNode = ctx.createGain();
    gainNode.gain.setValueAtTime(0.9, ctx.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + durationSeconds);

    source.connect(gainNode);
    gainNode.connect(ctx.destination);

    source.start();
  } catch (err) {
    console.warn('Error playing guitar pluck:', err);
  }
}

/**
 * Play standard metronome click
 */
export function playMetronomeClick(accent: boolean, volume = 0.8) {
  try {
    const ctx = getAudioContext();
    if (ctx.state === 'suspended') {
      ctx.resume().catch(() => {});
    }

    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    // Accent: high pitched bright woodblock/beep (2200Hz -> 800Hz), Normal: 1000Hz -> 400Hz
    const freq = accent ? 2200 : 1200;
    osc.type = accent ? 'triangle' : 'sine';
    osc.frequency.setValueAtTime(freq, ctx.currentTime);
    osc.frequency.exponentialRampToValueAtTime(accent ? 600 : 300, ctx.currentTime + 0.04);

    const now = ctx.currentTime;
    const maxGain = (accent ? 1.0 : 0.65) * volume;
    gain.gain.setValueAtTime(maxGain, now);
    gain.gain.exponentialRampToValueAtTime(0.0001, now + 0.05);

    osc.connect(gain);
    gain.connect(ctx.destination);

    osc.start(now);
    osc.stop(now + 0.055);
  } catch (err) {
    console.warn('Error playing metronome click:', err);
  }
}

/**
 * Play a sustained reference pitch for tuning by ear
 */
let referenceOsc: OscillatorNode | null = null;
let referenceGain: GainNode | null = null;

export function playReferenceTone(frequencyHz: number, volume = 0.5) {
  stopReferenceTone();
  try {
    const ctx = getAudioContext();
    if (ctx.state === 'suspended') {
      ctx.resume().catch(() => {});
    }

    referenceOsc = ctx.createOscillator();
    referenceGain = ctx.createGain();

    referenceOsc.type = 'triangle';
    referenceOsc.frequency.setValueAtTime(frequencyHz, ctx.currentTime);

    referenceGain.gain.setValueAtTime(0.001, ctx.currentTime);
    referenceGain.gain.linearRampToValueAtTime(volume * 0.5, ctx.currentTime + 0.05);

    referenceOsc.connect(referenceGain);
    referenceGain.connect(ctx.destination);

    referenceOsc.start();
  } catch (err) {
    console.warn('Error playing reference tone:', err);
  }
}

export function stopReferenceTone() {
  if (referenceOsc && referenceGain) {
    try {
      const ctx = getAudioContext();
      referenceGain.gain.linearRampToValueAtTime(0.001, ctx.currentTime + 0.05);
      setTimeout(() => {
        try {
          referenceOsc?.stop();
          referenceOsc?.disconnect();
          referenceGain?.disconnect();
        } catch {
          // ignore
        }
        referenceOsc = null;
        referenceGain = null;
      }, 60);
    } catch {
      referenceOsc = null;
      referenceGain = null;
    }
  }
}


/**
 * Studio-grade Physical Modeling Drum Synth for Web Audio:
 * - Kick: Punchy pitch drop (140Hz -> 42Hz) + acoustic beater click
 * - Snare: Membrane pitch tone + snare wire filtered noise
 * - Hi-Hat / Cymbal: Crisp metallic inharmonic cluster + bandpass decay
 * - Tom: Resonant drum shell drop (160Hz -> 80Hz)
 */
export function playDrumHit(drumIndex: number, velocity = 0.9) {
  try {
    const ctx = getAudioContext();
    if (ctx.state === 'suspended') {
      ctx.resume().catch(() => {});
    }
    const t0 = ctx.currentTime;

    switch (drumIndex) {
      case 0: {
        // Crash cymbal
        const noise = ctx.createBufferSource();
        const buf = ctx.createBuffer(1, ctx.sampleRate * 1.5, ctx.sampleRate);
        const data = buf.getChannelData(0);
        for (let i = 0; i < data.length; i++) {
          data[i] = (Math.random() * 2 - 1);
        }
        noise.buffer = buf;
        const bpf = ctx.createBiquadFilter();
        bpf.type = 'highpass';
        bpf.frequency.setValueAtTime(4500, t0);
        const gain = ctx.createGain();
        gain.gain.setValueAtTime(0.7 * velocity, t0);
        gain.gain.exponentialRampToValueAtTime(0.001, t0 + 1.4);
        noise.connect(bpf);
        bpf.connect(gain);
        gain.connect(ctx.destination);
        noise.start(t0);
        break;
      }
      case 1: {
        // Hi-Hat
        const noise = ctx.createBufferSource();
        const buf = ctx.createBuffer(1, ctx.sampleRate * 0.15, ctx.sampleRate);
        const data = buf.getChannelData(0);
        for (let i = 0; i < data.length; i++) {
          data[i] = (Math.random() * 2 - 1);
        }
        noise.buffer = buf;
        const hpf = ctx.createBiquadFilter();
        hpf.type = 'highpass';
        hpf.frequency.setValueAtTime(7000, t0);
        const gain = ctx.createGain();
        gain.gain.setValueAtTime(0.6 * velocity, t0);
        gain.gain.exponentialRampToValueAtTime(0.001, t0 + 0.12);
        noise.connect(hpf);
        hpf.connect(gain);
        gain.connect(ctx.destination);
        noise.start(t0);
        break;
      }
      case 2: {
        // Snare Drum (Tone + Wire rattle)
        const osc = ctx.createOscillator();
        const oscGain = ctx.createGain();
        osc.frequency.setValueAtTime(195, t0);
        osc.frequency.exponentialRampToValueAtTime(130, t0 + 0.12);
        oscGain.gain.setValueAtTime(0.8 * velocity, t0);
        oscGain.gain.exponentialRampToValueAtTime(0.001, t0 + 0.18);
        osc.connect(oscGain);
        oscGain.connect(ctx.destination);
        osc.start(t0);
        osc.stop(t0 + 0.2);

        // Snare wire noise
        const noise = ctx.createBufferSource();
        const buf = ctx.createBuffer(1, ctx.sampleRate * 0.25, ctx.sampleRate);
        const data = buf.getChannelData(0);
        for (let i = 0; i < data.length; i++) {
          data[i] = (Math.random() * 2 - 1);
        }
        noise.buffer = buf;
        const hpf = ctx.createBiquadFilter();
        hpf.type = 'highpass';
        hpf.frequency.setValueAtTime(1500, t0);
        const noiseGain = ctx.createGain();
        noiseGain.gain.setValueAtTime(0.7 * velocity, t0);
        noiseGain.gain.exponentialRampToValueAtTime(0.001, t0 + 0.22);
        noise.connect(hpf);
        hpf.connect(noiseGain);
        noiseGain.connect(ctx.destination);
        noise.start(t0);
        break;
      }
      case 3: {
        // Tom
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.frequency.setValueAtTime(160, t0);
        osc.frequency.exponentialRampToValueAtTime(80, t0 + 0.22);
        gain.gain.setValueAtTime(0.9 * velocity, t0);
        gain.gain.exponentialRampToValueAtTime(0.001, t0 + 0.3);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(t0);
        osc.stop(t0 + 0.32);
        break;
      }
      default: {
        // Kick Drum (Sub-bass drop + beater punch)
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.frequency.setValueAtTime(135, t0);
        osc.frequency.exponentialRampToValueAtTime(42, t0 + 0.14);
        gain.gain.setValueAtTime(1.2 * velocity, t0);
        gain.gain.exponentialRampToValueAtTime(0.001, t0 + 0.35);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(t0);
        osc.stop(t0 + 0.36);
        break;
      }
    }
  } catch (err) {
    console.warn('Error playing drum hit:', err);
  }
}
