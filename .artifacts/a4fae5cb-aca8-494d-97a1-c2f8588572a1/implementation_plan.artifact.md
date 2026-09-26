# Hilt Integration Plan for GuitarLab & TuxGuitar Android

Transition the legacy TuxGuitar Android module (`tuxguitar-android`) and core app components to fully leverage Dagger Hilt dependency injection, replacing manual singleton lookups and context factories where appropriate.

## User Review Required

> [!IMPORTANT]
> The TuxGuitar core framework relies heavily on `TGContext` and `.getInstance(context)` lookups. We will introduce Hilt modules to supply application-scoped components while preserving binary compatibility for legacy action processors and managers.

## Proposed Changes

### app
#### [MODIFY] [GuitarLabApplication.kt](file:///Users/mamontov/StudioProjects/copilot-worktrees/GuitarLab/yattzy54-reimagined-fishstick/app/src/main/java/com/mmt/guitarlab/GuitarLabApplication.kt)
- Ensure `@HiltAndroidApp` is correctly present.

### tuxguitar-android
#### [NEW] [TuxGuitarAppModule.kt](file:///Users/mamontov/StudioProjects/copilot-worktrees/GuitarLab/yattzy54-reimagined-fishstick/third_party/tuxguitar/android/TuxGuitar-android/src/app/tuxguitar/android/di/TuxGuitarAppModule.kt)
- Provide singleton instances for `TGContext`, `TGActionManager`, `TGBrowserManager`, and `TGEditorManager` using Hilt `@Provides` and `@Singleton`.

#### [MODIFY] ViewModels & Fragments
- Annotate ViewModels with `@HiltViewModel` and inject repositories/use-cases via constructor injection where applicable.

## Verification Plan

### Automated Tests
- Build debug targets: `./gradlew :app:assembleDebug`, `:tuxguitar-android:compileDebugKotlin`
