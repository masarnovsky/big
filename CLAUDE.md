# CLAUDE.md - AI Assistant Command Reference

> **Last Updated:** 2025-11-16
> **App Version:** 1.1
> **Package:** `com.masarnovsky.big`
> **Target SDK:** 36 (Android 15.0)

Quick reference guide for AI assistants working with the BIG Android application.

---

## Project Overview

**BIG** is a Kotlin + Jetpack Compose Android app for displaying text fullscreen with customizable styling (fonts, colors, orientation).

**Architecture:** MVVM (ViewModel → Repository → Room/DataStore)
**Stack:** Kotlin 2.0.21, Compose, Room 2.8.2, DataStore, Coroutines

---

## Quick Commands

### Version Update

Edit `app/build.gradle.kts`:
```kotlin
versionCode = 2        // Increment
versionName = "1.2"    // Update
```

---

## Testing Commands

### Run Tests

```bash
# Unit tests (JUnit 4)
./gradlew test

# Instrumented tests (Espresso + Compose)
./gradlew connectedAndroidTest

# Test with coverage
./gradlew testDebugUnitTest --tests "com.masarnovsky.big.*"

# Run specific test class
./gradlew test --tests "MainViewModelTest"
```

### Test File Creation

**Unit Test Template** (`app/src/test/java/com/masarnovsky/big/viewmodel/MainViewModelTest.kt`):

```kotlin
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import kotlinx.coroutines.test.*

class MainViewModelTest {
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        // Initialize mocks/test data
    }

    @Test
    fun `updateInputText should limit to 200 characters`() {
        // Test input validation
        viewModel.updateInputText("a".repeat(250))
        assertEquals(200, viewModel.inputText.value.length)
    }

    @Test
    fun `selectedFont should persist to DataStore`() = runTest {
        // Test preference persistence
    }
}
```

**Compose UI Test Template** (`app/src/androidTest/java/com/masarnovsky/big/MainActivityTest.kt`):

```kotlin
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testTextInputDisplayed() {
        composeTestRule.onNodeWithText("Enter text").assertIsDisplayed()
    }

    @Test
    fun testFontSelectionChangesState() {
        composeTestRule.onNodeWithContentDescription("Select Montserrat").performClick()
        // Verify font changed
    }
}
```

### Key Test Areas

**ViewModel Tests** (`MainViewModel.kt`):
- ✓ Input text validation (200-char limit)
- ✓ State updates (font, background, orientation)
- ✓ History operations (delete, clear)
- ✓ Preference persistence to DataStore

**Repository Tests** (`TextRepository.kt`):
- ✓ Database insert/delete operations
- ✓ Flow emissions from Room
- ✓ Error handling with try-catch
- ✓ History retrieval

**Composable Tests** (UI components):
- ✓ Text input rendering
- ✓ Font selector interactions
- ✓ Background selector state changes
- ✓ History list display

**Integration Tests**:
- ✓ End-to-end user flow (input → fullscreen display)
- ✓ Intent data passing to FullscreenActivity
- ✓ Orientation changes
- ✓ Database persistence across app restarts

---

## Debugging Commands

### ADB Commands

```bash
# Clear app data (reset database/preferences)
adb shell pm clear com.masarnovsky.big

# Launch main activity
adb shell am start -n com.masarnovsky.big/.mvvm.view.MainActivity

# Launch fullscreen activity with test data
adb shell am start -n com.masarnovsky.big/.mvvm.view.FullscreenActivity \
    -e EXTRA_TEXT "Test Message" \
    -e EXTRA_FONT "MONTSERRAT" \
    -e EXTRA_BACKGROUND "BLACK" \
    -e EXTRA_ORIENTATION "PORTRAIT"

# View app logs (filter by tag)
adb logcat | grep "BIG"
adb logcat MainActivity:D TextRepository:E *:S

# Pull Room database for inspection
adb shell run-as com.masarnovsky.big cp /data/data/com.masarnovsky.big/databases/text_database /sdcard/
adb pull /sdcard/text_database

# Pull DataStore preferences
adb shell run-as com.masarnovsky.big cp /data/data/com.masarnovsky.big/files/datastore/user_preferences.preferences_pb /sdcard/
adb pull /sdcard/user_preferences.preferences_pb

# Monitor app crashes
adb logcat | grep AndroidRuntime

# Check current activity
adb shell dumpsys activity activities | grep mResumedActivity
```

### Logcat Filters

```bash
# View only errors
adb logcat *:E

# View specific class logs
adb logcat MainViewModel:D *:S

# View crashes and errors
adb logcat AndroidRuntime:E *:S
```

