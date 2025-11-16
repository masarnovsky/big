# CLAUDE.md - AI Assistant Guide for BIG Android App

> **Last Updated:** 2025-11-16
> **App Version:** 1.1
> **Target SDK:** 36 (Android 15.0)

This document provides comprehensive guidance for AI assistants working with the BIG Android application codebase.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Architecture & Design Patterns](#architecture--design-patterns)
4. [Codebase Structure](#codebase-structure)
5. [Development Conventions](#development-conventions)
6. [Key Components Guide](#key-components-guide)
7. [Data Flow & State Management](#data-flow--state-management)
8. [Build & Release Process](#build--release-process)
9. [Testing Strategy](#testing-strategy)
10. [Common Development Tasks](#common-development-tasks)
11. [Known Issues & Technical Debt](#known-issues--technical-debt)
12. [Best Practices for AI Assistants](#best-practices-for-ai-assistants)

---

## Project Overview

**BIG** is an Android mobile application designed for displaying text in fullscreen with customizable styling. The app's core purpose is to show important text messages "BIG" so everyone can see them clearly.

### Key Features
- Text input with 200-character limit
- Fullscreen display mode with customizable styling
- Display history with local persistence
- 4 custom fonts (Montserrat, Pangolin, Roboto, Playfair Display)
- Multiple background colors and gradient options
- Portrait/Landscape orientation support
- No network connectivity required (local-first architecture)

### Application Info
- **Package Name:** `com.masarnovsky.big`
- **App Name:** BIG
- **Version Code:** 1
- **Version Name:** 1.1
- **Min SDK:** 23 (Android 6.0 Marshmallow)
- **Target SDK:** 36 (Android 15.0)

---

## Technology Stack

### Core Technologies
- **Language:** Kotlin 2.0.21 (100% Kotlin codebase)
- **UI Framework:** Jetpack Compose (2025.10.00 BOM)
- **Build System:** Gradle 8.13.0 with Kotlin DSL
- **JVM Target:** Java 11

### Key Libraries

| Category | Library | Purpose |
|----------|---------|---------|
| **UI** | Jetpack Compose | Declarative UI framework |
| **UI** | Material Design 3 | UI components and theming |
| **Database** | Room 2.8.2 | Local data persistence |
| **Preferences** | DataStore | Reactive settings storage |
| **Lifecycle** | AndroidX Lifecycle 2.9.4 | ViewModel and lifecycle management |
| **Async** | Kotlin Coroutines | Asynchronous programming |
| **Annotation** | KSP 2.0.21-1.0.28 | Kotlin Symbol Processing |
| **Testing** | JUnit 4.13.2 | Unit testing |
| **Testing** | Espresso 3.7.0 | UI testing |

### Dependency Management
- **Version Catalog:** All dependencies managed via `gradle/libs.versions.toml`
- **Repositories:** Google Maven + Maven Central
- **Build Type:** Strict repository mode (no transitive repositories)

---

## Architecture & Design Patterns

### MVVM Architecture

The app follows the **Model-View-ViewModel (MVVM)** pattern with a repository layer:

```
┌─────────────────────────────────────────────┐
│  View Layer (Composables + Activities)      │
│  - MainActivity.kt                          │
│  - FullscreenActivity.kt                    │
│  - components/*.kt                          │
└───────────────┬─────────────────────────────┘
                │ observes StateFlow
┌───────────────▼─────────────────────────────┐
│  ViewModel Layer                            │
│  - MainViewModel.kt                         │
│  - Manages UI state via StateFlow           │
│  - Business logic coordination              │
└───────────────┬─────────────────────────────┘
                │ calls
┌───────────────▼─────────────────────────────┐
│  Repository Layer                           │
│  - TextRepository.kt                        │
│  - UserPreferencesManager.kt                │
│  - Data access abstraction                  │
└───────────────┬─────────────────────────────┘
                │ uses
┌───────────────▼─────────────────────────────┐
│  Data Layer                                 │
│  - Room Database (TextDatabase.kt)          │
│  - DataStore (preferences)                  │
│  - DAOs (TextDao.kt)                        │
└─────────────────────────────────────────────┘
```

### Key Patterns

1. **Repository Pattern:** Abstracts data sources (Room + DataStore)
2. **Singleton Pattern:** Database instance (thread-safe lazy initialization)
3. **State Hoisting:** Compose state lifted to ViewModel
4. **Unidirectional Data Flow:** UI events → ViewModel → Repository → Data Layer
5. **Reactive Streams:** Kotlin Flow for reactive data propagation

---

## Codebase Structure

### Directory Tree

```
app/src/main/java/com/masarnovsky/big/
├── mvvm/
│   ├── Enums.kt                           # App enums (BackgroundColor, InputFont, etc.)
│   ├── Utils.kt                           # Utility functions (color mapping, gradients)
│   ├── model/                             # Data layer
│   │   ├── TextEntity.kt                  # Room entity for text history
│   │   ├── TextDao.kt                     # Database access object
│   │   ├── TextDatabase.kt                # Room database configuration
│   │   ├── TextRepository.kt              # Repository implementation
│   │   └── UserPreferencesManager.kt      # DataStore preferences
│   ├── view/                              # UI layer
│   │   ├── MainActivity.kt                # Main entry point activity
│   │   ├── FullscreenActivity.kt          # Fullscreen display activity
│   │   └── components/                    # Reusable Composables
│   │       ├── AutoSizeTextOptimized.kt   # Text sizing algorithm (binary search)
│   │       ├── FullscreenTextScreen.kt    # Fullscreen display component
│   │       ├── FontSelector.kt            # Font selection UI
│   │       ├── BackgroundSelector.kt      # Background/color picker
│   │       ├── OrientationSelector.kt     # Orientation toggle
│   │       ├── HistoryItem.kt             # History list item
│   │       ├── PreviewButton.kt           # Preview/display button
│   │       └── AppVersion.kt              # Version display
│   └── viewmodel/
│       ├── MainViewModel.kt               # Main state management
│       └── Defaults.kt                    # App-wide constants
└── ui/
    └── theme/
        └── Theme.kt                       # Material3 theming (monochrome light)
```

### Important Configuration Files

| File | Purpose |
|------|---------|
| `build.gradle.kts` (root) | Root build configuration, plugin management |
| `app/build.gradle.kts` | App module config, dependencies, APK naming |
| `settings.gradle.kts` | Project structure, module inclusion |
| `gradle.properties` | JVM arguments, Kotlin style, AndroidX config |
| `gradle/libs.versions.toml` | Centralized dependency versions |
| `app/src/main/AndroidManifest.xml` | App permissions, activities, metadata |
| `release.sh` | Release automation script |

---

## Development Conventions

### Code Style

1. **Kotlin Official Style:** Enforced via `gradle.properties`
2. **Naming Conventions:**
   - **Classes:** PascalCase (`MainViewModel`, `TextEntity`)
   - **Functions:** camelCase (`getUserPreferences`, `insertText`)
   - **Private State:** Underscore prefix (`_inputText`, `_history`)
   - **Public State:** No underscore (`inputText`, `history`)
   - **Constants:** UPPER_SNAKE_CASE in companion objects
3. **Package Organization:** Feature-based grouping (mvvm.view, mvvm.model, mvvm.viewmodel)

### State Management Conventions

```kotlin
// PATTERN: Private mutable state + Public read-only state
private val _inputText = MutableStateFlow("")
val inputText: StateFlow<String> = _inputText.asStateFlow()

// PATTERN: ViewModel uses viewModelScope for coroutines
fun saveText() {
    viewModelScope.launch {
        repository.insertText(text)
    }
}

// PATTERN: Repository uses .catch() for error handling
fun getHistory(): Flow<List<TextEntity>> {
    return textDao.getAllTexts()
        .catch { e ->
            Log.e(TAG, "Error fetching history", e)
            emit(emptyList())
        }
}
```

### Error Handling

1. **Try-Catch:** Used in repository layer for database operations
2. **Flow .catch():** Error handling in reactive streams
3. **Logging:** `android.util.Log` with class-level TAG constants
4. **Graceful Fallbacks:** Default values for parsing errors (enums, preferences)

### Resource Naming

- **Layouts:** Not applicable (Compose-based)
- **Strings:** `strings.xml` (minimal usage)
- **Colors:** Defined in `ui/theme/Theme.kt` (not colors.xml)
- **Fonts:** Stored in `res/font/` as `.ttf` files

---

## Key Components Guide

### 1. MainViewModel (`mvvm/viewmodel/MainViewModel.kt`)

**Purpose:** Central state management for the entire app

**Key Responsibilities:**
- Manages input text, font, background, orientation settings
- Coordinates text history loading/deletion
- Persists user preferences via DataStore
- Provides state to UI via StateFlow

**Key State Properties:**
```kotlin
val inputText: StateFlow<String>              // Current input text
val selectedFont: StateFlow<InputFont>        // Selected font
val selectedBackground: StateFlow<BackgroundColor>  // Background selection
val selectedOrientation: StateFlow<Orientation>     // Screen orientation
val history: StateFlow<List<TextEntity>>      // Text history
```

**Key Functions:**
- `updateInputText(text: String)` - Update input with 200-char limit
- `updateSelectedFont(font: InputFont)` - Change font and persist
- `updateSelectedBackground(bg: BackgroundColor)` - Change background and persist
- `deleteHistoryItem(item: TextEntity)` - Remove history entry
- `clearHistory()` - Clear all history

### 2. TextRepository (`mvvm/model/TextRepository.kt`)

**Purpose:** Abstraction layer for Room database access

**Key Functions:**
- `insertText(text, font, background, orientation)` - Save to history
- `getAllTexts(): Flow<List<TextEntity>>` - Get all history entries
- `deleteText(entity)` - Delete specific entry
- `clearHistory()` - Delete all entries

**Error Handling:** All operations wrapped in try-catch with logging

### 3. AutoSizeTextOptimized (`mvvm/view/components/AutoSizeTextOptimized.kt`)

**Purpose:** Advanced text sizing algorithm using binary search

**Algorithm:**
1. Binary search between 1sp and maxFontSize to find optimal size
2. Measures text with `Paragraph` API
3. Checks if text fits within available dimensions
4. Optimizes for both portrait and landscape modes

**Performance:** O(log n) complexity for font size calculation

### 4. FullscreenActivity (`mvvm/view/FullscreenActivity.kt`)

**Purpose:** Displays text in fullscreen mode

**Features:**
- Immersive mode (hides system bars)
- Supports portrait/landscape locking
- Receives text, font, background, orientation via Intent extras
- Applies gradient or solid backgrounds
- Uses AutoSizeTextOptimized for text rendering

**Intent Extras:**
- `EXTRA_TEXT`: String - Text to display
- `EXTRA_FONT`: String - Font enum name
- `EXTRA_BACKGROUND`: String - Background enum name
- `EXTRA_ORIENTATION`: String - Orientation enum name

---

## Data Flow & State Management

### State Flow Diagram

```
User Action (Composable)
    ↓
ViewModel Function Call
    ↓
Update MutableStateFlow
    ↓
Persist to DataStore/Room (if needed)
    ↓
StateFlow emits new value
    ↓
Composable recomposes with new state
```

### Example: Changing Font

```kotlin
// 1. User clicks font in FontSelector
FontSelector(
    selectedFont = viewModel.selectedFont.collectAsState().value,
    onFontSelected = { font -> viewModel.updateSelectedFont(font) }
)

// 2. ViewModel updates state and persists
fun updateSelectedFont(font: InputFont) {
    _selectedFont.value = font
    viewModelScope.launch {
        userPreferencesManager.saveInputFont(font)
    }
}

// 3. DataStore persists the change
suspend fun saveInputFont(font: InputFont) {
    dataStore.edit { preferences ->
        preferences[INPUT_FONT] = font.name
    }
}

// 4. State flow emits, UI recomposes automatically
```

### Data Persistence

**Room Database:**
- **Table:** `text_history`
- **Columns:** id, text, timestamp, font, backgroundColor, orientation
- **Auto-Migration:** Single database version (1)
- **Thread Safety:** Singleton pattern with synchronized initialization

**DataStore:**
- **File:** `user_preferences`
- **Keys:** INPUT_FONT, BACKGROUND_COLOR, ORIENTATION
- **Access:** Reactive Flow-based API

---

## Build & Release Process

### Build Configuration

**APK Naming Convention:**
```kotlin
setProperty("archivesBaseName", "BIG-v${defaultConfig.versionName}")
// Output: BIG-v1.1-release.apk
```

**Build Types:**
- **Debug:** Standard build with debugging enabled
- **Release:**
  - ProGuard minification enabled
  - Resource shrinking enabled
  - Optimized for production

### Release Script (`release.sh`)

**Automated Release Steps:**
```bash
#!/bin/bash
# 1. Clean download folder
rm -rf docs/download/*

# 2. Find APK in build folder
find app/build/outputs/release -name '*.apk'

# 3. Rename to BIG.apk
mv <found-apk> docs/download/BIG.apk

# 4. Stage for git commit
# (Manual commit required)
```

**Usage:**
```bash
./gradlew assembleRelease
./release.sh
git add docs/download/BIG.apk
git commit -m "Release v1.1"
```

### Version Management

**Update Version:**
1. Open `app/build.gradle.kts`
2. Update `versionCode` (increment by 1)
3. Update `versionName` (semantic versioning: X.Y.Z)
4. Build and test
5. Run release script

---

## Testing Strategy

### Current Test Coverage

**Unit Tests:**
- Location: `app/src/test/java/com/masarnovsky/big/`
- Status: Minimal (only `ExampleUnitTest.kt` placeholder)
- Framework: JUnit 4

**Instrumented Tests:**
- Location: `app/src/androidTest/`
- Status: Not implemented
- Framework: Espresso + Compose Testing

### Testing Infrastructure (Ready to Use)

```gradle
testImplementation(libs.junit)
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
androidTestImplementation(libs.androidx.compose.ui.test.junit4)
debugImplementation(libs.androidx.compose.ui.tooling)
debugImplementation(libs.androidx.compose.ui.test.manifest)
```

### Recommended Test Coverage Areas

**ViewModel Tests:**
```kotlin
// Test state updates
// Test preference persistence
// Test history operations
// Test input validation (200-char limit)
```

**Repository Tests:**
```kotlin
// Test database operations
// Test error handling
// Test Flow emissions
```

**Composable Tests:**
```kotlin
// Test UI rendering
// Test user interactions
// Test state changes
```

**Integration Tests:**
```kotlin
// Test end-to-end user flows
// Test fullscreen activity launch
// Test orientation changes
```

---

## Common Development Tasks

### 1. Adding a New Background Color

**Steps:**
1. Add enum value to `BackgroundColor` in `mvvm/Enums.kt`
2. Add color mapping in `getBackgroundColor()` in `mvvm/Utils.kt`
3. Add text color mapping in `getTextColor()` in `mvvm/Utils.kt`
4. Update `BackgroundSelector` UI to include new color option

**Example:**
```kotlin
// 1. Enums.kt
enum class BackgroundColor {
    WHITE, BLACK, RED, BLUE, YELLOW, GREEN, RANDOM, CUSTOM_PURPLE  // New
}

// 2. Utils.kt
fun getBackgroundColor(background: BackgroundColor): Color {
    return when (background) {
        // ... existing colors
        BackgroundColor.CUSTOM_PURPLE -> Color(0xFF9C27B0)
    }
}

// 3. BackgroundSelector.kt
// Add button/option for CUSTOM_PURPLE
```

### 2. Adding a New Font

**Steps:**
1. Add `.ttf` font file to `app/src/main/res/font/`
2. Add enum value to `InputFont` in `mvvm/Enums.kt`
3. Update font resource mapping in `FontSelector.kt`

**Example:**
```kotlin
// 1. Add font: res/font/comic_sans.ttf

// 2. Enums.kt
enum class InputFont {
    MONTSERRAT, PANGOLIN, ROBOTO, PLAYFAIR_DISPLAY, COMIC_SANS  // New
}

// 3. FontSelector.kt
val fontResource = when (selectedFont) {
    InputFont.MONTSERRAT -> R.font.montserrat_bold
    InputFont.PANGOLIN -> R.font.pangolin_regular
    InputFont.ROBOTO -> R.font.roboto_bold
    InputFont.PLAYFAIR_DISPLAY -> R.font.playfair_display_bold
    InputFont.COMIC_SANS -> R.font.comic_sans  // New
}
```

### 3. Changing Input Character Limit

**Steps:**
1. Update `inputTextMaxAmount` in `mvvm/viewmodel/Defaults.kt`
2. Limit is automatically enforced in `MainViewModel.updateInputText()`

**Example:**
```kotlin
// Defaults.kt
object Defaults {
    const val inputTextMaxAmount = 300  // Changed from 200
}
```

### 4. Adding a New Preference

**Steps:**
1. Add DataStore key in `UserPreferencesManager.kt`
2. Add save/load functions in `UserPreferencesManager.kt`
3. Add StateFlow in `MainViewModel.kt`
4. Add UI control in relevant Composable

**Example:**
```kotlin
// 1. UserPreferencesManager.kt
private val FONT_SIZE = intPreferencesKey("font_size")

suspend fun saveFontSize(size: Int) {
    dataStore.edit { it[FONT_SIZE] = size }
}

val fontSizeFlow: Flow<Int> = dataStore.data
    .map { it[FONT_SIZE] ?: 72 }
    .catch { emit(72) }

// 2. MainViewModel.kt
private val _fontSize = MutableStateFlow(72)
val fontSize: StateFlow<Int> = _fontSize.asStateFlow()

init {
    viewModelScope.launch {
        userPreferencesManager.fontSizeFlow.collect {
            _fontSize.value = it
        }
    }
}
```

### 5. Debugging Fullscreen Activity

**Access Intent Data:**
```kotlin
// FullscreenActivity.kt
val text = intent.getStringExtra(EXTRA_TEXT) ?: ""
Log.d(TAG, "Received text: $text")

// Test with:
adb shell am start -n com.masarnovsky.big/.mvvm.view.FullscreenActivity \
    -e EXTRA_TEXT "Test" \
    -e EXTRA_FONT "MONTSERRAT" \
    -e EXTRA_BACKGROUND "BLACK" \
    -e EXTRA_ORIENTATION "PORTRAIT"
```

### 6. Building Release APK

```bash
# Clean build
./gradlew clean

# Build release APK
./gradlew assembleRelease

# Find APK (auto-named)
# Location: app/build/outputs/release/BIG-v1.1-release.apk

# Run release script (optional)
./release.sh
```

---

## Known Issues & Technical Debt

### Current TODO Comments in Code

1. **Dependency Injection Missing:**
   - Location: `MainViewModel.kt`, `FullscreenActivity.kt`
   - Issue: Manual repository/database instantiation
   - Recommendation: Consider Hilt or Koin for DI

2. **Test Coverage:**
   - Only placeholder tests exist
   - No integration or UI tests
   - No ViewModel unit tests

3. **Hardcoded Strings:**
   - Some UI strings not externalized to `strings.xml`
   - Impact: Limited internationalization support

4. **ProGuard Rules:**
   - Not explicitly defined (using default)
   - May need custom rules for Room/DataStore

5. **Error Feedback:**
   - Silent failures in some error cases
   - No user-facing error messages for database failures

### Potential Improvements

1. **Architecture:**
   - Add use case layer between ViewModel and Repository
   - Implement dependency injection (Hilt recommended)

2. **Testing:**
   - Add comprehensive test suite
   - Set up CI/CD for automated testing

3. **UX:**
   - Add loading states
   - Add error snackbars/dialogs
   - Add input validation feedback

4. **Performance:**
   - Add LazyColumn for history (currently using Column)
   - Optimize text sizing algorithm caching

5. **Features:**
   - Add text alignment options
   - Add text shadow/stroke options
   - Add export/share functionality

---

## Best Practices for AI Assistants

### When Modifying Code

1. **Always Read Before Editing:**
   - Use `Read` tool before any `Edit` or `Write` operation
   - Understand existing patterns before making changes

2. **Follow MVVM Architecture:**
   - UI changes → Update Composables
   - State changes → Update ViewModel
   - Data changes → Update Repository/DAO
   - Never bypass layers (e.g., View → Database directly)

3. **Maintain Reactive Patterns:**
   - Use StateFlow for state management
   - Use Flow for database queries
   - Always collect state in Composables with `collectAsState()`

4. **Preserve Error Handling:**
   - Keep try-catch blocks in repository operations
   - Maintain `.catch()` operators in Flow chains
   - Add logging for debugging

5. **Update Tests:**
   - Add tests for new functionality
   - Update existing tests when modifying behavior

6. **Respect Conventions:**
   - Follow Kotlin official style
   - Use existing naming patterns
   - Maintain package structure

### When Adding Features

1. **Check for Related Code:**
   - Search for similar implementations before creating new patterns
   - Reuse existing utilities (e.g., `Utils.kt` for color/gradient functions)

2. **Update All Layers:**
   - Add enum values if needed (`Enums.kt`)
   - Add constants to `Defaults.kt` if applicable
   - Update ViewModels for state management
   - Create/update Composables for UI
   - Add repository methods for data access

3. **Consider Persistence:**
   - Should this setting be persisted? → Add to DataStore
   - Should this data be saved? → Add to Room database

4. **Update Documentation:**
   - Update this CLAUDE.md if architecture changes
   - Add code comments for complex logic
   - Update README.md if user-facing changes

### When Debugging

1. **Check Logs First:**
   - Look for Log.e/Log.d statements in relevant classes
   - Add temporary logging if needed

2. **Verify State Flow:**
   - Check if StateFlow is updated correctly
   - Verify Composable is collecting state
   - Confirm ViewModel function is called

3. **Check Intent Data:**
   - For FullscreenActivity issues, verify Intent extras
   - Log received data for debugging

4. **Database Issues:**
   - Check Room migrations if schema changes
   - Verify DAO queries return expected data
   - Check for null handling in entity mappings

### When Reviewing Code

1. **Architecture Compliance:**
   - Verify MVVM pattern is followed
   - Check for proper layer separation
   - Ensure no business logic in UI layer

2. **Performance:**
   - Check for unnecessary recompositions
   - Verify Flow collectors are properly scoped
   - Look for memory leaks (unclosed resources)

3. **Error Handling:**
   - Verify all async operations have error handling
   - Check for graceful fallbacks
   - Ensure errors are logged

4. **Code Quality:**
   - Check for code duplication
   - Verify naming conventions
   - Ensure proper documentation

---

## Quick Reference

### Key File Locations

| Task | File Path |
|------|-----------|
| Add/modify app state | `mvvm/viewmodel/MainViewModel.kt` |
| Add database entity | `mvvm/model/TextEntity.kt` |
| Add database query | `mvvm/model/TextDao.kt` |
| Add user preference | `mvvm/model/UserPreferencesManager.kt` |
| Add new enum | `mvvm/Enums.kt` |
| Add utility function | `mvvm/Utils.kt` |
| Add/modify main screen | `mvvm/view/MainActivity.kt` |
| Add/modify fullscreen | `mvvm/view/FullscreenActivity.kt` |
| Add UI component | `mvvm/view/components/<ComponentName>.kt` |
| Modify theme | `ui/theme/Theme.kt` |
| Update dependencies | `gradle/libs.versions.toml` |
| Update version | `app/build.gradle.kts` |
| Update constants | `mvvm/viewmodel/Defaults.kt` |

### Common Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Install on device
./gradlew installDebug

# View dependencies
./gradlew app:dependencies

# Release automation
./release.sh
```

### Useful ADB Commands

```bash
# Clear app data
adb shell pm clear com.masarnovsky.big

# Launch main activity
adb shell am start -n com.masarnovsky.big/.mvvm.view.MainActivity

# View app logs
adb logcat | grep "BIG"

# Pull database for inspection
adb shell run-as com.masarnovsky.big cp /data/data/com.masarnovsky.big/databases/text_database /sdcard/
adb pull /sdcard/text_database
```

---

## Summary

This is a **well-architected, modern Android application** using current best practices:

**Strengths:**
- Clean MVVM architecture with clear separation of concerns
- Modern tech stack (Compose, Room, DataStore, Coroutines)
- Reactive state management with Kotlin Flow
- Local-first data architecture (no network dependencies)
- Advanced UI optimization (binary search text sizing)
- Proper error handling and logging

**Areas for Improvement:**
- Implement dependency injection (Hilt/Koin)
- Expand test coverage significantly
- Add user-facing error feedback
- Externalize remaining hardcoded strings
- Consider performance optimizations for history list

**AI Assistant Guidelines:**
- Always maintain MVVM architecture
- Preserve reactive patterns and state management
- Follow existing conventions and code style
- Add comprehensive tests for new features
- Update this documentation when making significant changes

---

**Document Version:** 1.0
**Maintained By:** AI Assistants
**Project Repository:** https://github.com/masarnovsky/big
