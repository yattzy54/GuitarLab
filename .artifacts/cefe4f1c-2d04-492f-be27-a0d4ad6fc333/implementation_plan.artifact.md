# Implementation Plan - Extract Audio Engines and Audio Infrastructure to `:core:audio`

Extract low-level audio components, engines (drums, metronome, tuner, playback, slowdown, recorder), exporters (MIDI), and domain interfaces from the `:app` module into a dedicated `:core:audio` Android library module.

## User Review Required

> [!IMPORTANT]
> - We will create a new Gradle subproject `:core:audio` and wire it up in `settings.gradle.kts`.
> - The `:app` module will depend on `:core:audio`.
> - Package names inside `:core:audio` can remain `com.mmt.guitarlab.audio` and `com.mmt.guitarlab.domain.audio` or be adapted as appropriate, but keeping package names or adjusting imports in `:app` will be handled automatically.

## Open Questions

- None.

## Proposed Changes

### Gradle Configuration
#### [MODIFY] [settings.gradle.kts](file:///Users/mamontov/StudioProjects/copilot-worktrees/GuitarLab/yattzy54-effective-journey/settings.gradle.kts)
- Add `:core:audio` subproject.

#### [NEW] [build.gradle.kts](file:///Users/mamontov/StudioProjects/copilot-worktrees/GuitarLab/yattzy54-effective-journey/core/audio/build.gradle.kts)
- Create build.gradle.kts for `:core:audio` (Android library, AndroidX, Hilt if needed, coroutines).

#### [MODIFY] [app/build.gradle.kts](file:///Users/mamontov/StudioProjects/copilot-worktrees/GuitarLab/yattzy54-effective-journey/app/build.gradle.kts)
- Add `implementation(project(":core:audio"))`.

### Audio Module Files (`:core:audio`)
Create files under `core/audio/src/main/java/com/mmt/guitarlab/`:
- `audio/AudioFocusHandler.kt`
- `audio/drums/AudioTrackDrumEngine.kt`, `DrumSoundSynthesizer.kt`
- `audio/metronome/AudioTrackMetronomeEngine.kt`, `ClickSynthesizer.kt`
- `audio/midi/MidiExporter.kt`
- `audio/recorder/RiffRecorderEngine.kt`
- `audio/slowdown/AudioSlowDownerEngine.kt`
- `audio/tab/TabPlaybackEngine.kt`
- `audio/tuner/AudioRecordTunerEngine.kt`, `YinPitchDetector.kt`
- `domain/audio/DrumEngine.kt`, `MetronomeEngine.kt`, `TunerEngine.kt`

(And remove them from `:app`).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleStandardDebug` and `./gradlew :core:audio:assembleDebug` to verify successful compilation.

### Manual Verification
- Test audio playback, metronome, drums, tuner, and tab playback in the app.
