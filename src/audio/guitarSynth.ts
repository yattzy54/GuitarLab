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
