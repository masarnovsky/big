# Android Instrumented Tests

This directory contains Android instrumented tests that run on an Android device or emulator. These tests require the Android framework and are used for integration testing and UI testing.

## Test Coverage

### Database Integration Tests (`TextDaoTest.kt`)
- Insert operations
- Query operations with Flow
- Delete operations
- Timestamp auto-generation
- ID auto-generation
- Ordering by timestamp (descending)
- Special character support
- Unicode character support
- Empty text handling
- Stress tests (bulk operations)
- Reactive updates via Flow

### UI Tests (`MainActivityTest.kt`)
- Main screen layout and components
- Text input functionality
- Character count display
- Font selection UI
- Background selection UI
- Orientation selection UI
- Preview button enabled/disabled states
- History section display
- Input validation
- Special character input
- Unicode character input
- Max length enforcement
- Complete user flow testing
- Accessibility features

## Running the Tests

### Prerequisites
- Connected Android device or running emulator
- USB debugging enabled (for physical device)
- Minimum SDK 23 (Android 6.0)

### From Android Studio
1. Right-click on the `androidTest` directory
2. Select "Run 'All Tests'"

### From Command Line
```bash
# Run all instrumented tests
./gradlew connectedAndroidTest

# Run on specific device
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.deviceName=<device_id>

# Run specific test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.masarnovsky.big.mvvm.model.TextDaoTest
```

### Generate Test Reports
After running tests, HTML reports are available at:
```
app/build/reports/androidTests/connected/index.html
```

## Test Database
- Tests use in-memory Room database
- Database is created fresh for each test
- `allowMainThreadQueries()` is enabled for testing only
- Database is closed after each test to prevent leaks

## UI Testing Framework
- Jetpack Compose Testing
- AndroidX Test libraries
- Espresso for UI interactions
- Truth for assertions

## Test Execution Time
- Database tests: ~5-10 seconds
- UI tests: ~15-30 seconds
- Total: ~30-40 seconds

## Best Practices
- Tests clean up after themselves
- Each test is independent
- Tests use in-memory database
- UI tests wait for idle state
- Tests handle async operations properly with Turbine

## Troubleshooting

### Tests fail with "No connected devices"
- Ensure device/emulator is connected: `adb devices`
- Start emulator or connect physical device

### Tests timeout
- Increase timeout in gradle.properties: `android.testInstrumentationRunnerArguments.timeout_msec=300000`
- Check device performance

### UI tests fail intermittently
- Tests may need to wait for animations
- Use `composeTestRule.waitForIdle()`
- Ensure device has sufficient resources

### Database tests fail
- Check Room dependencies are correct
- Verify database schema matches entities
- Clear app data between test runs
