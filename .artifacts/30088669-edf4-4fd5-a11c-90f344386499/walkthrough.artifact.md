# Walkthrough - Unified Error Handling (`AppError`)

## Changes Made

### 1. Domain Error Hierarchy (`core:model`)
- **[AppError.kt](file:///Users/mamontov/StudioProjects/copilot-worktrees/GuitarLab/yattzy54-effective-journey/core/model/src/main/java/com/mmt/guitarlab/domain/model/AppError.kt)**: Created a unified `sealed interface AppError` defining standard application error types:
  - `NetworkError`
  - `DatabaseError`
  - `ParseError`
  - `AudioError`
  - `UnknownError`

### 2. Unit Testing Error Models (`core:model`)
- **[AppErrorTest.kt](file:///Users/mamontov/StudioProjects/copilot-worktrees/GuitarLab/yattzy54-effective-journey/core/model/src/test/java/com/mmt/guitarlab/domain/model/AppErrorTest.kt)**: Added thorough unit tests verifying all variants of `AppError` and their associated messages/throwables.

## Verification Results
- Successfully ran unit tests for `core:model` (`core:model:testDebugUnitTest`), all **8 tests passed successfully** (3 tap tempo tests + 5 error handling tests).
