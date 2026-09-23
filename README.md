# GuitarLab

Android companion for guitarists: a low-latency chromatic tuner and a smart metronome with auto-speed trainer mode.

- **UI:** Jetpack Compose + Material 3
- **Architecture:** Clean layers (`ui`, `domain`, `audio`, `data`) with Hilt, Coroutines, and Flow
- **minSdk:** 24 · **package:** `com.mmt.guitarlab`

## Features

### Chromatic tuner
Real-time pitch tracking from the microphone (`AudioRecord`) using the YIN algorithm. Shows note name, cents deviation, and frequency. A4 is configurable (default 440 Hz).

### Metronome + trainer
Click generation through `AudioTrack` on a dedicated audio thread. BPM 30–300, common time signatures, and an auto-speed trainer that raises tempo every N bars or every N minutes toward a target BPM.

## Build

Open the project in Android Studio or run:

```bash
./gradlew :app:assembleDebug
```

Microphone permission is requested in-app before the tuner starts.