---

## Code Patterns

### State Management Pattern

```kotlin
// ViewModel pattern (always use this)
private val _inputText = MutableStateFlow("")
val inputText: StateFlow<String> = _inputText.asStateFlow()

fun updateInputText(text: String) {
    _inputText.value = text.take(Defaults.inputTextMaxAmount)
    viewModelScope.launch {
        // Persist if needed
    }
}
```

### Error Handling Pattern

```kotlin
// Repository pattern (always wrap DB operations)
suspend fun insertText(entity: TextEntity) {
    try {
        textDao.insert(entity)
    } catch (e: Exception) {
        Log.e(TAG, "Error inserting text", e)
    }
}

// Flow pattern (always use .catch())
fun getAllTexts(): Flow<List<TextEntity>> {
    return textDao.getAllTexts()
        .catch { e ->
            Log.e(TAG, "Error fetching texts", e)
            emit(emptyList())
        }
}
```

---

## Testing Setup

### Add Test Dependencies (already configured)

`app/build.gradle.kts` includes:
- `testImplementation(libs.junit)` - Unit tests
- `androidTestImplementation(libs.androidx.junit)` - Instrumented tests
- `androidTestImplementation(libs.androidx.espresso.core)` - UI tests
- `androidTestImplementation(libs.androidx.compose.ui.test.junit4)` - Compose tests

### Create Test Files

**Unit Test Location:** `app/src/test/java/com/masarnovsky/big/`
**Instrumented Test Location:** `app/src/androidTest/java/com/masarnovsky/big/`

### Mock/Test Utilities

```kotlin
// Coroutine test dispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest

// Compose test rule
import androidx.compose.ui.test.junit4.createComposeRule
```

---

## Known Issues & TODOs

1. **No Dependency Injection** - Manual repository instantiation (consider Hilt)
2. **Minimal Test Coverage** - Only placeholder tests exist
3. **No LazyColumn** - History uses Column (performance issue for large lists)
4. **No ProGuard Rules** - Using defaults (may need custom rules)
5. **Silent Errors** - No user-facing error messages for DB failures

---

## Database Inspection

### Room Database Schema

**Table:** `text_history`
**Columns:** `id (PRIMARY KEY)`, `text`, `timestamp`, `font`, `backgroundColor`, `orientation`

### Query Database via ADB

```bash
# Pull database
adb exec-out run-as com.masarnovsky.big cat databases/text_database > /tmp/big.db

# Open with sqlite3
sqlite3 /tmp/big.db
sqlite> .tables
sqlite> SELECT * FROM text_history;
sqlite> .schema text_history
```

---

## Performance Testing

```bash
# Profile app performance
adb shell am start -n com.masarnovsky.big/.mvvm.view.MainActivity --start-profiler profile.trace

# Dump memory info
adb shell dumpsys meminfo com.masarnovsky.big

# Monitor CPU usage
adb shell top | grep masarnovsky
```

---

## CI/CD Setup (Recommended)

### GitHub Actions Test Workflow

Create `.github/workflows/test.yml`:

```yaml
name: Android Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '11'
      - name: Run Unit Tests
        run: ./gradlew test
      - name: Run Instrumented Tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 29
          script: ./gradlew connectedAndroidTest
```

---

## Quick Reference

### MVVM Layer Responsibilities

- **View (Composables):** UI rendering, user interactions → call ViewModel functions
- **ViewModel:** State management, business logic → call Repository
- **Repository:** Data abstraction → call DAO/DataStore
- **DAO/DataStore:** Database/preferences access

### Never Do This

- ❌ Access database from View/Composables directly
- ❌ Put business logic in Composables
- ❌ Skip error handling in Repository
- ❌ Forget to limit coroutines to viewModelScope
- ❌ Hardcode strings (use strings.xml)

### Always Do This

- ✓ Read files before editing
- ✓ Follow MVVM architecture strictly
- ✓ Use StateFlow for ViewModel state
- ✓ Wrap DB operations in try-catch
- ✓ Add tests for new features
- ✓ Use `collectAsState()` in Composables
- ✓ Log errors with `Log.e(TAG, message, exception)`

---

## Resources

- **Compose Docs:** https://developer.android.com/jetpack/compose
- **Room Docs:** https://developer.android.com/training/data-storage/room
- **DataStore Docs:** https://developer.android.com/topic/libraries/architecture/datastore
- **Testing Compose:** https://developer.android.com/jetpack/compose/testing

---

**Document Version:** 2.0 (Streamlined)
**Focus:** Testing & Commands
**Repository:** https://github.com/masarnovsky/big
